package betsy.executables.analytics.model

import java.nio.file.Path


class CsvReport {

    Path file
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

    int getNumberOfSuccessfulTestsPer(Engine engine){
        int successfulTests = 0
        for(Test test : tests){
           Result result = test.engineToResult.get(engine)
            if(result.isSuccessful()){
                successfulTests++
            }
        }

        successfulTests
    }

    String getRelativePath(Group group, Engine engine, Test test) {
        try {
            String path = new FileNameFinder().getFileNames("test/reports/html/soapui/${engine.name}/${group.name}", "*_${test.fullName}.html").first()
            String parentPath = new File(file).parentFile.absolutePath
            String relativePath = path.substring(parentPath.length() + 1)

            return relativePath
        } catch (Exception e){
            println e.getMessage()
            return "#"
        }
    }


}
