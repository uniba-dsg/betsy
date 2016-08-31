package betsy.bpmn.engines.activiti;

import java.time.LocalDate;
import java.util.Optional;

import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;
import betsy.common.tasks.FileTasks;

/**
 * Activiti 5.18.0
 */
public class Activiti600Beta1Engine extends ActivitiEngine {

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPMN, "activiti", "6.0.0.beta1", LocalDate.of(2015, 9, 2), "Apache-2.0");
    }

    @Override
    public void install() {
        ActivitiInstaller installer = new ActivitiInstaller();
        installer.setFileName("activiti-6.0.0.Beta1.zip");
        installer.setDestinationDir(getServerPath());
        installer.setGroovyFile(Optional.of("groovy-all-2.4.3.jar"));

        installer.install();

        // Modify preferences
        FileTasks.replaceTokenInFile(installer.getClassesPath().resolve("activiti-custom-context.xml"), "\t\t<property name=\"jobExecutorActivate\" value=\"false\" />", "\t\t<property name=\"jobExecutorActivate\" value=\"true\" />");
        FileTasks.replaceTokenInFile(installer.getClassesPath().resolve("activiti-custom-context.xml"), "<!--", "");
        FileTasks.replaceTokenInFile(installer.getClassesPath().resolve("activiti-custom-context.xml"), "-->", "");
    }
}

