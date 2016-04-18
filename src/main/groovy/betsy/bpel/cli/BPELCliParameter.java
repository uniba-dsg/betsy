package betsy.bpel.cli;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.common.cli.CliParameter;
import betsy.common.model.input.EngineIndependentProcess;

import java.util.List;

public interface BPELCliParameter extends CliParameter {

    List<AbstractBPELEngine> getEngines();
    List<EngineIndependentProcess> getProcesses();

    boolean checkDeployment();
    boolean hasCustomPartnerAddress();
    boolean transformToCoreBpel();
    String getCoreBPELTransformations();
    String getCustomPartnerAddress();
    boolean useExternalPartnerService();

}
