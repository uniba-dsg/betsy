package betsy.bpmn.engines.jbpm

import ant.tasks.AntUtil
import betsy.common.tasks.FileTasks

import java.nio.file.Path

class JbpmResourcesGenerator {
    private static final AntBuilder ant = AntUtil.builder()

    Path jbpmSrcDir
    Path destDir
    String processName
    String groupId
    String version

    public void generateProject(){
        Path resDir = destDir.resolve("src").resolve("main").resolve("resources")

        //setup directories
        FileTasks.mkdirs(destDir.resolve("src").resolve("main").resolve("java"))
        FileTasks.mkdirs(destDir.resolve("src").resolve("test").resolve("java"))
        FileTasks.mkdirs(destDir.resolve("src").resolve("test").resolve("resources"))

        //copy files
        ant.copy(todir: resDir.resolve("META-INF")){
            fileset(dir: jbpmSrcDir.resolve("META-INF")){ }
        }
        ant.copy(todir: destDir){
            fileset(file: jbpmSrcDir.resolve("project.imports")){ }
        }

        //generate pom.xml
        generatePom()
    }

    private void generatePom(){
        String pomString = """<?xml version="1.0" encoding="UTF-8"?>
<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <modelVersion>4.0.0</modelVersion>
  <groupId>${groupId}</groupId>
  <artifactId>${processName}</artifactId>
  <version>${version}</version>
  <repositories>
    <repository>
      <id>guvnor-m2-repo</id>
      <name>Guvnor M2 Repo</name>
      <url>http://127.0.0.1:8888/maven2/</url>
    </repository>
  </repositories>
</project>
"""
        ant.echo(message: pomString, file: destDir.resolve("pom.xml"))
    }
}
