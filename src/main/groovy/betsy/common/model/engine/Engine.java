package betsy.common.model.engine;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import betsy.common.HasName;
import betsy.common.model.HasID;
import betsy.common.model.ProcessLanguage;
import com.google.common.base.Objects;

import static java.util.Objects.requireNonNull;

public final class Engine implements HasID, HasName {

    public static final String DELIMITER = "--";

    private final String name;
    private final String version;
    private final List<String> configuration;
    private final ProcessLanguage language;
    private final LocalDate releaseDate;

    /**
     * http://spdx.org/licenses/
     */
    private final String license;

    private final String programmingLanguage = "Java";

    public Engine(ProcessLanguage language, String name, String version, LocalDate releaseDate, String license) {
        this(language, name, version, releaseDate, Collections.emptyList(), license);
    }

    public Engine(ProcessLanguage language, String name, String version, String configuration, LocalDate releaseDate, String license) {
        this(language, name, version, releaseDate, Collections.singletonList(configuration), license);
    }

    public Engine(ProcessLanguage language, String name, String version, LocalDate releaseDate, List<String> configuration, String license) {
        this.language = language;
        this.releaseDate = releaseDate;
        this.license = license;
        this.name = requireNonNull(name);
        this.version = requireNonNull(version);

        List<String> values = new LinkedList<>();
        values.addAll(configuration);
        this.configuration = requireNonNull(Collections.unmodifiableList(values));
    }

    public String toString() {
        return getNormalizedId();
    }

    public String getNormalizedId() {
        return getId().replaceAll(DELIMITER, "__").replaceAll("\\.", "_");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Engine engine = (Engine) o;
        return Objects.equal(toString(), engine.toString());
    }

    public ProcessLanguage getLanguage() {
        return language;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(toString());
    }

    private String getId() {
        List<String> values = new LinkedList<>();
        values.add(name);
        values.add(version);
        values.addAll(configuration);

        return String.join(DELIMITER, values);
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public List<String> getConfiguration() {
        return configuration;
    }

    @Override
    public String getID() {
        return getNormalizedId();
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public String getLicense() {
        return license;
    }

    public String getLicenseURL() {
        return String.format("http://spdx.org/licenses/%s.html", getLicense());
    }
}
