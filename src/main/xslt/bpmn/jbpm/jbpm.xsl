<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <xsl:output indent="yes" method="xml" />
    <xsl:strip-space elements="*" />

    <!-- in the case of a second process rename the properties -->
    <xsl:template match="bpmn2:process[@id='Process_2']">
        <bpmn2:process tns:version="1" tns:adHoc="false" name="Test Process 2" processType="Private">
            <xsl:apply-templates select="@*"/>
            <bpmn2:property id="test2" itemSubjectRef="testItem" />
            <bpmn2:property id="testCaseNumber2" itemSubjectRef="testCaseNumberItem" />
            <xsl:apply-templates select="node()"/>
        </bpmn2:process>
    </xsl:template>

    <!-- in the case of a third process rename the properties -->
    <xsl:template match="bpmn2:process[@id='Process_1']">
        <bpmn2:process tns:version="1" tns:adHoc="false" name="Test Process 3" processType="Private">
            <xsl:apply-templates select="@*"/>
            <bpmn2:property id="test3" itemSubjectRef="testItem" />
            <bpmn2:property id="testCaseNumber3" itemSubjectRef="testCaseNumberItem" />
            <xsl:apply-templates select="node()"/>
        </bpmn2:process>
    </xsl:template>

    <!-- general settings -->
    <xsl:template match="bpmn2:definitions">
        <bpmn2:definitions xmlns="http://www.jboss.org/drools" expressionLanguage="http://www.mvel.org/2.0" targetNamespace="http://www.jboss.org/drools" typeLanguage="http://www.java.com/javaTypes" xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd">
            <xsl:namespace name="tns" select="'http://www.jboss.org/drools'" />
            <xsl:apply-templates select="@*"/>
            <bpmn2:itemDefinition id="testItem" structureRef="String" />
            <bpmn2:itemDefinition id="testCaseNumberItem" structureRef="Long" />
            <xsl:apply-templates select="node()"/>
        </bpmn2:definitions>
    </xsl:template>

    <xsl:template match="bpmn2:process">
        <bpmn2:process tns:version="1" tns:adHoc="false" name="Test Process" processType="Private">
            <xsl:apply-templates select="@*"/>
            <bpmn2:property id="test" itemSubjectRef="testItem" />
            <bpmn2:property id="testCaseNumber" itemSubjectRef="testCaseNumberItem" />
            <xsl:apply-templates select="node()"/>
        </bpmn2:process>
    </xsl:template>

    <xsl:template match="bpmn2:sequenceFlow">
        <bpmn2:sequenceFlow tns:priority="1">
            <xsl:apply-templates select="@*|node()"/>
        </bpmn2:sequenceFlow>
    </xsl:template>

    <xsl:template match="bpmn2:scriptTask">
        <bpmn2:scriptTask scriptFormat="http://www.java.com/java">
            <xsl:apply-templates select="@*|node()"/>
        </bpmn2:scriptTask>
    </xsl:template>

    <!-- for Exclusive gateways-->
    <xsl:template match="bpmn2:exclusiveGateway[@id='ExclusiveGateway_1']">
        <bpmn2:exclusiveGateway gatewayDirection="Diverging">
            <xsl:apply-templates select="@*|node()"/>
        </bpmn2:exclusiveGateway>
    </xsl:template>

    <xsl:template match="bpmn2:exclusiveGateway[@id='ExclusiveGateway_2']">
        <bpmn2:exclusiveGateway gatewayDirection="Converging">
            <xsl:apply-templates select="@*|node()"/>
        </bpmn2:exclusiveGateway>
    </xsl:template>

    <xsl:template match="bpmn2:exclusiveGateway[@id='ExclusiveGateway_X']">
        <bpmn2:exclusiveGateway gatewayDirection="Mixed">
            <xsl:apply-templates select="@*|node()"/>
        </bpmn2:exclusiveGateway>
    </xsl:template>

    <!-- for gateways in general -->
    <xsl:template match="bpmn2:conditionExpression">
        <bpmn2:conditionExpression language="http://www.java.com/java">
            <xsl:apply-templates select="@*"/>
            return <xsl:value-of select="." />;
        </bpmn2:conditionExpression>
    </xsl:template>

    <!-- for conditional event definitions -->
    <xsl:template match="bpmn2:condition">
        <bpmn2:condition language="http://www.java.com/java">
            <xsl:apply-templates select="@*"/>
            return <xsl:value-of select="." />;
        </bpmn2:condition>
    </xsl:template>

    <!-- for parallel gateways-->
    <xsl:template match="bpmn2:parallelGateway[@id='ParallelGateway_1']">
        <bpmn2:parallelGateway gatewayDirection="Diverging">
            <xsl:apply-templates select="@*|node()"/>
        </bpmn2:parallelGateway>
    </xsl:template>

    <xsl:template match="bpmn2:parallelGateway[@id='ParallelGateway_2']">
        <bpmn2:parallelGateway gatewayDirection="Converging">
            <xsl:apply-templates select="@*|node()"/>
        </bpmn2:parallelGateway>
    </xsl:template>

    <!-- for inclusive gateways -->
    <xsl:template match="bpmn2:inclusiveGateway[@id='InclusiveGateway_1']">
        <bpmn2:inclusiveGateway gatewayDirection="Diverging">
            <xsl:apply-templates select="@*|node()"/>
        </bpmn2:inclusiveGateway>
    </xsl:template>

    <xsl:template match="bpmn2:inclusiveGateway[@id='InclusiveGateway_2']">
        <bpmn2:inclusiveGateway gatewayDirection="Converging">
            <xsl:apply-templates select="@*|node()"/>
        </bpmn2:inclusiveGateway>
    </xsl:template>

    <!-- for complex gateways -->
    <xsl:template match="bpmn2:complexGateway[@id='ComplexGateway_1']">
        <bpmn2:complexGateway gatewayDirection="Diverging">
            <xsl:apply-templates select="@*|node()"/>
        </bpmn2:complexGateway>
    </xsl:template>

    <xsl:template match="bpmn2:complexGateway[@id='ComplexGateway_2']">
        <bpmn2:complexGateway gatewayDirection="Converging">
            <xsl:apply-templates select="@*|node()"/>
        </bpmn2:complexGateway>
    </xsl:template>

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>