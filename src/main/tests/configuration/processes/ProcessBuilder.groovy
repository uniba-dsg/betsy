package configuration.processes

import betsy.data.Process
import betsy.data.TestCase

class ProcessBuilder {

    public Process buildProcess(String name, List<TestCase> testCases) {
        new Process(bpel: "language-features/${name}.bpel",
                wsdls: ["language-features/TestInterface.wsdl"],
                testCases: testCases
        )
    }

    public Process buildProcessWithXsd(String name, List<TestCase> testCases) {
        new Process(bpel: "language-features/${name}.bpel",
                wsdls: ["language-features/TestInterface.wsdl"],
                testCases: testCases    ,
                additionalFiles: ["language-features/basic-activities/months.xsd"],
        )
    }

    public Process buildProcessWithPartner(String name, List<TestCase> testCases) {
        new Process(bpel: "language-features/${name}.bpel",
                wsdls: ["language-features/TestInterface.wsdl", "language-features/TestPartner.wsdl"],
                testCases: testCases
        )
    }

    public Process buildProcessWithXslt(String name, List<TestCase> testCases) {
        new Process(bpel: "language-features/${name}.bpel",
                wsdls: ["language-features/TestInterface.wsdl"],
                additionalFiles: ["language-features/basic-activities/echo.xslt", "language-features/basic-activities/notCompileable.xslt"],
                testCases: testCases
        )
    }

    public Process buildStructuredActivityProcess(String name, List<TestCase> testCases) {
        buildProcess("structured-activities/${name}", testCases)
    }

    public Process buildScopeProcess(String name, List<TestCase> testCases) {
        buildProcess("scopes/${name}", testCases)
    }

    public Process buildBasicActivityProcess(String name, List<TestCase> testCases) {
        buildProcess("basic-activities/${name}", testCases)
    }

    public Process buildStructuredActivityProcess(String name, String description, List<TestCase> testCases) {
        Process process = buildStructuredActivityProcess(name,testCases)
        process.description = description
        return process
    }

    public Process buildScopeProcess(String name, String description, List<TestCase> testCases) {
        Process process = buildScopeProcess(name, testCases)
        process.description = description
        return process
    }

    public Process buildBasicActivityProcess(String name, String description, List<TestCase> testCases) {
        Process process =  buildBasicActivityProcess(name, testCases)
        process.description = description
        return process
    }

    public Process buildProcessWithXsd(String name, String description, List<TestCase> testCases){
        Process process = buildProcessWithXsd(name, testCases)
        process.description = description
        return process
    }

    public Process buildProcessWithPartner(String name, String description, List<TestCase> testCases){
        Process process = buildProcessWithPartner(name,testCases)
        process.description = description
        return process
    }

    public Process buildProcessWithXslt(String name, String description, List<TestCase> testCases){
        Process process = buildProcessWithXslt(name, testCases)
        process.description = description
        return process
    }


}
