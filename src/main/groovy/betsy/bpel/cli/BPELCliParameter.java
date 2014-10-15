package betsy.bpel.cli;

import betsy.bpel.engines.Engine;
import betsy.bpel.model.BetsyProcess;

import java.util.List;

public interface BPELCliParameter {

    List<Engine> getEngines();
    List<BetsyProcess> getProcesses();

    boolean openResultsInBrowser();
    boolean checkDeployment();
    boolean hasCustomPartnerAddress();
    boolean transformToCoreBpel();
    String getCoreBPELTransformations();
    String getCustomPartnerAddress();
    boolean useExternalPartnerService();
    boolean buildArtifactsOnly();

    boolean showHelp();

}
