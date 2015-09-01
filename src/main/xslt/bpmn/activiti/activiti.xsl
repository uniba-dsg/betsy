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

                        // get service
                        org.activiti.engine.RuntimeService runtimeService = execution.getEngineServices().getRuntimeService();

                        // set variable
                        String executionId = execution.getId();

                        if(runtimeService.hasVariable(executionId, 'data')){
                            runtimeService.setVariable(executionId, 'data', "String");
                        }

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
                        org.activiti.engine.RuntimeService runtimeService = execution.getEngineServices().getRuntimeService();

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


            <!-- Set a counter variable with value 0 that can be used for escaping infinite loop conditions -->
            <xsl:when test="text() = 'SET_COUNTER'">
            <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;
            def counter = 0
                execution.setVariable("counter",counter)
                &lt;/bpmn2:script&gt;
             </xsl:text>
            </xsl:when>

            <!-- Increases the counter variable by 1 Note that unlike in Camunda the counter variable has to be explicitly defined in order to be accessible-->
            <xsl:when test="text() = 'INC_COUNTER'">
            <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;
            def counter = counter
                counter += 1
            execution.setVariable("counter",counter)
            &lt;/bpmn2:script&gt;
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
