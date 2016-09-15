<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL">
    <xsl:output omit-xml-declaration="yes" indent="yes" method="xml" />
    <xsl:strip-space elements="*" />

    <xsl:include href="../scriptActivitiCamunda.xsl" />

    <xsl:template match="bpmn2:script">
        <xsl:choose>
            <xsl:when test="text() = 'SET_STRING_DATA'">
                <xsl:text disable-output-escaping="yes">
                    &lt;bpmn2:script&gt;&lt;![CDATA[
// set variable "data" to value "String"
org.activiti.engineExtended.RuntimeService runtimeService = execution.getEngineServices().getRuntimeService();
String newValue = "String";
String executionId = execution.getId();
runtimeService.setVariable(executionId, 'data', newValue);
                    ]]&gt;&lt;/bpmn2:script&gt;
                </xsl:text>
            </xsl:when>

            <xsl:when test="text() = 'LOG_DATA'">
                <xsl:text disable-output-escaping="yes">
                    &lt;bpmn2:script&gt;&lt;![CDATA[
// create log file
java.io.File file = new java.io.File("log" + testCaseNumber + "_data.txt");

// create writer
java.io.BufferedWriter bw = null;

try {
    file.createNewFile();
    bw = new java.io.BufferedWriter(
            new java.io.FileWriter(file, true)
    );

    // get service
    org.activiti.engineExtended.RuntimeService runtimeService = execution.getEngineServices().getRuntimeService();

    // get variable
    String executionId = execution.getId();
    Object obj = runtimeService.getVariable(executionId, 'data');

    // log data
    bw.append(String.valueOf(obj));
    bw.newLine();

} catch(java.io.IOException e) {
} finally {
    if (bw != null) {
        try {
            bw.close();
        } catch(java.io.IOException e) {
        }
    }
}
                    ]]&gt;&lt;/bpmn2:script&gt;
                </xsl:text>
            </xsl:when>

            <xsl:when test="text() = 'INCREMENT_INTEGER_VARIABLE'">
                <xsl:text disable-output-escaping="yes">
                    &lt;bpmn2:script&gt;&lt;![CDATA[
execution.setVariable("integerVariable", execution.getVariable("integerVariable")+1)
                    ]]&gt;&lt;/bpmn2:script&gt;
                </xsl:text>
            </xsl:when>

            <xsl:when test="text() = 'INCREMENT_INTEGER_VARIABLE_AND_LOG'">
                <xsl:text disable-output-escaping="yes">
                    &lt;bpmn2:script&gt;&lt;![CDATA[
//Increment Variable
execution.setVariable("integerVariable", execution.getVariable("integerVariable")+1)

//log execution
java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter("log" + testCaseNumber + ".txt", true));
try {
    bw.append("INCREMENT");
    bw.newLine();
} catch(java.io.IOException e) {
} finally{
    if(bw != null) {
        try{
            bw.close();
        } catch(java.io.IOException e) {
        }
    }
}
                    ]]&gt;&lt;/bpmn2:script&gt;
                </xsl:text>
            </xsl:when>

            <xsl:when test="text() = 'THROW_ERROR'">
                <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;
                    &lt;![CDATA[
throw new org.activiti.engineExtended.delegate.BpmnError("ERR_CODE");
                    ]]&gt;&lt;/bpmn2:script&gt;
                 </xsl:text>
            </xsl:when>

            <xsl:otherwise>
                <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;</xsl:text>
                    <xsl:value-of select="text()"/>
                <xsl:text disable-output-escaping="yes">&lt;/bpmn2:script&gt;</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
