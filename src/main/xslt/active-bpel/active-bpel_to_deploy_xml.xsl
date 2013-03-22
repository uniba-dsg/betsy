<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable">

    <xsl:output indent="yes" method="xml"/>


    <xsl:template match="/bpel:process">

        <process xmlns="http://schemas.active-endpoints.com/pdd/2006/08/pdd.xsd" name="tns:{attribute::name}" platform="opensource" location="{attribute::name}.bpel">
                <xsl:namespace name="tns" select="string(@targetNamespace)"/>
                <partnerLinks>
                    <xsl:apply-templates select="bpel:partnerLinks"/>
                    <xsl:apply-templates select="bpel:scope" />
                </partnerLinks>
                <references>
                    <xsl:apply-templates select="bpel:import" />
                </references>
        </process>
    </xsl:template>

    <xsl:template match="//bpel:import" name="getImports">
        <xsl:choose>
            <xsl:when test="@importType='http://schemas.xmlsoap.org/wsdl/'">
                <wsdl xmlns="http://schemas.active-endpoints.com/pdd/2006/08/pdd.xsd" namespace="{attribute::namespace}">
                    <xsl:attribute name="location">
                    <xsl:value-of select="concat('project:',substring(@location,4))" />
                    </xsl:attribute>
                </wsdl>
            </xsl:when>

            <xsl:when test="@importType='http://www.w3.org/2001/XMLSchema'">
                <schema xmlns="http://schemas.active-endpoints.com/pdd/2006/08/pdd.xsd" namespace="{attribute::namespace}">
                    <xsl:attribute name="location">
                        <xsl:value-of select="concat('project:',@location)" />
                    </xsl:attribute>
                </schema>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="//bpel:partnerLink" name="getPartnerLinks">

        <xsl:if test="attribute::myRole">
            <partnerLink xmlns="http://schemas.active-endpoints.com/pdd/2006/08/pdd.xsd" name="{string(attribute::name)}">
               <myRole allowedRoles="" binding="MSG" >
                   <xsl:attribute name="service">
                        <xsl:value-of select="concat(string(ancestor::bpel:process/@name),'TestInterfaceService')" />
                    </xsl:attribute>
               </myRole>
            </partnerLink>
        </xsl:if>

        <xsl:if test="attribute::partnerRole">
            <partnerLink  xmlns="http://schemas.active-endpoints.com/pdd/2006/08/pdd.xsd" name="{string(attribute::name)}">
                <partnerRole endpointReference="static" invokeHandler="default:Address">
                    <wsa:EndpointReference xmlns:wsa="http://www.w3.org/2005/08/addressing" xmlns:s="http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner">
                        <wsa:Address>http://PARTNER_IP_AND_PORT/bpel-testpartner</wsa:Address>
                        <wsa:ServiceName PortName="TestPort">s:TestService</wsa:ServiceName>
                    </wsa:EndpointReference>
                </partnerRole>
            </partnerLink>
        </xsl:if>

    </xsl:template>

    <xsl:template match="//bpel:scope" name="getScopes">
        <xsl:apply-templates select="bpel:partnerLinks"/>
    </xsl:template>

    <!-- Override default template for copying text -->
    <xsl:template match="text()|@*" />

</xsl:stylesheet>