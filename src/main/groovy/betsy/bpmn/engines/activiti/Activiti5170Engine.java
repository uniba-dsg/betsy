package betsy.bpmn.engines.activiti;

import betsy.common.config.Configuration;
import betsy.common.engines.tomcat.TomcatInstaller;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.tasks.ZipTasks;

import java.nio.file.Path;

/**
 * Activiti 5.17.0
 *
 * @author adbazy
 * @author ckremitzl
 */
public class Activiti5170Engine extends ActivitiEngine {

    @Override
    public String getName() {
        return "activiti5170"; // 5.17.0
    }

    @Override
    public void install() {
        // install tomcat
        TomcatInstaller.v7(getServerPath()).install();

        // unzip activiti
        String filename = "activiti-5.17.0.zip";
        NetworkTasks.downloadFileFromBetsyRepo(filename);
        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(filename), getServerPath());

        // deploy
        getTomcat().deployWar(getServerPath().resolve("activiti-5.17.0").resolve("wars").resolve("activiti-rest.war"));

        String groovyFile = "groovy-all-2.4.1.jar";
        NetworkTasks.downloadFileFromBetsyRepo(groovyFile);
        getTomcat().addLib(Configuration.getDownloadsDir().resolve(groovyFile));

        Path classes = getTomcat().getTomcatWebappsDir().resolve("activiti-rest").resolve("WEB-INF").resolve("classes");
        FileTasks.createFile(classes.resolve("log4j.properties"), "log4j.rootLogger=DEBUG, CA, FILE" + System.lineSeparator() + System.lineSeparator() +
                "# ConsoleAppender" + System.lineSeparator() +
                "log4j.appender.CA=org.apache.log4j.ConsoleAppender" + System.lineSeparator() +
                "log4j.appender.CA.layout=org.apache.log4j.PatternLayout" + System.lineSeparator() +
                "log4j.appender.CA.layout.ConversionPattern= %d{hh:mm:ss,SSS} [%t] %-5p %c %x - %m%n" + System.lineSeparator() + System.lineSeparator() +
                "log4j.appender.FILE=org.apache.log4j.DailyRollingFileAppender" + System.lineSeparator() +
                "log4j.appender.FILE.File=${catalina.base}/logs/activiti.log" + System.lineSeparator() +
                "log4j.appender.FILE.Append=true" + System.lineSeparator() +
                "log4j.appender.FILE.Encoding=UTF-8" + System.lineSeparator() +
                "log4j.appender.FILE.layout=org.apache.log4j.PatternLayout" + System.lineSeparator() +
                "log4j.appender.FILE.layout.ConversionPattern=%d{ABSOLUTE} %-5p [%c{1}] %m%n" + System.lineSeparator());
        FileTasks.replaceTokenInFile(classes.resolve("activiti-custom-context.xml"), "\t\t<property name=\"jobExecutorActivate\" value=\"false\" />", "\t\t<property name=\"jobExecutorActivate\" value=\"true\" />");
        FileTasks.replaceTokenInFile(classes.resolve("activiti-custom-context.xml"),"<!--","");
        FileTasks.replaceTokenInFile(classes.resolve("activiti-custom-context.xml"),"-->","");
    }
}

