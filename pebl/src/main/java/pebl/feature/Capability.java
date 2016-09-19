package pebl.feature;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import pebl.HasName;
import pebl.HasID;

public class Capability implements HasID, HasName {

    private final String name;
    private final ResultFormat resultFormat;
    private final List<Language> languages = new LinkedList<>();

    public Capability(String name, ResultFormat resultFormat) {
        this.name = Objects.requireNonNull(name);
        this.resultFormat = Objects.requireNonNull(resultFormat);
    }

    @Override
    public String getID() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    public ResultFormat getResultFormat() {
        return resultFormat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Capability construct = (Capability) o;
        return Objects.equals(getID(), construct.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }

    void addLanguage(Language language) {
        this.languages.add(language);
    }

    public List<Language> getLanguages() {
        return Collections.unmodifiableList(languages);
    }
}
