<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.jboss.org/drools"
                xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <xsl:output indent="yes" method="xml"/>
    <xsl:strip-space elements="*"/>

    <!-- general settings -->
    <xsl:template match="bpmn2:definitions">
        <bpmn2:definitions xmlns="http://www.jboss.org/drools" expressionLanguage="http://www.mvel.org/2.0"
                           targetNamespace="http://www.jboss.org/drools" typeLanguage="http://www.java.com/javaTypes"
                           xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd">
            <xsl:namespace name="tns" select="'http://www.jboss.org/drools'"/>
            <xsl:apply-templates select="@*"/>
            <bpmn2:itemDefinition id="testItem" structureRef="String"/>
            <bpmn2:itemDefinition id="testCaseNumberItem" structureRef="Long"/>
            <bpmn2:itemDefinition id="integerVariableItem" structureRef="Long"/>
            <xsl:apply-templates select="node()"/>
        </bpmn2:definitions>
    </xsl:template>

    <xsl:template match="bpmn2:process">
        <bpmn2:process tns:version="1" tns:adHoc="false" name="Test Process" processType="Private">
            <xsl:apply-templates select="@*"/>
            <bpmn2:property id="test" itemSubjectRef="testItem"/>
            <bpmn2:property id="testCaseNumber" itemSubjectRef="testCaseNumberItem"/>
            <bpmn2:property id="integerVariable" itemSubjectRef="integerVariableItem"/>
            <xsl:apply-templates select="node()"/>
        </bpmn2:process>
    </xsl:template>

    <!-- Special treatment for CallActivity_Process test: Do not create properties for second process in the file -->
    <xsl:template match="bpmn2:process[@id='CalledProcess']">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

    <!-- Special treatment for test with two processes: Do not create properties for second process in the file -->
    <xsl:template match="bpmn2:process[@id='ParallelProcess']">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="bpmn2:scriptTask">
        <bpmn2:scriptTask scriptFormat="http://www.java.com/java">
            <xsl:apply-templates select="@*|node()"/>
        </bpmn2:scriptTask>
    </xsl:template>

    <!-- for gateways in general -->
    <xsl:template match="bpmn2:conditionExpression">
        <bpmn2:conditionExpression xsi:type="bpmn2:tFormalExpression" language="http://www.java.com/java">
            <xsl:apply-templates select="@*"/>
            return <xsl:value-of select="."/>;
        </bpmn2:conditionExpression>
    </xsl:template>

    <!-- for conditional event definitions -->
    <xsl:template match="bpmn2:condition">
        <bpmn2:condition xsi:type="bpmn2:tFormalExpression" language="http://www.java.com/java">
            <xsl:apply-templates select="@*"/>
            return <xsl:value-of select="."/>;
        </bpmn2:condition>
    </xsl:template>

    <!-- For bpmn constraints: convert task elements to scriptTasks -->
    <xsl:template match="bpmn2:task">
        <bpmn2:scriptTask scriptFormat="http://www.java.com/java">
            <xsl:apply-templates select="@*|node()" />
            <bpmn2:script>return;</bpmn2:script>
        </bpmn2:scriptTask>
    </xsl:template>


    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="bpmn2:script">
        <xsl:choose>
            <xsl:when test="text() = 'SET_STRING_DATA'">
                <xsl:text disable-output-escaping="yes">
                    &lt;bpmn2:script&gt;&lt;![CDATA[
// set variable "data" to value "String"
kcontext.setVariable("data", "String");
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

    // get variable
    Object obj = kcontext.getVariable("data");

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
kcontext.setVariable("integerVariable", integerVariable+1);
                    ]]&gt;&lt;/bpmn2:script&gt;
                 </xsl:text>
            </xsl:when>

            <xsl:when test="text() = 'INCREMENT_INTEGER_VARIABLE_AND_LOG'">
                <xsl:text disable-output-escaping="yes">
                    &lt;bpmn2:script&gt;&lt;![CDATA[
// increment
kcontext.setVariable("integerVariable", integerVariable+1);

// log execution
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

            <!--This variation requires the { <bpmn2:error id="ERR_CODE" name="ERR_CODE"> to be defined in the bpmn file before the bpmn2:process definition-->
            <xsl:when test="text() = 'THROW_ERROR'">
                <xsl:text disable-output-escaping="yes">
                    &lt;bpmn2:script&gt;&lt;![CDATA[
kcontext.getProcessInstance().signalEvent(&quot;ERR_CODE&quot;, null);
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
