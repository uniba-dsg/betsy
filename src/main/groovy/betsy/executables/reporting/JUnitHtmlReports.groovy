package betsy.executables.reporting

class JUnitHtmlReports {

    AntBuilder ant = new AntBuilder()

    /**
     * tests folder
     */
    String path

    public void create() {
        ant.echo(message: "creating reporting ant scripts")
        ant.echo(message: createAntReportFile(), file: "${path}/build.xml")

        ant.echo(message: "executing reporting ant scripts")
        ant.exec(executable: "cmd", dir: path, osfamily: "windows") {
            arg(value: "/c")
            arg(value: "ant")
        }

        ant.exec(executable: "ant", dir: path, osfamily: "unix")
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
