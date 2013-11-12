package betsy.data

import betsy.data.engines.Engine;

class BetsyProcess implements Cloneable {

    public static String PATH_PREFIX = "src/main/tests"

    String bpel

    Engine engine

    String description=""

    List<TestCase> testCases = []

    List<String> wsdls = []

    void setTestCases(List<TestCase> testCases) {
        uniqueifyTestCaseNames(testCases)
        this.testCases = testCases
    }

    private static void uniqueifyTestCaseNames(List<TestCase> testCases) {
        // group by name of test case
        testCases.groupBy { it.name }.each {

            if(it.value.size() > 1) {
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
    List<String> additionalFiles = []

    String getId() {
        // without .bpel extension and without files/
        bpel.substring(6, bpel.length() - 5)
    }

    @Override
    protected Object clone() {
        return new BetsyProcess(bpel: bpel, wsdls: wsdls, additionalFiles: additionalFiles, testCases: testCases)
    }

    String toString() {
        getNormalizedId()
    }

    String getEndpoint() {
        getEngine().getEndpointUrl(this)
    }

    String getWsdlEndpoint() {
        getEndpoint() + "?wsdl"
    }

    String getGroup() {
        getId().split("/").first()
    }

    /**
     * An id as "language_features/structured_activities/Sequence" is transformed to
     * "language_features__structured_activities__Sequence"
     *
     * @return
     */
    String getNormalizedId() {
        getId().replaceAll('/', '__')
    }

    /**
     * A bpel path as "language_features/structured_activities/Sequence.bpel" is used to extract "Sequence.bpel"
     *
     * @return
     */
    String getBpelFileName() {
        bpel.split("/").last()
    }

    String getName() {
        getBpelFileNameWithoutExtension()
    }

    String getBpelFileNameWithoutExtension() {
        getBpelFileName().substring(0, getBpelFileName().length() - 5)
    }

    String getBpelFilePath() {
        "$PATH_PREFIX/$bpel"
    }

    /**
     * The path <code>test/$engine/$process</code>
     *
     * @return the path <code>test/$engine/$process</code>
     */
    String getTargetPath() {
        "$engine.path/${normalizedId}"
    }

    String getTargetLogsPath() {
        "${getTargetPath()}/logs"
    }

    /**
     * The path <code>test/$engine/$process/pkg</code>
     *
     * @return the path <code>test/$engine/$process/pkg</code>
     */
    String getTargetPackagePath() {
        "${getTargetPath()}/pkg"
    }

    /**
     * The path <code>test/$engine/$process/tmp</code>
     *
     * @return the path <code>test/$engine/$process/tmp</code>
     */
    String getTargetTmpPath() {
        "${getTargetPath()}/tmp"
    }

    /**
     * The path <code>test/$engine/$process/pkg/$processId.zip</code>
     *
     * @return the path <code>test/$engine/$process/pkg/$processId.zip</code>
     */
    String getTargetPackageFilePath() {
        getTargetPackageFilePath("zip")
    }

    String getTargetPackageFilePath(String extension) {
        "${getTargetPackagePath()}/${name}.${extension}"
    }

    /**
     * The path <code>test/$engine/$process/pkg/$processId.jar</code>
     *
     * @return the path <code>test/$engine/$process/pkg/$processId.jar</code>
     */
    String getTargetPackageJarFilePath() {
        "${getTargetTmpPath()}/${name}.jar"
    }

    /**
     * The path <code>test/$engine/$process/pkg/${processId}Application.zip</code>
     *
     * @return the path <code>test/$engine/$process/pkg/${processId}Application.zip</code>
     */
    String getTargetPackageCompositeFilePath() {
        "${getTargetPackagePath()}/${name}Application.zip"
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
    String getTargetSoapUIPath() {
        "${getTargetPath()}/soapui"
    }

    /**
     * The path <code>test/$engine/$process/reports</code>
     *
     * @return the path <code>test/$engine/$process/reports</code>
     */
    String getTargetReportsPath() {
        "${getTargetPath()}/reports"
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

    String getTargetSoapUIFilePath() {
        "${getTargetSoapUIPath()}/${getTargetSoapUIFileName()}"
    }

    /**
     * The path <code>test/$engine/$process/bpel</code>
     *
     * @return the path <code>test/$engine/$process/bpel</code>
     */
    String getTargetBpelPath() {
        "${getTargetPath()}/bpel"
    }

    String getTargetBpelFilePath() {
        "${getTargetBpelPath()}/$bpelFileName"
    }

    List<String> getWsdlPaths() {
        getWsdls().collect { wsdl -> "$PATH_PREFIX/$wsdl"}
    }

    List<String> getTargetWsdlPaths() {
        getWsdlFileNames().collect {wsdl -> "${getTargetBpelPath()}/$wsdl"}
    }

    List<String> getWsdlFileNames() {
        wsdls.collect { wsdl -> wsdl.split("/").last()}
    }

    List<String> getAdditionalFilePaths() {
        additionalFiles.collect { additionalFile -> "$PATH_PREFIX/$additionalFile"}
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        BetsyProcess process = (BetsyProcess) o

        if (bpel != process.bpel) return false

        return true
    }

    int hashCode() {
        return (bpel != null ? bpel.hashCode() : 0)
    }

}
