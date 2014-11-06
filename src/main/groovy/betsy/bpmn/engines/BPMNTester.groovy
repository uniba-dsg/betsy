package betsy.bpmn.engines

import ant.tasks.AntUtil
import betsy.bpmn.model.BPMNAssertions
import betsy.common.tasks.FileTasks
import org.codehaus.groovy.tools.RootLoader

import java.nio.file.Path

public class BPMNTester {

    Path source
    Path target
    Path reportPath

    public void setupPathToToolsJarForJavacAntTask() {
        // required for javac task in Apache Ant
        String javaHome = System.getProperty("java.home");
        if (javaHome.endsWith("jre")) {
            javaHome = javaHome.substring(0, javaHome.length() - 4);
        }
        RootLoader rl = (RootLoader) this.class.classLoader.getRootLoader();

        URL url = new URL("file:///${javaHome}/lib/tools.jar");
        if (rl == null) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            ((URLClassLoader) classLoader).addURL(url);
        } else {
            rl.addURL(url);
        }
    }

    private void executeTest() {
        String systemClasspath = System.getProperty('java.class.path');

        AntBuilder ant = AntUtil.builder();

        ant.junit(printsummary: "on", fork: "true", haltonfailure: "no") {
            classpath {
                pathelement(path: systemClasspath)
                pathelement(location: target)
            }
            formatter(type: "xml")
            batchtest(todir: reportPath) {
                fileset(dir: source) {
                    include(name: "**/*.java")
                }
            }
        }

    }

    private void compileTest() {
        String systemClasspath = System.getProperty('java.class.path');

        AntBuilder ant = AntUtil.builder();

        // compile test sources
        ant.javac(srcdir: source, destdir: target, includeantruntime: false) {
            classpath {
                pathelement(path: systemClasspath)
            }
        }
    }

    public void test() {
        FileTasks.mkdirs(target);
        FileTasks.mkdirs(reportPath);

        setupPathToToolsJarForJavacAntTask()
        compileTest()
        executeTest()
    }
}
