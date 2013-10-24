package betsy.data.engines.installer

import static betsy.data.engines.installer.Constants.DOWNLOADS_DIR

class PetalsEsbInstaller {

    AntBuilder ant = new AntBuilder()

    String serverDir = "server/petalsesb"

    String fileName = "petals-esb-distrib-4.0.zip"
    String downloadUrl = "https://lspi.wiai.uni-bamberg.de/svn/betsy/${fileName}"

    String targetEsbInstallDir = "${serverDir}/petals-esb-4.0/install"
    String bpelComponentPath = "${serverDir}/petals-esb-distrib-4.0/esb-components/petals-se-bpel-1.1.0.zip"
    String soapComponentPath = "${serverDir}/petals-esb-distrib-4.0/esb-components/petals-bc-soap-4.1.0.zip"
    String sourceFile = "${serverDir}/petals-esb-distrib-4.0/esb/petals-esb-4.0.zip"

    public void install() {
        ant.delete dir: serverDir
        ant.mkdir dir: serverDir

        ant.get(dest: DOWNLOADS_DIR, skipexisting: true) {
            ant.url url: downloadUrl
        }

        ant.unzip src: "${DOWNLOADS_DIR}/${fileName}", dest: serverDir
        ant.unzip src: sourceFile, dest: serverDir

        // install bpel service engine and binding connector for soap messages
        ant.copy file: bpelComponentPath, todir: targetEsbInstallDir
        ant.copy file: soapComponentPath, todir: targetEsbInstallDir
    }
}
