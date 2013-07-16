package configuration.processes

import betsy.data.BetsyProcess
import betsy.data.TestCase

class ProcessBuilder {

    public BetsyProcess buildProcess(String name, List<TestCase> testCases) {
        new BetsyProcess(bpel: "language-features/${name}.bpel",
                wsdls: ["language-features/TestInterface.wsdl"],
                testCases: testCases
        )
    }

    public BetsyProcess buildProcessWithXsd(String name, List<TestCase> testCases) {
        new BetsyProcess(bpel: "language-features/${name}.bpel",
                wsdls: ["language-features/TestInterface.wsdl"],
                testCases: testCases    ,
                additionalFiles: ["language-features/basic-activities/months.xsd"],
        )
    }

    public BetsyProcess buildProcessWithPartner(String name, List<TestCase> testCases) {
        new BetsyProcess(bpel: "language-features/${name}.bpel",
                wsdls: ["language-features/TestInterface.wsdl", "language-features/TestPartner.wsdl"],
                testCases: testCases
        )
    }

    public BetsyProcess buildProcessWithXslt(String name, List<TestCase> testCases) {
        new BetsyProcess(bpel: "language-features/${name}.bpel",
                wsdls: ["language-features/TestInterface.wsdl"],
                additionalFiles: ["language-features/basic-activities/echo.xslt", "language-features/basic-activities/notCompileable.xslt"],
                testCases: testCases
        )
    }

    public BetsyProcess buildStructuredActivityProcess(String name, List<TestCase> testCases) {
        buildProcess("structured-activities/${name}", testCases)
    }

    public BetsyProcess buildScopeProcess(String name, List<TestCase> testCases) {
        buildProcess("scopes/${name}", testCases)
    }

    public BetsyProcess buildBasicActivityProcess(String name, List<TestCase> testCases) {
        buildProcess("basic-activities/${name}", testCases)
    }

    public BetsyProcess buildStructuredActivityProcess(String name, String description, List<TestCase> testCases) {
        BetsyProcess process = buildStructuredActivityProcess(name,testCases)
        process.description = description
        return process
    }

    public BetsyProcess buildScopeProcess(String name, String description, List<TestCase> testCases) {
        BetsyProcess process = buildScopeProcess(name, testCases)
        process.description = description
        return process
    }

    public BetsyProcess buildBasicActivityProcess(String name, String description, List<TestCase> testCases) {
        BetsyProcess process =  buildBasicActivityProcess(name, testCases)
        process.description = description
        return process
    }

    public BetsyProcess buildProcessWithXsd(String name, String description, List<TestCase> testCases){
        BetsyProcess process = buildProcessWithXsd(name, testCases)
        process.description = description
        return process
    }

    public BetsyProcess buildProcessWithPartner(String name, String description, List<TestCase> testCases){
        BetsyProcess process = buildProcessWithPartner(name,testCases)
        process.description = description
        return process
    }

    public BetsyProcess buildProcessWithXslt(String name, String description, List<TestCase> testCases){
        BetsyProcess process = buildProcessWithXslt(name, testCases)
        process.description = description
        return process
    }


}
