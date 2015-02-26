package betsy.common;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public interface HasPath {

    Path getPath();

    static List<Path> getPaths(List<? extends HasPath> list) {
        return list.stream().map(HasPath::getPath).collect(Collectors.toList());
    }

}
