package betsy.common.analytics.additional;

import betsy.bpmn.model.BPMNProcess;
import betsy.common.model.AbstractProcess;
import betsy.common.model.ProcessFolderStructure;
import configuration.bpmn.BPMNProcessRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Prints out the BPMN test tree (group, language construct, feature)
 */
public class BPMNTestTreePrinter {

    static class TestNameToLanguageFeature {

        private final Properties properties = new Properties();

        public TestNameToLanguageFeature(InputStream stream) throws IOException {
            properties.load(stream);
        }

        public String getGroupByTestName(String testname){
            Object group = properties.get(testname);
            if(group == null){
                throw new IllegalStateException(testname + " has no language feature");
            }
            return group.toString();
        }
    }

    public static void main(String[] args) throws IOException {
        TestNameToLanguageFeature bpmn = new TestNameToLanguageFeature(BPMNTestTreePrinter.class.getResourceAsStream("BpmnLanguageConstructs.properties"));

        BPMNProcessRepository repository = new BPMNProcessRepository();
        List<BPMNProcess> processes = repository.getByName("ALL");

        Map<String, Map<String, List<BPMNProcess>>> entries = processes.stream().
                collect(Collectors.groupingBy(AbstractProcess::getGroup,
                        Collectors.groupingBy(p -> bpmn.getGroupByTestName(p.getName()))));
        for(Map.Entry<String, Map<String, List<BPMNProcess>>> entry : entries.entrySet()) {
            String group = entry.getKey();
            for(Map.Entry<String, List<BPMNProcess>> entry2 : entry.getValue().entrySet()) {
                String languageFeature = entry2.getKey();
                for(BPMNProcess process : entry2.getValue()) {
                    System.out.println(String.join(", ", group, languageFeature, process.getName()));
                }
            }
        }

    }

}
