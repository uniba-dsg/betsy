package betsy.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ClasspathHelper {

    public static Path getFilesystemPathFromClasspathPath(String classpathPath) {
        try {
            URL resource = ClasspathHelper.class.getResource(Objects.requireNonNull(classpathPath, "given path must not be null"));
            Objects.requireNonNull(resource, "classpath path could not be found for " + classpathPath);
            return Paths.get(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("path not found", e);
        }
    }

    public static URL getURLFromClasspathPath(String classpathPath) {
        URL resource = ClasspathHelper.class.getResource(Objects.requireNonNull(classpathPath, "given path must not be null"));
        Objects.requireNonNull(resource, "classpath path could not be found for " + classpathPath);
        return resource;
    }

    public static List<String> getContentsForFileOfClasspath(String classpathPath) {
        InputStream is = ClasspathHelper.class.getResourceAsStream(Objects.requireNonNull(classpathPath));
        if(is == null) {
            throw new IllegalArgumentException("Resource not available on classpath: " + classpathPath);
        }

        List<String> lines = new LinkedList<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return lines;
    }

}
