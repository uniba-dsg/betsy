package betsy.bpmn.engines.activiti;

import java.nio.file.Path;
import java.util.Optional;

import betsy.common.config.Configuration;
import betsy.common.engines.tomcat.Tomcat;
import betsy.common.engines.tomcat.TomcatInstaller;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.tasks.ZipTasks;

public class ActivitiInstaller {

    private Path destinationDir;

    private String fileName;

    private Optional<String> groovyFile = Optional.empty();

    public void setDestinationDir(Path destinationDir) {
        this.destinationDir = destinationDir;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setGroovyFile(Optional<String> groovyFile) {
        this.groovyFile = groovyFile;
    }

    public Path getClassesPath() {
        return Tomcat.v7(destinationDir).getTomcatWebappsDir().resolve("activiti-rest").resolve("WEB-INF").resolve("classes");
    }


    public void install() {
        // install tomcat
        TomcatInstaller.v7(destinationDir).install();

        // unzip activiti
        NetworkTasks.downloadFileFromBetsyRepo(fileName);
        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(fileName), destinationDir);

        // deploy
        String activitiFolder = fileName.replace(".zip","");
        Tomcat.v7(destinationDir).deployWar(destinationDir.resolve(activitiFolder).resolve("wars").resolve("activiti-rest.war"));

        if(groovyFile.isPresent()) {
            NetworkTasks.downloadFileFromBetsyRepo(groovyFile.get());
            Tomcat.v7(destinationDir).addLib(Configuration.getDownloadsDir().resolve(groovyFile.get()));
        }

        FileTasks.createFile(getClassesPath().resolve("log4j.properties"), "log4j.rootLogger=DEBUG, CA, FILE\n" +
                "\n" +
                "# ConsoleAppender\n" +
                "log4j.appender.CA=org.apache.log4j.ConsoleAppender\n" +
                "log4j.appender.CA.layout=org.apache.log4j.PatternLayout\n" +
                "log4j.appender.CA.layout.ConversionPattern= %d{hh:mm:ss,SSS} [%t] %-5p %c %x - %m%n\n" +
                "\n" +
                "log4j.appender.FILE=org.apache.log4j.DailyRollingFileAppender\n" +
                "log4j.appender.FILE.File=${catalina.base}/logs/activiti.log\n" +
                "log4j.appender.FILE.Append=true\n" +
                "log4j.appender.FILE.Encoding=UTF-8\n" +
                "log4j.appender.FILE.layout=org.apache.log4j.PatternLayout\n" +
                "log4j.appender.FILE.layout.ConversionPattern=%d{ABSOLUTE} %-5p [%c{1}] %m%n\n");
    }
}
