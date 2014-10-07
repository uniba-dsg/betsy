package betsy.bpmn.engines;

import ant.tasks.AntUtil;
import groovy.util.AntBuilder;
import org.apache.tools.ant.taskdefs.Javac;
import org.codehaus.groovy.tools.RootLoader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

public class BPMNTester {

    public static void setupPathToToolsJarForJavacAntTask(Object object) throws MalformedURLException {
        //setup path to 'tools.jar' for the javac ant task
        String javaHome = System.getProperty("java.home");
        if (javaHome.endsWith("jre")) {
            javaHome = javaHome.substring(0, javaHome.length() - 4);
        }
        RootLoader rl = (RootLoader) object.class.classLoader.getRootLoader();

        URL url = new URL("file:///${javaHome}/lib/tools.jar");
        if (rl == null) {
            Thread.currentThread().getContextClassLoader().addURL(url);
        } else {
            rl.addURL(url);
        }
    }

    public static void writeToLog(Path fileName, String s) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName.toFile(), true));
            bw.append(s);
            bw.newLine();
            bw.close();
        } catch (IOException ignored) {
        }
    }

    public static void executeTest(Path source, Path target, Path reportPath) {
        String systemClasspath = System.getProperty('java.class.path');

        AntBuilder ant = AntUtil.builder();
        //start test
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

    public static void compileTest(Path source, Path target) {
        String systemClasspath = System.getProperty('java.class.path');

        AntBuilder ant = AntUtil.builder();

        // compile test sources
        ant.javac(srcdir: source, destdir: target, includeantruntime: false) {
            classpath {
                pathelement(path: systemClasspath)
            }
        }
    }

}
