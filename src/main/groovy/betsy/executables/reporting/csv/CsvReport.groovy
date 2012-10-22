package betsy.executables.reporting.csv


class CsvReport {

    String file
    final SortedMap<String, Test> nameToTest = new TreeMap<String, Test>()
    final SortedMap<String, Group> nameToGroup = new TreeMap<String, Group>()
    final SortedMap<String, Engine> nameToEngine = new TreeMap<String, Engine>()

    Group getGroup(String name) {
        if (nameToGroup.containsKey(name)) {
            return nameToGroup.get(name)
        } else {
            Group group = new Group(name: name)
            nameToGroup.put(name, group)

            return group
        }
    }

    Collection<Engine> getEngines() {
        return nameToEngine.values();
    }

    Collection<Group> getGroups() {
        return nameToGroup.values();
    }

    Collection<Test> getTests(){
        return nameToTest.values()
    }

    Engine getEngine(String name) {
        if (nameToEngine.containsKey(name)) {
            return nameToEngine.get(name)
        } else {
            Engine engine = new Engine(name: name)
            nameToEngine.put(name, engine)

            return engine
        }
    }

    Test getTest(String name) {
        if (nameToTest.containsKey(name)) {
            return nameToTest.get(name)
        } else {
            Test test = new Test(name: name)
            nameToTest.put(name, test)

            return test
        }
    }


}
