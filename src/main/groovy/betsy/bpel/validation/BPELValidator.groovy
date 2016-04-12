package betsy.bpel.validation

import betsy.bpel.model.BPELProcess
import betsy.common.model.EngineIndependentProcess
import betsy.common.util.FileTypes

import java.nio.file.Files
import java.nio.file.Path

/**
 * Validates a list of processes
 */
class BPELValidator {

    private final List<EngineIndependentProcess> processes

    BPELValidator(List<EngineIndependentProcess> processes) {
        this.processes = processes
    }

    public void validate() {
        // checks for two processes referencing the same BPEL file
        assertEachProcessReferenceBpelFile()

        // checks that the files references by the process exist
        assertAllFilesPerProcessExists()

        // checks that the BPEL process name is equal to the file name
        assertBpelProcessNameEqualToFileName()

        // checks that the targetNamespace of each BPEL process is unique
        assertBpelTargetNamespacesAreUnique()
    }

    private void assertBpelTargetNamespacesAreUnique() {
        List<String> namespaces = processes.collect { process ->
            def bpel = new XmlSlurper(false, false).parse(process.process.toFile())
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

    private void assertBpelProcessNameEqualToFileName() {
        processes.each { process ->
            String attributeName = new XmlSlurper(false, false).parse(process.process.toFile()).@name.text()
            if (attributeName != process.name) {
                throw new IllegalStateException("The configuration does not correspond with the BPEL process. Names differ. BPEL uses " + attributeName + " while " + process.name + " is the given fileName")
            }
        }
    }

    private void assertAllFilesPerProcessExists() {
        processes.each { process ->
            List<Path> paths = new LinkedList<>()
            paths.add(process.process)
            paths.addAll(process.getFiles());

            paths.each { path ->
                if (!Files.isRegularFile(path)) {
                    throw new IllegalStateException("process " + process + " references not existing file " + path)
                }
            }
        }
    }

    private void assertEachProcessReferenceBpelFile() {
        processes.each { p1 ->
            processes.each { p2 ->
                if (!p1.is(p2) && p1.process == p2.process) {
                    throw new IllegalStateException("Two processes reference same bpel file: " + p1 + " and " + p2)
                }
            }
        }
    }
}
