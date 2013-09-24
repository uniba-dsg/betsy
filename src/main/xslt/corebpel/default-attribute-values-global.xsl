<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Make the global default attribute values explicit.
     Attributes: expressionLanguage, queryLanguage -->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable">

    <xsl:output indent="yes" method="xml"/>

    <xsl:include href="constants.xsl"/>

    <xsl:template match="*">
        <xsl:param name="expressionLanguage"/>
        <xsl:param name="queryLanguage"/>
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates>
                <xsl:with-param name="expressionLanguage" select="$expressionLanguage"/>
                <xsl:with-param name="queryLanguage" select="$queryLanguage"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="bpel:process">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates>
                <xsl:with-param name="expressionLanguage">
                    <xsl:choose>
                        <xsl:when test="@expressionLanguage">
                            <xsl:value-of select="@expressionLanguage"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$xpathURN"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:with-param>
                <xsl:with-param name="queryLanguage">
                    <xsl:choose>
                        <xsl:when test="@queryLanguage">
                            <xsl:value-of select="@queryLanguage"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$xpathURN"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:with-param>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <!-- Adding missing expressionLanguage attributes -->
    <xsl:template
            match="bpel:branches | bpel:condition | bpel:finalCounterValue | bpel:for | bpel:from[normalize-space(text())] | bpel:joinCondition | bpel:repeatEvery | bpel:startCounterValue | bpel:to[normalize-space(text())] | bpel:transitionCondition | bpel:until">
        <xsl:param name="expressionLanguage"/>
        <xsl:param name="queryLanguage"/>
        <xsl:copy>
            <xsl:copy-of select="@*"/>

            <!-- if expressionLanguage attribute is missing add it -->
            <xsl:if test="not(@expressionLanguage)">
                <xsl:attribute name="expressionLanguage">
                    <xsl:value-of select="$expressionLanguage"/>
                </xsl:attribute>
            </xsl:if>

            <xsl:apply-templates>
                <xsl:with-param name="expressionLanguage" select="$expressionLanguage"/>
                <xsl:with-param name="queryLanguage" select="$queryLanguage"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <!-- Adding missing queryLanguge attributes -->
    <xsl:template match="bpel:query">
        <xsl:param name="expressionLanguage"/>
        <xsl:param name="queryLanguage"/>
        <xsl:copy>
            <xsl:copy-of select="@*"/>

            <!-- if queryLanguage attribute is missing add it -->
            <xsl:if test="not(@queryLanguage)">
                <xsl:attribute name="queryLanguage">
                    <xsl:value-of select="$queryLanguage"/>
                </xsl:attribute>
            </xsl:if>

            <xsl:apply-templates>
                <xsl:with-param name="expressionLanguage" select="$expressionLanguage"/>
                <xsl:with-param name="queryLanguage" select="$queryLanguage"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>