package betsy.common.model.engine;

import java.util.HashMap;
import java.util.Map;

public final class EngineDetails {

    private EngineDetails() {}

    public static Map<String, String> getEngineToURL() {
        Map<String, String> result = new HashMap<>();

        result.put("jbpm", "http://www.jbpm.org/");
        result.put("activiti", "http://www.activiti.org/");
        result.put("camunda", "http://www.camunda.org/");

        result.put("ode", "http://ode.apache.org/");
        result.put("bpelg", "https://code.google.com/archive/p/bpel-g/");
        result.put("openesb", "http://open-esb.net/");
        result.put("orchestra", "http://orchestra.ow2.org/");
        result.put("petalsesb", "http://petals.ow2.org/");
        result.put("wso2", "http://wso2.com/products/business-process-server/");
        result.put("activebpel", "https://sourceforge.net/p/activebpel502/");

        return result;
    }
}
