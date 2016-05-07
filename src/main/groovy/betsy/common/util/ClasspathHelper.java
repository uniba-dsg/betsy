package betsy.common.util;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class ClasspathHelper {

    private ClasspathHelper() {}

    public static Path getFilesystemPathFromClasspathPath(String classpathPath) {
        try {
            URL resource = ClasspathHelper.class.getResource(Objects.requireNonNull(classpathPath, "given path must not be null"));
            Objects.requireNonNull(resource, "classpath path could not be found for " + classpathPath);
            return Paths.get(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("path not found", e);
        }
    }

}
