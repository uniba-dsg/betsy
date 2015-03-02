package betsy.bpel.engines.bpelg;

import betsy.bpel.model.BPELProcess;
import betsy.bpel.model.steps.WsdlOperation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Util {

    public static List<String> computeMatchingPattern(BPELProcess process) {
        // This method works based on the knowledge that we have no more than two operations available anyway
        String text = getText(process.getProcess());
        String canonicalText = betsy.common.engines.Util.canonicalizeXML(text);

        Set<WsdlOperation> operations = new HashSet<>();
        operations.addAll(Arrays.asList(WsdlOperation.SYNC_STRING, WsdlOperation.SYNC, WsdlOperation.ASYNC));

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
