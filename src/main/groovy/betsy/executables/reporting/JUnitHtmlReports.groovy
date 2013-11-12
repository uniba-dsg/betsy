package betsy.executables.reporting

import ant.tasks.AntUtil
import betsy.Configuration
import org.apache.log4j.Logger

class JUnitHtmlReports {

    private static final Logger log = Logger.getLogger(JUnitHtmlReports.class)

    AntBuilder ant = AntUtil.builder()

    /**
     * tests folder
     */
    String path

    public void create() {
        String antPath = "${Configuration.get("ant.home")}/bin/ant.bat"

        log.info "creating reporting ant scripts"
        ant.echo(message: createAntReportFile(), file: "${path}/build.xml")

        log.info "executing reporting ant scripts"
        ant.exec(executable: "cmd", dir: path, osfamily: "windows") {
            arg(value: "/c")
            arg(value: new File(antPath).absolutePath)
        }

        ant.exec(executable: new File(antPath).absolutePath, dir: path, osfamily: "unix")
    }

    private String createAntReportFile() {
        """
<project name="${path}" default="reports">

    <target name="reports">

        <mkdir dir="reports"/>
        <junitreport todir="reports">
            <fileset dir=".">
                <include name="**/TEST-*.xml"/>
            </fileset>
            <report format="frames" todir="reports/html"/>
        </junitreport>

    </target>

</project>"""
    }

}
