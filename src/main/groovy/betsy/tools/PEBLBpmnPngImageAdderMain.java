package betsy.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import pebl.xsd.PEBL;

public class PEBLBpmnPngImageAdderMain {

    public static void main(String[] args) throws JAXBException {
        final Path path = Paths.get(args[0]);
        final PEBL pebl = PEBL.from(path);
        createBpmnPngs(pebl, path.toAbsolutePath());
        pebl.writeTo(path.toAbsolutePath().getParent());
    }

    static void createBpmnPngs(PEBL pebl, Path path) {
        final Predicate<Path> isBpmnFile = f -> f.toString().endsWith(".bpmn");
        final Predicate<Path> missesPngFile = f -> !Files.exists(f.getParent().resolve(f.getFileName().toString() + ".png"));
        pebl.benchmark.tests.forEach(t -> {
            Optional.of(t.getProcess())
                    .filter(isBpmnFile)
                    .filter(missesPngFile)
                    .map(PEBLBpmnPngImageAdderMain::createBPMNImage)
                    .ifPresent(f -> {
                        if (!t.getFiles().contains(f)) {
                            t.getFiles().add(f);
                        }
                    });
        });
        pebl.result.testResults.forEach(t -> {
            final List<Path> newPngs = t.getFiles()
                    .stream()
                    .filter(isBpmnFile)
                    .filter(missesPngFile)
                    .map(PEBLBpmnPngImageAdderMain::createBPMNImage)
                    .collect(Collectors.toList());
            for (Path png : newPngs) {
                if (!t.getFiles().contains(png)) {
                    t.getFiles().add(png);
                }
            }
        });
    }

    private static Path createBPMNImage(Path bpmnFile) {
        String[] args = {bpmnFile.getParent().toString()};
        try {
            bpmnviz.Main.main(args);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return Paths.get(bpmnFile.toString() + (".png")).toAbsolutePath();
    }
}
