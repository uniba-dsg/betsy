package betsy.data

import betsy.data.engines.Engine

import java.nio.file.Path

class BetsyProcess implements Cloneable {

    Path bpel

    Engine engine

    String description = ""

    List<TestCase> testCases = []

    List<Path> wsdls = []

    void setTestCases(List<TestCase> testCases) {
        uniqueifyTestCaseNames(testCases)
        this.testCases = testCases
    }

    private static void uniqueifyTestCaseNames(List<TestCase> testCases) {
        // group by name of test case
        testCases.groupBy { it.name }.each {

            if (it.value.size() > 1) {
                // iterate over values
                it.value.eachWithIndex { testCase, index ->

                    // append counter starting from 1
                    testCase.name += "-${index + 1}"
                }
            }
        }
    }

    /**
     * Additional files like xslt scripts or other resources.
     */
    List<Path> additionalFiles = []

    @Override
    protected Object clone() {
        return new BetsyProcess(bpel: bpel, wsdls: wsdls, additionalFiles: additionalFiles, testCases: testCases)
    }

    @Override
    public String toString() {
        return getNormalizedId();
    }

    String getEndpoint() {
        getEngine().getEndpointUrl(this)
    }

    String getWsdlEndpoint() {
        getEndpoint() + "?wsdl"
    }

    String getGroup() {
        bpel.getParent().getFileName().toString()
    }

    /**
     * An id as "language_features/structured_activities/Sequence" is transformed to
     * "language_features__structured_activities__Sequence"
     *
     * @return
     */
    String getNormalizedId() {
        "${group}__${name}"
    }

    /**
     * A bpel path as "language_features/structured_activities/Sequence.bpel" is used to extract "Sequence.bpel"
     *
     * @return
     */
    String getBpelFileName() {
        bpel.getFileName().toString()
    }

    String getName() {
        getBpelFileNameWithoutExtension()
    }

    String getBpelFileNameWithoutExtension() {
        getBpelFileName().substring(0, getBpelFileName().length() - 5)
    }

    Path getBpelFilePath() {
        bpel
    }

    /**
     * The path <code>test/$engine/$process</code>
     *
     * @return the path <code>test/$engine/$process</code>
     */
    Path getTargetPath() {
        engine.path.resolve(normalizedId)
    }

    Path getTargetLogsPath() {
        targetPath.resolve("logs")
    }

    /**
     * The path <code>test/$engine/$process/pkg</code>
     *
     * @return the path <code>test/$engine/$process/pkg</code>
     */
    Path getTargetPackagePath() {
        targetPath.resolve("pkg")
    }

    /**
     * The path <code>test/$engine/$process/tmp</code>
     *
     * @return the path <code>test/$engine/$process/tmp</code>
     */
    Path getTargetTmpPath() {
        targetPath.resolve("tmp")
    }

    /**
     * The path <code>test/$engine/$process/pkg/$processId.zip</code>
     *
     * @return the path <code>test/$engine/$process/pkg/$processId.zip</code>
     */
    Path getTargetPackageFilePath() {
        getTargetPackageFilePath("zip")
    }

    Path getTargetPackageFilePath(String extension) {
        targetPackagePath.resolve("${name}.${extension}")
    }

    /**
     * The path <code>test/$engine/$process/pkg/$processId.jar</code>
     *
     * @return the path <code>test/$engine/$process/pkg/$processId.jar</code>
     */
    Path getTargetPackageJarFilePath() {
        getTargetPackageFilePath("jar")
    }

    /**
     * The path <code>test/$engine/$process/pkg/${processId}Application.zip</code>
     *
     * @return the path <code>test/$engine/$process/pkg/${processId}Application.zip</code>
     */
    Path getTargetPackageCompositeFilePath() {
        targetPackagePath.resolve(targetPackageCompositeFile)
    }

    /**
     * The file name <code>${processId}Application.zip</code>
     *
     * @return the file name <code>${processId}Application.zip</code>
     */
    String getTargetPackageCompositeFile() {
        "${name}Application.zip"
    }

    /**
     * The path <code>test/$engine/$process/soapui</code>
     *
     * @return the path <code>test/$engine/$process/soapui</code>
     */
    Path getTargetSoapUIPath() {
        targetPath.resolve("soapui")
    }

    /**
     * The path <code>test/$engine/$process/reports</code>
     *
     * @return the path <code>test/$engine/$process/reports</code>
     */
    Path getTargetReportsPath() {
        targetPath.resolve("reports")
    }

    String getTargetSoapUIProjectName() {
        "${engine}.${getGroup()}.${getName()}".replaceAll("__", ".")
    }

    /**
     * The file name <code>test_$engine_$process_soapui.xml</code>
     *
     * @return the file name <code>test_$engine_$process_soapui.xml</code>
     */
    String getTargetSoapUIFileName() {
        "test_${engine}_${getGroup()}_${getName()}_soapui.xml"
    }

    Path getTargetSoapUIFilePath() {
        targetSoapUIPath.resolve(targetSoapUIFileName)
    }

    /**
     * The path <code>test/$engine/$process/bpel</code>
     *
     * @return the path <code>test/$engine/$process/bpel</code>
     */
    Path getTargetBpelPath() {
        targetPath.resolve("bpel")
    }

    Path getTargetBpelFilePath() {
        targetBpelPath.resolve(bpelFileName)
    }

    List<Path> getWsdlPaths() {
        getWsdls()
    }

    List<Path> getTargetWsdlPaths() {
        getWsdlFileNames().collect { wsdl -> targetBpelPath.resolve(wsdl) }
    }

    List<String> getWsdlFileNames() {
        wsdls.collect { wsdl -> wsdl.getFileName().toString() }
    }

    List<Path> getAdditionalFilePaths() {
        additionalFiles
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        BetsyProcess that = (BetsyProcess) o

        if (bpel != that.bpel) return false

        return true
    }

    int hashCode() {
        return bpel.hashCode()
    }
}
