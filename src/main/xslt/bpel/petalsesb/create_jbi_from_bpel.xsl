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
             xmlns:cdk5="http://petals.ow2.org/components/extensions/version-5"
             xmlns:bpel-engine="http://petals.ow2.org/components/petals-bpel-engine/version-1"
             xmlns:soap="http://petals.ow2.org/components/soap/version-4"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             version="1.0">

            <xsl:namespace name="{$bpelNamespacePrefix}" select="$bpelNamespace" />
            <xsl:namespace name="ti" select="'http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface'"/>
            <xsl:namespace name="tp" select="'http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner'"/>

            <services binding-component="false">

                <provides endpoint-name="TestInterfacePort"
                          interface-name="ti:TestInterfacePortType"
                          service-name="ti:{$bpelName}TestInterfaceService">

                    <cdk5:timeout>30000</cdk5:timeout>
                    <cdk5:validate-wsdl>true</cdk5:validate-wsdl>
                    <cdk5:forward-security-subject>false</cdk5:forward-security-subject>
                    <cdk5:forward-message-properties>false</cdk5:forward-message-properties>
                    <cdk5:forward-attachments>false</cdk5:forward-attachments>
                    <cdk5:wsdl>TestInterface.wsdl</cdk5:wsdl>
                    <bpel-engine:bpel><xsl:value-of select="$bpelName"/>.bpel</bpel-engine:bpel>
                    <bpel-engine:poolsize>1</bpel-engine:poolsize>

                </provides>
                


            </services>

        </jbi>

    </xsl:template>

</xsl:stylesheet>