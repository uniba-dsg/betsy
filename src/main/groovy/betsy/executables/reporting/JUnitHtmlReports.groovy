package betsy.executables.reporting

import betsy.Configuration

class JUnitHtmlReports {

    AntBuilder ant = new AntBuilder()

    /**
     * tests folder
     */
    String path

    public void create() {
        String antPath = "${Configuration.ANT_HOME}/bin/ant.bat"

        ant.echo(message: "creating reporting ant scripts")
        ant.echo(message: createAntReportFile(), file: "${path}/build.xml")

        ant.echo(message: "executing reporting ant scripts")
        ant.exec(executable: "cmd", dir: path) {
            arg(value: "/c")
            arg(value: new File(antPath).absolutePath)
        }
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
