package betsy.bpel.engines.orchestra;

import betsy.common.tasks.FileTasks;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import javax.xml.namespace.QName;
import java.io.File;
import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;

public class OrchestraDeployer {

    public OrchestraDeployer(Path orchestraHome) {
        this.orchestraHome = Objects.requireNonNull(orchestraHome);
    }

    public void deploy(Path packageFilePath, String processName) {
        Project p = loadAntBuildFile();
        p.setProperty("bar", packageFilePath.toAbsolutePath().toString());
        p.executeTarget("deploy");

        FileTasks.createFile(getDeploymentMarkerFile(processName), "deployment done at " + new Date());
    }

    private Project loadAntBuildFile() {
        File buildFile = orchestraHome.resolve("build.xml").toFile();
        Project p = new Project();
        p.setUserProperty("ant.file", buildFile.getAbsolutePath());
        p.init();
        ProjectHelper helper = ProjectHelper.getProjectHelper();
        p.addReference("ant.projectHelper", helper);
        helper.parse(p, buildFile);
        return p;
    }

    public void undeploy(QName processQName) {
        Project p = loadAntBuildFile();
        p.setProperty("process", processQName.toString());
        p.executeTarget("undeploy");

        FileTasks.deleteFile(getDeploymentMarkerFile(processQName.getLocalPart()));
    }

    private Path getDeploymentMarkerFile(String processName) {
        return orchestraHome.resolve(processName + ".deployed");
    }

    public boolean isDeployed(QName processQName) {
        return FileTasks.hasFile(getDeploymentMarkerFile(processQName.getLocalPart()));
    }

    private final Path orchestraHome;
}
