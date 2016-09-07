package betsy.common;

import java.util.List;
import java.util.stream.Collectors;

public interface HasName {

    String getName();

    static List<String> getNames(List<? extends HasName> list) {
        return list.stream().map(HasName::getName).collect(Collectors.toList());
    }

}
