package betsy.common.model.engine;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import pebl.HasId;
import pebl.HasName;
import pebl.ProcessLanguage;
import pebl.result.engine.Engine;

public final class EngineExtended implements HasId, HasName {

    public static final String DELIMITER = "--";

    private final Engine engine;

    private final LocalDate releaseDate;

    private final ProcessLanguage processLanguage;

    /**
     * http://spdx.org/licenses/
     */
    private final String license;

    private final String programmingLanguage = "Java";

    public EngineExtended(ProcessLanguage language, String name, String version, LocalDate releaseDate, String license) {
        this(language, name, version, releaseDate, Collections.emptyList(), license);
    }

    public EngineExtended(ProcessLanguage language, String name, String version, String configuration, LocalDate releaseDate, String license) {
        this(language, name, version, releaseDate, Collections.singletonList(configuration), license);
    }

    public EngineExtended(ProcessLanguage language, String name, String version, LocalDate releaseDate, List<String> configuration, String license) {
        List<String> values = new LinkedList<>();
        values.addAll(configuration);
        this.engine = new Engine(language.getId(), name, version, Collections.unmodifiableList(values));
        this.processLanguage = language;
        this.releaseDate = releaseDate;
        this.license = license;

        engine.addExtension("programmingLanguage", "Java");
        engine.addExtension("releaseDate", releaseDate.toString());
        engine.addExtension("license", license);
        engine.addExtension("licenseURL", getLicenseURL());
        engine.addExtension("url", getURL());
    }

    public String getURL() {
        return EngineDetails.getEngineToURL().getOrDefault(this.getName(), "");
    }

    public String toString() {
        return getNormalizedId();
    }

    public String getNormalizedId() {
        return getInternalId().replaceAll(DELIMITER, "__").replaceAll("\\.", "_");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EngineExtended engineExtended = (EngineExtended) o;
        return Objects.equals(toString(), engineExtended.toString());
    }

    public ProcessLanguage getLanguage() {
        return processLanguage;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(toString());
    }

    private String getInternalId() {
        return engine.getId();
    }

    public String getName() {
        return engine.getName();
    }

    public String getVersion() {
        return engine.getVersion();
    }

    public List<String> getConfiguration() {
        return engine.getConfiguration();
    }

    @Override
    public String getId() {
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

    public Engine getEngine() {
        return engine;
    }
}
