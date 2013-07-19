<?xml version='1.0' encoding='UTF-8' ?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- required to be used further in BPEL -->
    <xsl:output method="xml"/>

    <!-- identity template, simply copies everything -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>