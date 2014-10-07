<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable">

    <xsl:output indent="yes" method="xml"/>

    <xsl:template match="/bpel:process">
        <catalog xmlns="http://schemas.active-endpoints.com/catalog/2006/07/catalog.xsd">
                <xsl:apply-templates select="bpel:import" />
        </catalog>
    </xsl:template>

    <xsl:template match="//bpel:import" name="getImports">
        <xsl:choose>
            <xsl:when test="@importType='http://www.w3.org/2001/XMLSchema'">
                <schemaEntry xmlns="http://schemas.active-endpoints.com/catalog/2006/07/catalog.xsd">
                    <xsl:attribute name="location">
                        <xsl:value-of select="concat('project:',@location)" />
                    </xsl:attribute>
                    <xsl:attribute name="classpath">
                        <xsl:value-of select="@location" />
                    </xsl:attribute>
                </schemaEntry>
            </xsl:when>

            <xsl:when test="@importType='http://schemas.xmlsoap.org/wsdl/'">
                <wsdlEntry xmlns="http://schemas.active-endpoints.com/catalog/2006/07/catalog.xsd">
                    <xsl:attribute name="location">
                        <xsl:value-of select="concat('project:',substring(@location,4))" />
                    </xsl:attribute>
                    <xsl:attribute name="classpath">
                        <xsl:value-of select="substring(@location,4)" />
                    </xsl:attribute>
                </wsdlEntry>
            </xsl:when>

        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>