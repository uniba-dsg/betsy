<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- get the namespace uri for the namespace identified by the prefix in the parameter -->
    <xsl:template name="get-ns-name">
        <xsl:param name="ns-prefix"/>
        <xsl:variable name="ns-node" select="namespace::node()[local-name()=$ns-prefix]"/>
        <xsl:value-of select="$ns-node"/>
    </xsl:template>


</xsl:stylesheet>