package betsy.bpmn;

import betsy.bpmn.model.BPMNProcess;
import configuration.bpmn.BPMNProcessRepository;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class BpmnLanguageConstructsProperties {

    @Test
    public void testProperties() throws IOException {
        try(InputStream is =  BpmnLanguageConstructsProperties.class.getResourceAsStream("/betsy/common/analytics/additional/BpmnGroups.properties")) {

            Properties properties = new Properties();
            properties.load(is);

            BPMNProcessRepository repository = new BPMNProcessRepository();
            for(BPMNProcess process : repository.getByName("ALL")) {
                Assert.assertNotNull("process " + process.getName() + " has no entry in the properties file", properties.getProperty(process.getName()));
            }

            for(Map.Entry<Object, Object> entry : properties.entrySet()) {
                try {
                    repository.getByName(entry.getKey().toString());
                } catch (IllegalArgumentException e) {
                    Assert.fail(e.getMessage());
                }
            }

        }

    }

}
