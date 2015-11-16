package betsy.bpel;

import betsy.bpel.model.BPELProcess;
import configuration.bpel.BPELProcessRepository;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class BpelLanguageConstructsProperties {

    @Test
    public void testProperties() throws IOException {
        try(InputStream is =  BpelLanguageConstructsProperties.class.getResourceAsStream("/betsy/common/analytics/additional/BpelLanguageConstructs.properties")) {

            Properties properties = new Properties();
            properties.load(is);

            BPELProcessRepository repository = BPELProcessRepository.INSTANCE;
            for(BPELProcess process : repository.getByName("ALL")) {
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
