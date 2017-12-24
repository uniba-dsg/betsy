package betsy.bpmn.engines.activiti;

import java.time.LocalDate;
import java.util.Optional;

import betsy.common.model.engine.EngineExtended;
import betsy.common.tasks.FileTasks;
import pebl.ProcessLanguage;

/**
 * Activiti 5.20.0
 *
 */
public class Activiti5220Engine extends ActivitiEngine {

    @Override
    public EngineExtended getEngineObject() {
        return new EngineExtended(ProcessLanguage.BPMN, "activiti", "5.22.0", LocalDate.of(2016, 11, 4), "Apache-2.0");
    }

    @Override
    public void install() {
        ActivitiInstaller installer = new ActivitiInstaller();
        installer.setFileName("activiti-5.22.0.zip");
        installer.setDestinationDir(getServerPath());
        installer.setGroovyFile(Optional.of("groovy-all-2.4.5.jar"));

        installer.install();

        // Modify preferences
        FileTasks.replaceTokenInFile(installer.getClassesPath().resolve("activiti-custom-context.xml"), "\t\t<property name=\"jobExecutorActivate\" value=\"false\" />", "\t\t<property name=\"jobExecutorActivate\" value=\"true\" />");
        FileTasks.replaceTokenInFile(installer.getClassesPath().resolve("activiti-custom-context.xml"),"<!--","");
        FileTasks.replaceTokenInFile(installer.getClassesPath().resolve("activiti-custom-context.xml"),"-->","");
    }
}

