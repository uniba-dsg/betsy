package betsy.bpel;

import betsy.bpel.model.BPELProcess;
import betsy.common.model.EngineIndependentProcess;
import configuration.bpel.BPELProcessRepository;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class BpelLanguageConstructsProperties {

    @Test
    public void testProperties() throws IOException {
        try(InputStream is =  BpelLanguageConstructsProperties.class.getResourceAsStream("/betsy/common/analytics/additional/BpelLanguageConstructs.properties")) {

            Properties properties = new Properties();
            properties.load(is);

            BPELProcessRepository repository = BPELProcessRepository.INSTANCE;
            for(EngineIndependentProcess process : repository.getByName("ALL")) {
                Assert.assertNotNull("process " + process.getName() + " has no entry in the properties file", properties.getProperty(process.getName()));
            }

            for(Map.Entry<Object, Object> entry : properties.entrySet()) {
                String featureName = entry.getKey().toString();
                String constructName = entry.getValue().toString();
                try {
                    EngineIndependentProcess engineIndependentProcess = repository.getByName(entry.getKey().toString()).get(0);
                    assertEquals(featureName, engineIndependentProcess.getFeature().getName());
                    assertEquals(constructName, engineIndependentProcess.getConstruct().getName());
                } catch (IllegalArgumentException e) {
                    Assert.fail(e.getMessage());
                }
            }

        }

    }

}
