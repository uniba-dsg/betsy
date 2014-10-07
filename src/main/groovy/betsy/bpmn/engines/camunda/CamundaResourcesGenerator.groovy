package betsy.bpmn.engines.camunda

import ant.tasks.AntUtil
import betsy.common.config.Configuration
import betsy.common.tasks.FileTasks
import betsy.common.tasks.NetworkTasks
import org.codehaus.groovy.tools.RootLoader

import java.nio.file.Path
import java.nio.file.Paths

class CamundaResourcesGenerator {

    private static final AntBuilder ant = AntUtil.builder()

    Path srcDir
    Path destDir
    String processName
    String groupId
    String version

    void generateWar(){
        //directory structure
        Path pomDir = destDir.resolve("META-INF/maven/${groupId}/${processName}")
        Path classesDir = destDir.resolve("WEB-INF/classes")
        Path srcDestDir = destDir.resolve("../src").normalize()

        //setup infrastructure
        FileTasks.mkdirs(pomDir)
        FileTasks.mkdirs(classesDir)
        FileTasks.mkdirs(srcDestDir)

        //generate pom.properties
        PrintWriter pw = new PrintWriter(pomDir.resolve("pom.properties").toString(), "UTF-8")
        pw.println("version=${version}")
        pw.println("groupId=${groupId}")
        pw.println("artifactId=${processName}")
        pw.close()

        //copy process image
        ant.copy(todir: classesDir){
            fileset(file: srcDir.resolve("${processName}.png"))
        }
        //generate pom
        generatePom(pomDir)
        generateProcessesXml(classesDir)

        //setup path to 'tools.jar' for the javac ant task
        String javaHome = System.getProperty("java.home")
        if(javaHome.endsWith("jre")){
            javaHome = javaHome.substring(0, javaHome.length() - 4)
        }
        RootLoader rl = (RootLoader) this.class.classLoader.getRootLoader()
        if(rl == null){
            Thread.currentThread().getContextClassLoader().addURL(new URL("file:///${javaHome}/lib/tools.jar"))
        }else{
            rl.addURL(new URL("file:///${javaHome}/lib/tools.jar"))
        }

        NetworkTasks.downloadFileFromBetsyRepo("javaee-api-7.0.jar");
        NetworkTasks.downloadFileFromBetsyRepo("camunda-engine-7.0.0-Final.jar");

        // generate and compile sources
        generateServletProcessApplication(srcDestDir)
        ant.javac(srcdir: "${srcDestDir}", destdir: classesDir, includeantruntime: false) {
            classpath{
                pathelement(location: Configuration.getDownloadsDir().resolve("camunda-engine-7.0.0-Final.jar"))
                pathelement(location: Configuration.getDownloadsDir().resolve("javaee-api-7.0.jar"))
            }
        }
        //pack war
        ant.war(destfile: destDir.resolve("${processName}.war"), needxmlfile: false){
            fileset(dir: destDir){ }
        }
    }

    private void generatePom(Path pomDir){
        String pomString = """<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <groupId>${groupId}</groupId>
	<artifactId>${processName}</artifactId>
	<version>${version}</version>
	<packaging>war</packaging>
	<name>${processName}</name>

	<dependencies>
		<dependency>
			<groupId>org.camunda.bpm</groupId>
			<artifactId>camunda-engine</artifactId>
			<version>7.0.0-Final</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>javax.servlet-api</artifactId>
			<version>3.0.1</version>
			<scope>provided</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
					<artifactId>maven-war-plugin</artifactId>
					<version>2.3</version>
					<configuration>
					<failOnMissingWebXml>false</failOnMissingWebXml>
				</configuration>
			</plugin>
		</plugins>
	</build>

	<repositories>
		<repository>
			<id>camunda-bpm-nexus</id>
			<name>camunda-bpm-nexus</name>
			<url>https://app.camunda.com/nexus/content/groups/public</url>
		</repository>
	</repositories>

</project>
"""
        ant.echo(message: pomString, file: pomDir.resolve("pom.xml"))
    }

    private void generateProcessesXml(Path classesDir){
        String processesXmlString = """<?xml version="1.0" encoding="UTF-8" ?>

<process-application
	xmlns="http://www.camunda.org/schema/1.0/ProcessApplication" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

	<process-archive name="${processName}">
		<process-engine>default</process-engine>
		<properties>
			<property name="isDeleteUponUndeploy">true</property>
			<property name="isScanForProcessDefinitions">true</property>
		</properties>
	</process-archive>

</process-application>
"""
        ant.echo(message: processesXmlString, file: classesDir.resolve("META-INF/processes.xml"))
    }

    private void generateServletProcessApplication(Path srcDestDir){
        String fileString = """package ${groupId};

import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.application.impl.ServletProcessApplication;

@ProcessApplication("${processName} Application")
public class ProcessTestApplication extends ServletProcessApplication{
    //empty implementation
}

"""
        ant.echo(message: fileString, file: Paths.get("${srcDestDir}/${groupId.replaceAll('.', '/')}/ProcessTestApplication.java"))
    }
}


