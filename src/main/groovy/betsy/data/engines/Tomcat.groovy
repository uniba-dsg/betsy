package betsy.data.engines

/**
 * Responsible for starting and stopping tomcat as well as all tomcat related paths and properties.
 */
class Tomcat {

    AntBuilder ant = new AntBuilder()

    /**
     * the port of the tomcat
     */
    int port = 8080

    /**
     * The directory in which the tomcat has its directory.
     * Should contain a directory called <code>apache-tomcat-7.0.26</code>
     */
    String engineDir

    String tomcatName = "apache-tomcat-7.0.26"

    String getTomcatDir() {
        "$engineDir/$tomcatName"
    }

    String getTomcatUrl() {
        "http://localhost:$port"
    }

    /**
     * Start tomcat and wait until it responds to the <code>tomcatUrl</code>
     */
    void startup() {
        ant.exec(executable: "cmd", dir: engineDir) {
            arg(value: "/c")
            arg(value: new File("tomcat_startup.bat"))
        }
        ant.waitfor(maxwait: "30", maxwaitunit: "second", checkevery: "500") {
            http url: tomcatUrl
        }
    }

    /**
     * Shutdown the tomcat if running.
     */
    void shutdown() {
        // force shutdown
        ant.exec(executable: "cmd") {
            arg(value: "/c")
            arg(value: "taskkill")
            arg(value: '/FI')
            arg(value: "WINDOWTITLE eq Tomcat")
        }
    }

    /**
     * Throw exception if the tomcat is still running.
     */
    void checkIfIsRunning() {
        ant.fail(message: "tomcat for engine ${engineDir} is still running") {
            condition() {
                http url: tomcatUrl
            }
        }
    }

}
