<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <xsl:output omit-xml-declaration="yes" indent="yes" method="xml" />
    <xsl:strip-space elements="*" />

    <xsl:template match="bpmn2:definitions">
        <bpmn2:definitions targetNamespace="http://activiti.org/bpmn" xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd">
            <xsl:namespace name="camunda" select="'http://activiti.org/bpmn'" />
            <xsl:namespace name="bpmndi" select="'http://www.omg.org/spec/BPMN/20100524/DI'" />
            <xsl:namespace name="dc" select="'http://www.omg.org/spec/DD/20100524/DC'" />
            <xsl:namespace name="di" select="'http://www.omg.org/spec/DD/20100524/DI'" />
            <xsl:apply-templates select="@*|node()"/>
        </bpmn2:definitions>
    </xsl:template>

    <xsl:template match="bpmn2:scriptTask">
        <bpmn2:scriptTask scriptFormat="groovy">
            <xsl:apply-templates select="@*|node()"/>
        </bpmn2:scriptTask>
    </xsl:template>

    <!-- for gateways in general -->
    <xsl:template match="bpmn2:conditionExpression">
        <bpmn2:conditionExpression>
            <xsl:apply-templates select="@*"/>
            ${<xsl:value-of select="." />}
        </bpmn2:conditionExpression>
    </xsl:template>

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>