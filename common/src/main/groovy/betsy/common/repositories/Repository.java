package betsy.common.repositories;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

public class Repository<T> {

    private static final Logger log = Logger.getLogger(Repository.class);
    
    private final Map<String, List<T>> repository = new LinkedHashMap<>();

    public void put(String key, List<T> values) {
        if (key == null) {
            throw new IllegalArgumentException("cannot add null into repository for values " + values);
        }

        repository.put(key, values);
    }

    public List<T> getByName(final String name) {
        String trimmedName = name.trim();
        final String key = repository.keySet().stream().filter(t -> t.equalsIgnoreCase(trimmedName)).findFirst().orElse(trimmedName);

        if (!repository.containsKey(key)) {
            throw new IllegalArgumentException("Name '" + key + "' does not exist in repository.");
        }

        List<T> result = new ArrayList<>();
        result.addAll(repository.get(key));
        return result;
    }

    public List<T> getByNames(final String... names) {
        List<T> result = new ArrayList<>();

        for (String name : names) {
            result.addAll(getByName(name));
        }

        // only distinct values
        result = result.stream().distinct().collect(Collectors.toList());

        if (result.isEmpty()) {
            throw new IllegalArgumentException("Names " + String.join(",", names) + " do not exist in repository.");
        }

        return result;
    }

    public List<String> getGroups() {
        return repository.entrySet().stream().filter(e -> e.getValue().size() > 1).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public List<String> getNames() {
        return new ArrayList<>(repository.keySet());
    }
}
