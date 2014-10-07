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

            <services binding-component="true"
                      xmlns:cdk5="http://petals.ow2.org/components/extensions/version-5"
                      xmlns:soap="http://petals.ow2.org/components/soap/version-4"
                      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

                <xsl:if test="//bpel:partnerLink[@partnerRole='testPartnerRole']">
                    <provides endpoint-name="TestPort" interface-name="tp:TestPartnerPortType" service-name="tp:{$bpelName}TestService">
                        <!--CDK Properties-->
                        <cdk5:validate-wsdl>true</cdk5:validate-wsdl>
                        <cdk5:wsdl>TestPartner.wsdl</cdk5:wsdl>

                        <!-- Component specific elements -->
                        <soap:address>http://PARTNER_IP_AND_PORT/bpel-testpartner</soap:address>
                        <soap:soap-version>1.1</soap:soap-version>
                        <soap:chunked-mode>false</soap:chunked-mode>
                        <soap:cleanup-transport>true</soap:cleanup-transport>
                        <soap:mode>SOAP</soap:mode>
                    </provides>
                </xsl:if>

                <consumes endpoint-name="TestInterfacePort" interface-name="ti:TestInterfacePortType" service-name="ti:{$bpelName}TestInterfaceService">

                    <!--CDK Properties-->
                    <cdk5:timeout>30000</cdk5:timeout>
                    <cdk5:operation>ti:no-operation</cdk5:operation>
                    <cdk5:mep xsi:nil="true"/>

                    <!--Component's Specific Properties-->
                    <soap:service-name><xsl:value-of select="$bpelName"/>TestInterfaceService</soap:service-name>
                    <soap:mode>SOAP</soap:mode>
                    <soap:enable-http-transport>true</soap:enable-http-transport>
                    <soap:enable-jms-transport>false</soap:enable-jms-transport>

                </consumes>
            </services>

        </jbi>

    </xsl:template>

</xsl:stylesheet>