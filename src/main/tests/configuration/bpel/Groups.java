package configuration.bpel;

import betsy.common.model.ProcessLanguage;
import betsy.common.model.feature.Group;

public class Groups {
    public static Group BASIC = new Group("basic", ProcessLanguage.BPEL, "Basic activities are the basic building blocks of a BPEL process.");
    public static Group STRUCTURED = new Group("structured", ProcessLanguage.BPEL, "Structured activities compose basic activities into a control-flow graph.");
    public static Group SCOPES = new Group("scopes", ProcessLanguage.BPEL, "Scopes provide the execution context of their enclosed activities.");
    public static Group CFPATTERNS = new Group("cfpatterns", ProcessLanguage.BPEL, "The original 20 Workflow Control-Flow patterns from van der Aalst et al.");
    public static Group ERROR = new Group("error", ProcessLanguage.BPEL, "The robustness or fault tolerant tests.");
    public static Group SA = new Group("sa", ProcessLanguage.BPEL, "The 94 static analysis rules of BPEL.");
}
