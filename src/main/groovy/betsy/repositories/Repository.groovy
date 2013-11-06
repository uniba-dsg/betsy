package betsy.repositories

class Repository<T> {

    private Map<String, List<T>> repository = [:]

    public void put(String key, List<T> values) {
        repository.put(key, values);
    }

    public List<T> getByName(String name) {
        String trimmedName = name.trim()
        String key = repository.keySet().find { it.toUpperCase() == trimmedName.toUpperCase() }
        if (key == null) {
            key = trimmedName
        }

        if (!repository.containsKey(key)) {
            throw new IllegalArgumentException("Name '${key}' does not exist in repository.")
        }

        List<T> result = []
        result.addAll(repository.get(key))

        return result
    }

    public List<T> getByNames(String[] names) {
        List<T> result = []

        for (String name : names) {
            result.addAll(getByName(name))
        }

        result = result.unique()

        if (result.isEmpty()) {
            throw new IllegalArgumentException("Names ${names.join(",")} do not exist in repository.")
        }

        return result
    }

    public List<String> getNames() {
        return new ArrayList<String>(repository.keySet())
    }


}
