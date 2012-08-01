package betsy.executables

import betsy.data.Process
import configuration.processes.Processes

/**
 * Validates a list of processes
 */
class Validator {

    public void validate(List<Process> processes) {
        // checks for two processes referencing the same BPEL file
        assertEachProcessReferenceBpelFile(processes)

        // checks that the files references by the process exist
        assertAllFilesPerProcessExists(processes)

        // checks that the BPEL process name is equal to the file name
        assertBpelProcessNameEqualToFileName(processes)

        // checks that the targetNamespace of each BPEL process is unique
        assertBpelTargetNamespacesAreUnique(processes)
    }

    private void assertBpelTargetNamespacesAreUnique(List<Process> processes) {
        List<String> namespaces = processes.collect {
            process ->
            def bpel = new XmlSlurper(false, false).parse(process.bpelFilePath)
            [bpel.@name.text(), bpel.@targetNamespace.text()]
        }
        namespaces.each { n1 ->
            namespaces.each { n2 ->
                if (!n1.is(n2) && n1[1] == n2[1]) {
                    throw new IllegalStateException("Same namespace is used for different bpel processes: " + n1 + " and " + n2 + ".")
                }
            }
        }
    }

    private void assertBpelProcessNameEqualToFileName(List<Process> processes) {
        processes.each {
            process ->
            String attributeName = new XmlSlurper(false, false).parse(process.bpelFilePath).@name.text()
            if (attributeName != process.bpelFileNameWithoutExtension) {
                throw new IllegalStateException("The configuration does not correspond with the BPEL process. Names differ. BPEL uses " + attributeName + " while " + process.bpelFileNameWithoutExtension + " is the given filename")
            }
        }
    }

    private void assertAllFilesPerProcessExists(List<Process> processes) {
        processes.each {
            process ->
            List<String> paths = [process.bpelFilePath, process.wsdlPaths, process.additionalFilePaths].flatten()

            paths.each { path ->
                if (!new File(path).exists()) {
                    throw new IllegalStateException("process " + process + " references not existing file " + path)
                }
            }
        }
    }

    private void assertEachProcessReferenceBpelFile(List<Process> processes) {
        processes.each {
            p1 ->
            processes.each {
                p2 ->
                if (!p1.is(p2) && p1.bpelFileName == p2.bpelFileName) {
                    throw new IllegalStateException("Two processes reference same bpel file: " + p1 + " and " + p2)
                }
            }
        }
    }
}
