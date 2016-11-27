package betsy.bpel.cli;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.common.cli.CliParameter;
import pebl.benchmark.test.Test;

import java.util.List;

public interface BPELCliParameter extends CliParameter {

    List<AbstractBPELEngine> getEngines();
    List<Test> getProcesses();

    boolean checkDeployment();
    boolean hasCustomPartnerAddress();
    String getCustomPartnerAddress();
    boolean useExternalPartnerService();

}
