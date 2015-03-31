<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <xsl:output omit-xml-declaration="yes" indent="yes" method="xml" />
    <xsl:strip-space elements="*" />

    <xsl:include href="../scriptActivitiCamunda.xsl" />

    <xsl:template match="bpmn2:script">
        <xsl:choose>
            <xsl:when test="text() = 'SET_STRING_DATA'">
                <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;&lt;![CDATA[
                    // create log file
                    java.io.File file = new java.io.File("log" + testCaseNumber + "data_input.txt");

                    // create writer
                    java.io.BufferedWriter bw = null;
                    try {
                        file.createNewFile();
                        bw = new java.io.BufferedWriter(
                                new java.io.FileWriter(file, true)
                        );

                        // get service
                        org.camunda.bpm.engine.ProcessEngine processEngine = org.camunda.bpm.engine.ProcessEngines.getDefaultProcessEngine();
                        org.camunda.bpm.engine.RuntimeService runtimeService = processEngine.getRuntimeService();

                        // set variable
                        String executionId = execution.getId();
                        runtimeService.setVariable(executionId, 'data', "String")
                        Object data = runtimeService.getVariable(executionId, 'data')

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

                        // get service
                        org.camunda.bpm.engine.ProcessEngine processEngine = org.camunda.bpm.engine.ProcessEngines.getDefaultProcessEngine();
                        org.camunda.bpm.engine.RuntimeService runtimeService = processEngine.getRuntimeService();

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
                    }]]&gt;&lt;/bpmn2:script&gt; </xsl:text>
            </xsl:when>

            <xsl:otherwise>
                <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;</xsl:text>
                <xsl:value-of select="text()"/>
                <xsl:text disable-output-escaping="yes">&lt;/bpmn2:script&gt;</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>