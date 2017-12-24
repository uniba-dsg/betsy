package betsy.bpel.engines.bpelg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import betsy.bpel.model.BPELProcess;
import betsy.bpel.model.BPELWsdlOperations;
import betsy.common.util.XMLUtil;
import pebl.benchmark.test.steps.soap.WsdlOperation;

public class Util {

    public static List<String> computeMatchingPattern(BPELProcess process) {
        // This method works based on the knowledge that we have no more than two operations available anyway
        String text = getText(process.getProcess());
        String canonicalText = XMLUtil.canonicalizeXML(text);

        Set<WsdlOperation> operations = new HashSet<>();
        operations.addAll(Arrays.asList(BPELWsdlOperations.SYNC_STRING, BPELWsdlOperations.SYNC, BPELWsdlOperations.ASYNC));

        Set<WsdlOperation> implementedOperations = new HashSet<>();

        for (String line : canonicalText.split("\\n")) {
            for (WsdlOperation operation : operations) {
                if (line.contains("operation=\"" + operation.getName() + "\"") && !line.contains("invoke")) {
                    implementedOperations.add(operation);
                }
            }
        }

        List<WsdlOperation> unimplementedOperations = new LinkedList<>();
        unimplementedOperations.addAll(operations);
        unimplementedOperations.removeAll(implementedOperations);

        return unimplementedOperations.stream().map(WsdlOperation::getName).collect(Collectors.toList());
    }

    private static String getText(Path file) {
        try {
            return new String(Files.readAllBytes(file));
        } catch (IOException e) {
            throw new RuntimeException("could not read file " + file, e);
        }
    }

}
