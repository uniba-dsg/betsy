package betsy.bpmn.engines.camunda;

import java.time.LocalDate;

import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;
import betsy.common.tasks.FileTasks;

public class Camunda740Engine extends Camunda710Engine {

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPMN, "camunda", "7.4.0", LocalDate.of(2015, 11, 30), "Apache-2.0");
    }

    @Override
    public String getTomcatName() {
        return "apache-tomcat-8.0.24";
    }

    @Override
    public void install() {
        CamundaInstaller camundaInstaller = new CamundaInstaller();
        camundaInstaller.setDestinationDir(getServerPath());
        camundaInstaller.setFileName("camunda-bpm-tomcat-7.4.0.zip");
        camundaInstaller.setTomcatName(getTomcatName());
        camundaInstaller.install();

        // Modify preferences
        FileTasks.replaceTokenInFile(camundaInstaller.getTomcatDestinationDir().resolve("conf").resolve("bpm-platform.xml"),
                "    <job-acquisition name=\"default\" />",
                "    <job-acquisition name=\"default\">\n" + "      <properties>\n"
                        + "        <property name=\"maxWait\">3000</property>\n" + "      </properties>\n"
                        + "    </job-acquisition>");
    }
}
