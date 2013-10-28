package betsy.data.engines

import betsy.data.Engine
import betsy.data.BetsyProcess
import betsy.data.engines.installer.ActiveBpelInstaller
import betsy.data.engines.server.Tomcat

/*
* Currently using in-memory mode for the engine
 */
class ActiveBpelEngine extends Engine {

    @Override
    String getName() {
        return "active-bpel"
    }

    @Override
    String getEndpointUrl(BetsyProcess process) {
        "${tomcat.tomcatUrl}/active-bpel/services/${process.bpelFileNameWithoutExtension}TestInterfaceService"
    }

    Tomcat getTomcat() {
        new Tomcat(ant: ant, engineDir: serverPath, tomcatName: "apache-tomcat-5.5.36")
    }

    String getDeploymentDir() {
        "${tomcat.tomcatDir}/bpr"
    }

    @Override
    void storeLogs(BetsyProcess process) {
        ant.mkdir(dir: "${process.targetPath}/logs")
        ant.copy(todir: "${process.targetPath}/logs") {
            ant.fileset(dir: "${tomcat.tomcatDir}/logs/")
        }
        ant.copy(file: getAeDeploymentLog(), todir: "${process.targetPath}/logs")
    }

    private String getAeDeploymentLog() {
        String homeDir = System.getProperty("user.home");
        homeDir = homeDir.endsWith(File.separator) ?: homeDir + File.separator;

        "$homeDir/AeBpelEngine/deployment-logs/aeDeployment.log"
    }

    @Override
    void startup() {
        tomcat.startup()
    }

    @Override
    void shutdown() {
        tomcat.shutdown()
    }

    @Override
    void install() {
        new ActiveBpelInstaller().install()
    }


    @Override
    void deploy(BetsyProcess process) {
        ant.copy(file: process.getTargetPackageFilePath("bpr"), todir: deploymentDir)
    }

    @Override
    void onPostDeployment(BetsyProcess process) {
        // define custom condition
        ant.typedef (name:"httpcontains", classname:"betsy.data.engines.util.HttpContains")

        ant.sequential() {
            ant.waitfor(maxwait: "100", maxwaitunit: "second") {
                and {
                    available file: "${deploymentDir}/work/ae_temp_${process.bpelFileNameWithoutExtension}_bpr/META-INF/catalog.xml"
                    resourcecontains(resource: getAeDeploymentLog(),
                            substring: "[${process.getBpelFileNameWithoutExtension()}.pdd]")
                    httpcontains(contains:"Running", url:"http://localhost:8080/BpelAdmin/")
                }
            }
        }
    }

    public void buildArchives(BetsyProcess process) {
        createFolderAndCopyProcessFilesToTarget(process)

        // create deployment descriptor
        String metaDir = process.targetBpelPath + "/META-INF"
        ant.echo file: "$metaDir/MANIFEST.MF", message: "Manifest-Version: 1.0"
        ant.xslt(in: process.bpelFilePath, out: "$metaDir/${process.bpelFileNameWithoutExtension}.pdd", style: "${getXsltPath()}/active-bpel_to_deploy_xml.xsl")
        ant.xslt(in: process.bpelFilePath, out: "$metaDir/catalog.xml", style: "${getXsltPath()}/active-bpel_to_catalog.xsl")

        replaceEndpointAndPartnerTokensWithValues(process)
        bpelFolderToZipFile(process)

        // create bpr file
        ant.move(file: process.targetPackageFilePath, toFile: process.getTargetPackageFilePath("bpr"))
    }

    @Override
    void failIfRunning() {
        tomcat.checkIfIsRunning()
    }

}
