<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable"
        >

    <xsl:output method="text"/>

    <xsl:template match="/bpel:process"><xsl:variable name="bpelName" select="@name" as="xsd:string"/>Manifest-Version: 1.0
Bundle-Name: <xsl:value-of select="$bpelName"/>Application
Bundle-SymbolicName: <xsl:value-of select="$bpelName"/>Application
Bundle-ManifestVersion: 2
Bundle-Version: 1.0.0
    </xsl:template>

</xsl:stylesheet>