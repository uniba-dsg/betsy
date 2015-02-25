package betsy.bpel.corebpel;

import betsy.bpel.engines.AbstractEngine;
import corebpel.CoreBPEL;

import java.util.List;
import java.util.stream.Collectors;

public class CoreBPELEngineExtension {
    public static void extendEngine(AbstractEngine engine, final List<String> transformations) {
        assertValidTransformations(transformations);
        engine.setPackageBuilder(new CoreBPELEnginePackageBuilder(transformations));
    }

    private static void assertValidTransformations(List<String> transformations) {
        List<String> element = transformations.stream().filter((t) -> !CoreBPEL.XSL_SHEETS.contains(t)).collect(Collectors.toList());
        if (!element.isEmpty()) {
            throw new IllegalArgumentException("Given transformations " + element + " are not a valid CoreBPEL transformations");
        }
    }

}
