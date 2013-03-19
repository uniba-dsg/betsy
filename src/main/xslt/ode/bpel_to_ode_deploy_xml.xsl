<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable">

    <xsl:output indent="yes" method="xml"/>

    <xsl:include href="../utils.xsl"/>

    <xsl:template match="/bpel:process">
        <deploy xmlns="http://www.apache.org/ode/schemas/dd/2007/03">

            <process name="tns:{attribute::name}">
                <xsl:namespace name="tns" select="string(@targetNamespace)"/>
                <active>true</active>
                <xsl:apply-templates />
            </process>
        </deploy>
    </xsl:template>

    <xsl:template match="//bpel:partnerLink">

        <xsl:variable name="partnerLinkTypWithoutNamespacePrefix"
                      select="substring-after(attribute::partnerLinkType,':')"/>

        <xsl:variable name="wsdlBaseName" select="substring($partnerLinkTypWithoutNamespacePrefix,0,string-length($partnerLinkTypWithoutNamespacePrefix) - string-length('PartnerLinkType') + 1)" />

        <xsl:variable name="partnerLinkTypNamespacePrefix"
                      select="substring-before(string(attribute::partnerLinkType),':')"
                />

        <xsl:variable name="partnerLinkTypeNamespace">
            <xsl:call-template name="get-ns-name">
                <xsl:with-param name="ns-prefix" select="$partnerLinkTypNamespacePrefix"/>
            </xsl:call-template>
        </xsl:variable>


        <xsl:if test="attribute::partnerRole">
            <invoke partnerLink="{string(attribute::name)}" xmlns="http://www.apache.org/ode/schemas/dd/2007/03">
                <xsl:namespace name="tns" select="$partnerLinkTypeNamespace"/>
                <service name="tns:{$wsdlBaseName}Service"
                         port="{$wsdlBaseName}Port"/>
            </invoke>
        </xsl:if>

        <xsl:if test="attribute::myRole">
            <provide partnerLink="{string(attribute::name)}" xmlns="http://www.apache.org/ode/schemas/dd/2007/03">
                <xsl:namespace name="tns" select="$partnerLinkTypeNamespace"/>
                <service name="tns:{$wsdlBaseName}Service"
                         port="{$wsdlBaseName}Port"/>
            </provide>
        </xsl:if>

    </xsl:template>

    <!-- Override default template for copying text -->
    <xsl:template match="text()|@*" />

</xsl:stylesheet>