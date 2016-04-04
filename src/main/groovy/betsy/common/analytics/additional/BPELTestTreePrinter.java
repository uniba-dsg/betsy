package betsy.common.analytics.additional;

import betsy.bpel.model.BPELProcess;
import betsy.bpmn.model.BPMNProcess;
import betsy.common.model.ProcessFolderStructure;
import configuration.bpel.BPELProcessRepository;
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
public class BPELTestTreePrinter {

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
        TestNameToLanguageFeature bpmn = new TestNameToLanguageFeature(BPELTestTreePrinter.class.getResourceAsStream("BpelLanguageConstructs.properties"));

        BPELProcessRepository repository = BPELProcessRepository.INSTANCE;
        List<BPELProcess> processes = repository.getByName("ALL");

        Map<String, Map<String, List<BPELProcess>>> entries = processes.stream().
                collect(Collectors.groupingBy(ProcessFolderStructure::getGroup,
                        Collectors.groupingBy(p -> bpmn.getGroupByTestName(p.getName()))));
        for(Map.Entry<String, Map<String, List<BPELProcess>>> entry : entries.entrySet()) {
            String group = entry.getKey();
            for(Map.Entry<String, List<BPELProcess>> entry2 : entry.getValue().entrySet()) {
                String languageFeature = entry2.getKey();
                for(BPELProcess process : entry2.getValue()) {
                    System.out.println(String.join(", ", group, languageFeature, process.getName()));
                }
            }
        }
    }

}
