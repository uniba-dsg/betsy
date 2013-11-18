package corebpel

import javax.xml.transform.Source
import javax.xml.transform.Templates
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

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
            "remove-redundant-attributes.xsl",            // Remove redundant attributes

            //improved versions
            "repeatUntil-improved.xsl" //without variable definition and initialization in one step
    ]

    private static Map<String, Templates> nameToTransformation = new HashMap<>()

    private static Transformer getTransformerByName(String name) {
        getTemplatesByName(name).newTransformer()
    }

    protected static Templates getTemplatesByName(String name) {
        Templates tmpl = nameToTransformation.get(name)
        if (tmpl == null) {
            Source xsltSource = new StreamSource(name);
            TransformerFactory transFact = TransformerFactory.newInstance();
            tmpl = transFact.newTemplates(xsltSource);
            nameToTransformation.put(name, tmpl)
        }
        tmpl
    }

    Path temporaryDirectory
    Path bpelFilePath

    String getBpelFileName() {
        bpelFilePath.last().toString()
    }

    Path getTemporaryBpelFilePath(String suffix) {
        temporaryDirectory.resolve("${bpelFileName}.${suffix}")
    }

    public void toCoreBPEL(String[] xslSheets) {
        for (String xslSheet : xslSheets) {
            toCoreBPEL(xslSheet)
        }
    }

    public void toCoreBPEL(String xslSheet) {
        Path temporaryBeforeBpelFilePath = getTemporaryBpelFilePath("before_${xslSheet}.bpel")
        Path temporaryAfterBpelFilePath = getTemporaryBpelFilePath("after_${xslSheet}.bpel")

        Files.createDirectories(temporaryDirectory)
        Files.copy(bpelFilePath, temporaryBeforeBpelFilePath)

        Transformer transformer = getTransformerByName("src/main/xslt/corebpel/${xslSheet}")
        transformer.setParameter("freshPrefix", "newprefix")
        transformer.transform(new StreamSource(bpelFilePath.toFile()), new StreamResult(temporaryAfterBpelFilePath.toFile()))

        Files.copy(temporaryAfterBpelFilePath, bpelFilePath, StandardCopyOption.REPLACE_EXISTING)
    }

}
