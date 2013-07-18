package corebpel

import betsy.data.BetsyProcess

class CoreBPEL {

    // COPIED FROM src/com/beepell/deployment/transform/Transform.java
    public static final String[] XSL_SHEETS = [
        "remove-optional-extensions.xsl",            // Remove optional extensions

        // REMOVE we do not test these meta data
        "remove-documentation.xsl",                  // Remove human readable documentation

        "default-message-exchanges.xsl",             // Make default message exchanges explicit
        "process.xsl",                               // Move process scope to an explicit scope

        // TEST only petalsesb has problems with it
        "repeatUntil.xsl",                           // <repeatUntil> to <while>

        // REMOVE every engine supports if fully
        "if.xsl",                                    // Add missing <else>-clause and turn <elseif> into nested <if>

        // TEST should enable Variables-DefaultInitialization for ODE and OpenESB
        "scope.xsl",                                 // Variable initializations

        "receive.xsl",                               // Change <receive>s into <pick>s
        "invoke.xsl",                                // Make implicit <scope> and assignments explicit for <invoke>
        "pick.xsl",                                  // Make implicit <scope> and assignments explicit for <pick>
        "reply.xsl",                                 // Make implicit <scope> and assignments explicit for <reply>
        "default-handlers.xsl",                      // Make the default handlers explicit
        "sequence.xsl",                              // Change <sequence>s into <flow>s
        "default-conditions.xsl",                    // Make the default conditions explicit
        "default-attribute-values-simple.xsl",       // Make the simple default attribute values explicit
        "default-attribute-values-global.xsl",       // Make the global default attribute values explicit
        "default-attribute-values-inherited.xsl",    // Make the inherited default attribute values explicit
        "standard-attributes-elements.xsl",          // Move standard attributes and elements to wrapper <flow>s
        "remove-redundant-attributes.xsl"            // Remove redundant attributes
    ]

    AntBuilder ant = new AntBuilder()

    String temporaryDirectory
    String bpelFilePath

    String getBpelFileName() {
        bpelFilePath.split("/").last()
    }

    String getTemporaryBpelFilePath(String suffix) {
        "${temporaryDirectory}/${bpelFileName}.${suffix}"
    }

    public void toCoreBPEL(String[] xslSheets) {
        for(String xslSheet : xslSheets){
            toCoreBPEL(xslSheet)
        }
    }

    public void toCoreBPEL() {
        toCoreBPEL(XSL_SHEETS)
    }

    public void toCoreBPEL(String xslSheet) {
        String temporaryBeforeBpelFilePath = getTemporaryBpelFilePath("before_${xslSheet}")
        String temporaryAfterBpelFilePath = getTemporaryBpelFilePath("after_${xslSheet}")

        ant.copy(file: bpelFilePath, tofile: temporaryBeforeBpelFilePath)
        ant.xslt(in: bpelFilePath, out: temporaryAfterBpelFilePath, style: "src/main/xslt/corebpel/${xslSheet}", force: "yes") {
            // QUESTION which characters are allowed for a prefix? we use lower case letters to be safe!
            param(name: "freshPrefix", expression: "newprefix")
        }
        ant.copy(file: temporaryAfterBpelFilePath, tofile: bpelFilePath)
    }

}
