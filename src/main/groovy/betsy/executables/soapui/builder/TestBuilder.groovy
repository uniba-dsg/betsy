package betsy.executables.soapui.builder

import betsy.executables.soapui.builder.SoapUiProjectBuilder
import betsy.data.BetsyProcess


class TestBuilder {

    AntBuilder ant = new AntBuilder()

    BetsyProcess process

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
