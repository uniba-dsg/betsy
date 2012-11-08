<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable">

    <xsl:output indent="yes" method="xml"/>

    <xsl:template match="/bpel:process">

        <!-- variables from process -->
        <xsl:variable name="bpelName" select="@name" as="xsd:string"/>
        <xsl:variable name="bpelNamespace" select="@targetNamespace" as="xsd:string"/>

        <!-- variables dependent on <process name="" targetNamespace="" ...> -->
        <xsl:variable name="bpelNameWithFileExtension" select="concat($bpelName,'.bpel')" as="xsd:string"/>
        <xsl:variable name="bpelNamespacePrefix" select="'composite'" as="xsd:string" />
        <xsl:variable name="bpelNameWithPrefix" select="concat($bpelNamespacePrefix,':',$bpelName)" as="xsd:string" />

        <jbi xmlns="http://java.sun.com/xml/ns/jbi"
             version="1.0">

            <xsl:namespace name="{$bpelNamespacePrefix}" select="$bpelNamespace" />
            <xsl:namespace name="ti" select="'http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface'"/>
            <xsl:namespace name="tp" select="'http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner'"/>

            <service-assembly>
                <identification>
                    <name><xsl:value-of select="$bpelName"/>Application</name>
                    <description>Represents the Service Assembly of <xsl:value-of select="$bpelName"/>Application</description>
                </identification>
                <service-unit>
                    <identification>
                        <name><xsl:value-of select="$bpelName"/>Application</name>
                        <description>Represents this Service Unit</description>
                    </identification>
                    <target>
                        <artifacts-zip><xsl:value-of select="$bpelName"/>.zip</artifacts-zip>
                        <component-name>petals-se-bpel</component-name>
                    </target>
                </service-unit>
                <service-unit>
                    <identification>
                        <name><xsl:value-of select="$bpelName"/>Binding</name>
                        <description>Represents this Service Unit</description>
                    </identification>
                    <target>
                        <artifacts-zip><xsl:value-of select="$bpelName"/>Binding.zip</artifacts-zip>
                        <component-name>petals-bc-soap</component-name>
                    </target>
                </service-unit>
            </service-assembly>
        </jbi>

    </xsl:template>

</xsl:stylesheet>