package betsy.executables.reporting

import ant.tasks.AntUtil
import betsy.Configuration
import betsy.tasks.ConsoleTasks
import org.apache.log4j.Logger

import java.nio.file.Path

class JUnitHtmlReports {

    private static final Logger log = Logger.getLogger(JUnitHtmlReports.class)

    AntBuilder ant = AntUtil.builder()

    /**
     * tests folder
     */
    Path path

    public void create() {
        Path antBinFolder = Configuration.getPath("ant.home").resolve("bin").toAbsolutePath()

        log.info "creating reporting ant scripts"
        ant.echo(message: createAntReportFile(), file: path.resolve("build.xml"))

        log.info "executing reporting ant scripts"
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(path, antBinFolder.resolve("ant.bat").toString()))
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
