package betsy.common.engines;

import java.util.List;
import java.util.stream.Collectors;

public interface Nameable {

    String getName();

    static List<String> getNames(List<? extends Nameable> engines) {
        return engines.stream().map(Nameable::getName).collect(Collectors.toList());
    }

}
