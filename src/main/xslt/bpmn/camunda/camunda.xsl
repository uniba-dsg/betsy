<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <xsl:output omit-xml-declaration="yes" indent="yes" method="xml"/>
    <xsl:strip-space elements="*"/>

    <xsl:include href="../scriptActivitiCamunda.xsl"/>

    <xsl:template match="bpmn2:script">
        <xsl:choose>
            <xsl:when test="text() = 'SET_STRING_DATA'">
                <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;&lt;![CDATA[
                    // create log file
                    java.io.File file = new java.io.File("log" + testCaseNumber + "_dataInput.txt");

                    // create writer
                    java.io.BufferedWriter bw = null;
                    try {
                        file.createNewFile();
                        bw = new java.io.BufferedWriter(
                                new java.io.FileWriter(file, true)
                        );

                        // set variable
                        java.util.Map&lt;String,Object> vars = execution.getVariables();
                        if (vars.containsKey('data')) {
                            execution.setVariable('data', "String");
                        }

                        // get variable for logging purpose
                        Object data = execution.getVariable('data');

                        // log variable
                        bw.append(data);
                        bw.newLine();

                    } catch(java.io.IOException e) {
                    } finally {
                        if (bw != null) {
                            try {
                                bw.close();
                            } catch(java.io.IOException e) {
                            }
                        }
                    }]]&gt;&lt;/bpmn2:script&gt;</xsl:text>
            </xsl:when>

            <xsl:when test="text() = 'LOG_DATA'">
                <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;&lt;![CDATA[
                    // create log file
                    java.io.File file = new java.io.File("log" + testCaseNumber + "_data.txt");

                    // create writer
                    java.io.BufferedWriter bw = null;
                    try {
                        file.createNewFile();
                        bw = new java.io.BufferedWriter(
                                new java.io.FileWriter(file, true)
                        );

                        // get variable
                        Object obj = execution.getVariable('data');

                        // log value of variable
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
                    }]]&gt;&lt;/bpmn2:script&gt; </xsl:text>
            </xsl:when>

            <xsl:when test="text() = 'THROW_ERROR'">
                <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;
                    &lt;![CDATA[
                        throw new org.camunda.bpm.engine.delegate.BpmnError("ERR_CODE");
                    ]]&gt;&lt;/bpmn2:script&gt;
                 </xsl:text>
            </xsl:when>

            <xsl:when test="text() = 'INCREMENT_INTEGER_VARIABLE'">
                <xsl:text disable-output-escaping="yes">
                    &lt;bpmn2:script&gt;
                    &lt;![CDATA[
                        integerVariable++
                    ]]&gt;&lt;/bpmn2:script&gt;
                 </xsl:text>
            </xsl:when>

            <xsl:otherwise>
                <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;&lt;![CDATA[</xsl:text>
                <xsl:value-of disable-output-escaping="yes" select="text()"/>
                <xsl:text disable-output-escaping="yes">]]&gt;&lt;/bpmn2:script&gt;</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
