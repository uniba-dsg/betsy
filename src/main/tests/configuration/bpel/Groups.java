package configuration.bpel;

import betsy.common.model.feature.Group;
import betsy.common.model.feature.Language;
import configuration.Capabilities;

public class Groups {


    public static Language CONFORMANCE_BPEL = new Language(Capabilities.conformance, "BPEL");
    public static Language EXPRESSIVENESS_BPEL = new Language(Capabilities.expressiveness, "BPEL");

    public static Group BASIC = new Group("basic", CONFORMANCE_BPEL, "Basic activities are the basic building blocks of a BPEL process.");
    public static Group STRUCTURED = new Group("structured", CONFORMANCE_BPEL, "Structured activities compose basic activities into a control-flow graph.");
    public static Group SCOPES = new Group("scopes", CONFORMANCE_BPEL, "Scopes provide the execution context of their enclosed activities.");
    public static Group CFPATTERNS = new Group("cfpatterns", EXPRESSIVENESS_BPEL, "The original 20 Workflow Control-Flow patterns from van der Aalst et al.");
    public static Group ERROR = new Group("error", CONFORMANCE_BPEL, "The robustness or fault tolerant tests.");
    public static Group SA = new Group("sa", CONFORMANCE_BPEL, "The 94 static analysis rules of BPEL.");
}
