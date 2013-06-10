package betsy.executables.generator

import betsy.executables.soapui.builder.SoapUiProjectBuilder
import betsy.data.Process


class TestBuilder {

    AntBuilder ant = new AntBuilder()

    Process process

    /**
     * timeout for pending response in milliseconds
     */
    int requestTimeout

    public void buildTest() {
        ant.echo message: "Creating SoapUI TestSuite for Process ${process.bpelFileNameWithoutExtension}"
        ant.mkdir dir: process.targetSoapUIPath
        new SoapUiProjectBuilder(process: process, requestTimeout: requestTimeout).createSoapUIProject()
    }
}
