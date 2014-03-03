<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:camunda="http://activiti.org/bpmn">
    <xsl:output omit-xml-declaration="yes" indent="yes" method="xml" />
    <xsl:strip-space elements="*" />

    <xsl:template match="bpmn2:definitions">
        <bpmn2:definitions targetNamespace="http://activiti.org/bpmn">
            <xsl:namespace name="camunda" select="'http://activiti.org/bpmn'" />
            <xsl:apply-templates select="@*|node()"/>
        </bpmn2:definitions>
    </xsl:template>

    <xsl:template match="bpmn2:endEvent/bpmn2:messageEventDefinition">
        <bpmn2:messageEventDefinition camunda:class="org.camunda.bpm.simple.EndEventDelegate">
            <xsl:apply-templates select="@*|node()"/>
        </bpmn2:messageEventDefinition>
    </xsl:template>

    <xsl:template match="bpmn2:serviceTask[@id='ServiceTask_1']">
        <bpmn2:serviceTask camunda:class="org.camunda.bpm.simple.StartTaskDelegate">
            <xsl:apply-templates select="@*|node()"/>
        </bpmn2:serviceTask>
    </xsl:template>

    <xsl:template match="node()|@*" name="identity">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>