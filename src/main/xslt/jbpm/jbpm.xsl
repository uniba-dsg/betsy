<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <xsl:output omit-xml-declaration="yes" indent="yes" method="xml" />
    <xsl:strip-space elements="*" />

    <xsl:template match="bpmn2:definitions">
        <bpmn2:definitions xmlns="http://www.jboss.org/drools" expressionLanguage="http://www.mvel.org/2.0" targetNamespace="http://www.jboss.org/drools" typeLanguage="http://www.java.com/javaTypes" xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd">
            <xsl:namespace name="tns" select="'http://www.jboss.org/drools'" />
            <xsl:namespace name="bpmndi" select="'http://www.omg.org/spec/BPMN/20100524/DI'" />
            <xsl:namespace name="dc" select="'http://www.omg.org/spec/DD/20100524/DC'" />
            <xsl:namespace name="di" select="'http://www.omg.org/spec/DD/20100524/DI'" />
            <xsl:apply-templates select="@*|node()"/>
        </bpmn2:definitions>
    </xsl:template>

    <xsl:template match="bpmn2:process">
        <bpmn2:process tns:version="1" tns:adHoc="false" name="Test Process" processType="Private">
            <xsl:apply-templates select="@*|node()"/>
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

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>