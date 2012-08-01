<?xml version='1.0' encoding='UTF-8' ?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="urn:stylesheets">

    <!-- required to be used further in BPEL -->
    <xsl:output method="xml"/>

    <!-- identity template, simply copies everything -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:call-template name="doesNotExist"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>