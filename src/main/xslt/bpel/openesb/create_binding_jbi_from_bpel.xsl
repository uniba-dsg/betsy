<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable"
                xmlns:serviceunit="http://www.sun.com/jbi/descriptor/service-unit"

                xmlns:xsd="http://www.w3.org/2001/XMLSchema">

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/bpel:process">

        <!-- variables from process -->
        <xsl:variable name="bpelName" select="@name" as="xsd:string"/>
        <xsl:variable name="bpelNamespace" select="@targetNamespace" as="xsd:string"/>

        <!-- variables dependent on <process name="" targetNamespace="" ...> -->
        <xsl:variable name="bpelNameWithFileExtension" select="concat($bpelName,'.bpel')" as="xsd:string"/>
        <xsl:variable name="bpelNamespacePrefix" select="'composite'" as="xsd:string" />
        <xsl:variable name="bpelNameWithPrefix" select="concat($bpelNamespacePrefix,':',$bpelName)" as="xsd:string" />

        <jbi xmlns="http://java.sun.com/xml/ns/jbi"
             xmlns:ti="http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface"
             version="1.0">

            <xsl:namespace name="{$bpelNamespacePrefix}" select="$bpelNamespace" />
            <xsl:namespace name="ti" select="'http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface'"/>
            <xsl:namespace name="tp" select="'http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner'"/>

            <services binding-component="true">

                <xsl:if test="//bpel:partnerLink[@partnerRole='testPartnerRole']">
                    <provides endpoint-name="TestPort" interface-name="tp:TestPartnerPortType" service-name="tp:TestService"/>
                </xsl:if>

                <consumes endpoint-name="TestInterfacePort" interface-name="ti:TestInterfacePortType" service-name="ti:{$bpelName}TestInterfaceService"/>
            </services>

        </jbi>

    </xsl:template>

</xsl:stylesheet>