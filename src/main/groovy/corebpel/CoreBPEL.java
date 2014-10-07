package corebpel;

import betsy.common.tasks.FileTasks;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoreBPEL {
    public static final List<String> XSL_SHEETS = Arrays.asList("remove-optional-extensions.xsl", "remove-documentation.xsl",
            "default-message-exchanges.xsl", "process.xsl", "repeatUntil.xsl", "if.xsl", "scope.xsl", "receive.xsl",
            "invoke.xsl", "pick.xsl", "reply.xsl", "default-handlers.xsl", "sequence.xsl", "default-conditions.xsl",
            "default-attribute-values-simple.xsl", "default-attribute-values-global.xsl", "default-attribute-values-inherited.xsl",
            "standard-attributes-elements.xsl", "remove-redundant-attributes.xsl", "repeatUntil-improved.xsl");
    public static final List<String> ALL_OPTION = Arrays.asList("remove-optional-extensions.xsl", "remove-documentation.xsl",
            "default-message-exchanges.xsl", "process.xsl", "repeatUntil.xsl", "if.xsl", "scope.xsl", "receive.xsl",
            "invoke.xsl", "pick.xsl", "reply.xsl", "default-handlers.xsl", "sequence.xsl", "default-conditions.xsl",
            "default-attribute-values-simple.xsl", "default-attribute-values-global.xsl", "default-attribute-values-inherited.xsl",
            "standard-attributes-elements.xsl", "remove-redundant-attributes.xsl");

    private static Map<String, Templates> nameToTransformation = new HashMap<>();

    private final Path temporaryDirectory;
    private final Path bpelFilePath;

    public CoreBPEL(Path temporaryDirectory, Path bpelFilePath) {
        this.temporaryDirectory = temporaryDirectory;
        this.bpelFilePath = bpelFilePath;

        FileTasks.assertFile(bpelFilePath);
    }

    private static Transformer getTransformerByName(String name) throws TransformerConfigurationException {
        return getTemplatesByName(name).newTransformer();
    }

    protected static Templates getTemplatesByName(String name) throws TransformerConfigurationException {
        Templates tmpl = nameToTransformation.get(name);
        if (tmpl == null) {
            Source xsltSource = new StreamSource(new File("src/main/xslt/corebpel/" + name));
            TransformerFactory transFact = TransformerFactory.newInstance();
            tmpl = transFact.newTemplates(xsltSource);
            nameToTransformation.put(name, tmpl);
        }

        return tmpl;
    }

    public static void validate() throws TransformerConfigurationException {
        for (String xslSheet : XSL_SHEETS) {
            getTemplatesByName(xslSheet);
        }

    }

    public String getBpelFileName() {
        return bpelFilePath.getFileName().toString();
    }

    public Path getTemporaryBpelFilePath(final String suffix) {
        return temporaryDirectory.resolve(getBpelFileName() + "." + suffix);
    }

    public void toCoreBPEL(List<String> xslSheets) throws IOException, TransformerException {
        for (String xslSheet : xslSheets) {
            toCoreBPEL(xslSheet);
        }
    }

    public void toCoreBPEL(final String xslSheet) throws IOException, TransformerException {
        Path temporaryBeforeBpelFilePath = getTemporaryBpelFilePath("before_" + xslSheet + ".bpel");
        Path temporaryAfterBpelFilePath = getTemporaryBpelFilePath("after_" + xslSheet + ".bpel");

        Files.createDirectories(temporaryDirectory);
        Files.copy(bpelFilePath, temporaryBeforeBpelFilePath);

        Transformer transformer = getTransformerByName(xslSheet);
        transformer.setParameter("freshPrefix", "newprefix");
        transformer.transform(new StreamSource(bpelFilePath.toFile()), new StreamResult(temporaryAfterBpelFilePath.toFile()));

        Files.copy(temporaryAfterBpelFilePath, bpelFilePath, StandardCopyOption.REPLACE_EXISTING);
    }

}
