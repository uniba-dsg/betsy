package betsy.data.engines.camunda

import ant.tasks.AntUtil
import betsy.tasks.FileTasks
import org.codehaus.groovy.tools.RootLoader

import java.nio.file.Paths

/**
 * Created with IntelliJ IDEA.
 * User: Mathias Casar, Andreas Vorndran
 * Date: 28.02.14
 * Time: 13:50
 */
class CamundaResourcesGenerator {

    private static final AntBuilder ant = AntUtil.builder()

    String srcDir
    String destDir
    String processName
    String groupId
    String version

    void generateWar(){
        //directory structure
        String pomDir = "${destDir}/META-INF/maven/${groupId}/${processName}"
        String classesDir = "${destDir}/WEB-INF/classes"

        //setup infrastructure
        FileTasks.mkdirs(Paths.get(pomDir))
        FileTasks.mkdirs(Paths.get(classesDir))

        //generate pom.properties
        PrintWriter pw = new PrintWriter("${pomDir}/pom.properties", "UTF-8")
        pw.println("version=0.0.1-SNAPSHOT")
        pw.println("groupId=${groupId}")
        pw.println("artifactId=${processName}")
        pw.close()

        //copy process specific files
        ant.copy(todir: classesDir){
            fileset(dir: "${srcDir}/process"){ }
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

        // compile sources
        ant.javac(srcdir: "${srcDir}/src", destdir: classesDir, includeantruntime: false) {
            classpath{
                pathelement(location: "bpmnRes/camunda/camunda-engine-7.0.0-Final.jar")
                pathelement(location: "bpmnRes/camunda/javaee-api-7.0.jar")
            }
        }
        //pack war
        ant.war(destfile: "${destDir}/${processName}.war", needxmlfile: false){
            fileset(dir: destDir){ }
        }
    }

    private void generatePom(String pomDir){
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
        ant.echo(message: pomString, file: Paths.get("${pomDir}/pom.xml"))
    }

    private void generateProcessesXml(String classesDir){
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
        ant.echo(message: processesXmlString, file: Paths.get("${classesDir}/META-INF/processes.xml"))
    }

}


