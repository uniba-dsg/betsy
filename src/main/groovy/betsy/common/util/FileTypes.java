package betsy.common.util;

import java.nio.file.Files;
import java.nio.file.Path;

public final class FileTypes {

    public static final String BPEL = ".bpel";
    public static final String WSDL = ".wsdl";
    public static final String XSD = ".xsd";
    public static final String XSL = ".xsl";
    public static final String BPMN = ".bpmn";

    private FileTypes() {}

    public static boolean isBpelFile(Path file) {
        return isFileWithSpecificExtension(file, BPEL);
    }

    public static boolean isWsdlFile(Path file) {
        return isFileWithSpecificExtension(file, WSDL);
    }

    public static boolean isXsdFile(Path file) {
        return isFileWithSpecificExtension(file, XSD);
    }

    public static boolean isXslFile(Path file) {
        return isFileWithSpecificExtension(file, XSL);
    }

    public static boolean isBpmnFile(Path file) {
        return isFileWithSpecificExtension(file, BPMN);
    }


    private static boolean isFileWithSpecificExtension(Path file, String fileExtension) {
        return Files.isRegularFile(file) && file.toString().endsWith(fileExtension);
    }

}
