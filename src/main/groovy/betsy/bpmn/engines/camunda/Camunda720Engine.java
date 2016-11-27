package betsy.bpmn.engines.camunda;

import java.time.LocalDate;

import javax.xml.namespace.QName;

import betsy.bpmn.engines.JsonHelper;
import betsy.common.model.engine.EngineExtended;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import pebl.ProcessLanguage;

public class Camunda720Engine extends Camunda710Engine {

    private static final Logger LOGGER = Logger.getLogger(CamundaEngine.class);

    @Override
    public EngineExtended getEngineObject() {
        return new EngineExtended(ProcessLanguage.BPMN, "camunda", "7.2.0", LocalDate.of(2014, 11, 28), "Apache-2.0");
    }

    @Override
    public String getTomcatName() {
        return "apache-tomcat-7.0.50";
    }

    @Override
    public void install() {
        CamundaInstaller camundaInstaller = new CamundaInstaller();
        camundaInstaller.setDestinationDir(getServerPath());
        camundaInstaller.setFileName("camunda-bpm-tomcat-7.2.0.zip");
        camundaInstaller.setTomcatName(getTomcatName());
        camundaInstaller.install();
    }

    @Override
    public void undeploy(QName process) {
        LOGGER.info("Undeploying process " + process);
        try {
            JSONArray result = JsonHelper.getJsonArray("http://localhost:8080/engine-rest/engine/default" + "/process-definition", 200);
            String id = result.optJSONObject(0).optString("deploymentId");
            JsonHelper.delete("http://localhost:8080/engine-rest/engine/default" + "/deployment/" + id, 204);
        } catch (Exception e) {
            LOGGER.info("undeployment failed", e);
        }
    }

}
