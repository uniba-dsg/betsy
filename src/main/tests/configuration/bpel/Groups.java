package configuration.bpel;

import configuration.Capabilities;
import pebl.feature.ComputedMetric;
import pebl.feature.Group;
import pebl.feature.Language;
import pebl.feature.ValueType;

public class Groups {

    public static Language CONFORMANCE_BPEL = new Language(Capabilities.CONFORMANCE, "BPEL");
    public static Language EXPRESSIVENESS_BPEL = new Language(Capabilities.EXPRESSIVENESS, "BPEL");
    public static Language ROBUSTNESS_BPEL = new Language(Capabilities.ROBUSTNESS, "BPEL");

    public static Group BASIC = new Group("basic", CONFORMANCE_BPEL, "Basic activities are the basic building blocks of a BPEL process.");
    public static Group STRUCTURED = new Group("structured", CONFORMANCE_BPEL, "Structured activities compose basic activities into a control-flow graph.");
    public static Group SCOPES = new Group("scopes", CONFORMANCE_BPEL, "Scopes provide the execution context of their enclosed activities.");
    public static Group CFPATTERNS = new Group("cfpatterns", EXPRESSIVENESS_BPEL, "The original 20 Workflow Control-Flow patterns from van der Aalst et al.")
            .addMetric(ValueType.LONG, "directlySupportedPattern",
                    "The number of directly supported pattern.", "quantity", ComputedMetric.SCRIPT_COUNT_TRUE);

    public static Group SA = new Group("sa", CONFORMANCE_BPEL, "The 94 static analysis rules of BPEL.");

    public static Group ERROR = new Group("error", ROBUSTNESS_BPEL, "The robustness or fault tolerant tests.");
}
