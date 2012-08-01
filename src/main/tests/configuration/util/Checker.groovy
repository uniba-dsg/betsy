package configuration.util

import betsy.data.Process
import configuration.processes.Processes


class Checker {

    public static void main(String[] args) {
        List<Process> processes = new Processes().ALL

        printProcessesWithMoreThanOneAssertionPerTestCase(processes)
    }

    private static void printProcessesWithMoreThanOneAssertionPerTestCase(List<Process> processes) {
        List<Process> matchedProcesses = []

        processes.each { process ->
            process.testCases.each {
                testCase ->
                testCase.testSteps.each {
                    testStep ->
                    if (testStep.assertions.size() > 1) {
                        matchedProcesses << process
                    }
                }
            }
        }

        if(!matchedProcesses.empty){
            println "Cases with more than one assertion:"
            println "-------------"
            matchedProcesses.each {println it.normalizedId}
            println "-------------"
        }
    }

}
