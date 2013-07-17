<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Utility templates to make temporary variables and assignments, due to the
     use of <toParts>, <fromParts>, and/or references to element variables,
     explicit. -->
<!-- NB: this template should _not_ be applied by itself. -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable"
  xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
  xmlns:plnk="http://docs.oasis-open.org/wsbpel/2.0/plnktype">

  <xsl:include href="constants.xsl" />
  <xsl:include href="fresh-names.xsl" />

  <!-- Get the WSDL message name for either the input or output message of the
       operation in question. 
       Assumes that the context node is a message activity. -->
  <xsl:template name="wsdl-message-name">
    <!-- If $input = true then the input message type is returned,
         otherwise the output message type is returned. -->
    <xsl:param name="input" />
    <!-- If $myRole = true then the operation is provided by this process,
         otherwise it is provided by the partner. -->
    <xsl:param name="myRole" />
    
    <xsl:variable name="partnerLinkName" select="@partnerLink" />
    <xsl:variable name="operation" select="@operation" />
    <xsl:variable name="partnerLink" select="(self::*[self::bpel:invoke or self::bpel:onMessage or self::bpel:receive or self::bpel:reply] |
                                              self::bpel:onEvent/bpel:scope)
                                             /ancestor-or-self::*/bpel:partnerLinks/bpel:partnerLink[@name=$partnerLinkName][1]" />
    <xsl:variable name="partnerLinkNamespace" select="namespace::*[local-name() = substring-before($partnerLink/@partnerLinkType, ':')]" />
    <xsl:variable name="definitions" select="document(/bpel:process/bpel:import[@importType='http://schemas.xmlsoap.org/wsdl/']/@location)/wsdl:definitions[@targetNamespace=$partnerLinkNamespace]" />
    <xsl:variable name="portType">
      <xsl:choose>
        <xsl:when test="$myRole">
          <xsl:value-of select="substring-after($definitions/plnk:partnerLinkType[@name=substring-after($partnerLink/@partnerLinkType, ':')]/plnk:role[@name=$partnerLink/@myRole]/@portType, ':')" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="substring-after($definitions/plnk:partnerLinkType[@name=substring-after($partnerLink/@partnerLinkType, ':')]/plnk:role[@name=$partnerLink/@partnerRole]/@portType, ':')" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="message" select="$definitions/wsdl:portType[$input and @name=$portType]/wsdl:operation[@name=$operation]/wsdl:input |
                                         $definitions/wsdl:portType[not($input) and @name=$portType]/wsdl:operation[@name=$operation]/wsdl:output" />
    <!-- Expand the namespace -->
    <xsl:variable name="messageNamespace" select="$message/namespace::*[local-name() = substring-before($message/@message, ':')]" />
    <xsl:variable name="namespacePrefix" select="name(namespace::*[self::node() = $messageNamespace][1])" />
    <xsl:value-of select="concat($namespacePrefix, ':', substring-after($message/@message, ':'))" />
  </xsl:template>

  <!-- Get the WSDL name of _the_ part in either the input or output message of
       the operation in question. 
       Assumes that the context node is a message activity. -->
  <xsl:template name="wsdl-message-part-name">
    <!-- If $input = true then the name of the part in the input message type is returned,
         otherwise the output message part name is returned. -->
    <xsl:param name="input" />
    <!-- If $myRole = true then the operation is provided by this process,
         otherwise it is provided by the partner. -->
    <xsl:param name="myRole" />
    
    <xsl:variable name="partnerLinkName" select="@partnerLink" />
    <xsl:variable name="operation" select="@operation" />
    <xsl:variable name="partnerLink" select="(self::*[self::bpel:invoke or self::bpel:onMessage or self::bpel:receive or self::bpel:reply] |
                                              self::bpel:onEvent/bpel:scope)
                                             /ancestor-or-self::*/bpel:partnerLinks/bpel:partnerLink[@name=$partnerLinkName][1]" />
    <xsl:variable name="partnerLinkNamespace" select="namespace::*[local-name() = substring-before($partnerLink/@partnerLinkType, ':')]" />
    <xsl:variable name="definitions" select="document(/bpel:process/bpel:import[@importType='http://schemas.xmlsoap.org/wsdl/']/@location)/wsdl:definitions[@targetNamespace=$partnerLinkNamespace]" />
    <xsl:variable name="portType">
      <xsl:choose>
        <xsl:when test="$myRole">
          <xsl:value-of select="substring-after($definitions/plnk:partnerLinkType[@name=substring-after($partnerLink/@partnerLinkType, ':')]/plnk:role[@name=$partnerLink/@myRole]/@portType, ':')" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="substring-after($definitions/plnk:partnerLinkType[@name=substring-after($partnerLink/@partnerLinkType, ':')]/plnk:role[@name=$partnerLink/@partnerRole]/@portType, ':')" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="message">
      <xsl:choose>
        <xsl:when test="$input">
          <xsl:value-of select="substring-after($definitions/wsdl:portType[@name=$portType]/wsdl:operation[@name=$operation]/wsdl:input/@message, ':')" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="substring-after($definitions/wsdl:portType[@name=$portType]/wsdl:operation[@name=$operation]/wsdl:output/@message, ':')" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:value-of select="$definitions/wsdl:message[@name=$message]/wsdl:part/@name" />
  </xsl:template>

  <!-- Get the WSDL typing attribute of the given part in either the input or
       output message of the operation in question. 
       By default assumes that the context node is a message activity. -->
  <xsl:template name="wsdl-message-part-typing">
    <xsl:param name="messageActivity" select="." />
    <xsl:param name="part" />
    <!-- If $input = true then the name of the part in the input message type is returned,
         otherwise the output message part name is returned. -->
    <xsl:param name="input" />
    <!-- If $myRole = true then the operation is provided by this process,
         otherwise it is provided by the partner. -->
    <xsl:param name="myRole" />
    
    <xsl:variable name="partnerLinkName" select="$messageActivity/@partnerLink" />
    <xsl:variable name="operation" select="$messageActivity/@operation" />
    <xsl:variable name="partnerLink" select="($messageActivity/self::*[self::bpel:invoke or self::bpel:onMessage or self::bpel:receive or self::bpel:reply] |
                                              $messageActivity/self::bpel:onEvent/bpel:scope)
                                             /ancestor-or-self::*/bpel:partnerLinks/bpel:partnerLink[@name=$partnerLinkName][1]" />
    <xsl:variable name="partnerLinkNamespace" select="$messageActivity/namespace::*[local-name() = substring-before($partnerLink/@partnerLinkType, ':')]" />
    <xsl:variable name="definitions" select="document(/bpel:process/bpel:import[@importType='http://schemas.xmlsoap.org/wsdl/']/@location)/wsdl:definitions[@targetNamespace=$partnerLinkNamespace]" />
    <xsl:variable name="portType">
      <xsl:choose>
        <xsl:when test="$myRole">
          <xsl:value-of select="substring-after($definitions/plnk:partnerLinkType[@name=substring-after($partnerLink/@partnerLinkType, ':')]/plnk:role[@name=$partnerLink/@myRole]/@portType, ':')" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="substring-after($definitions/plnk:partnerLinkType[@name=substring-after($partnerLink/@partnerLinkType, ':')]/plnk:role[@name=$partnerLink/@partnerRole]/@portType, ':')" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="message">
      <xsl:choose>
        <xsl:when test="$input">
          <xsl:value-of select="substring-after($definitions/wsdl:portType[@name=$portType]/wsdl:operation[@name=$operation]/wsdl:input/@message, ':')" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="substring-after($definitions/wsdl:portType[@name=$portType]/wsdl:operation[@name=$operation]/wsdl:output/@message, ':')" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:copy-of select="$definitions/wsdl:message[@name=$message]/wsdl:part[@name=$part]/@*[namespace-uri() = '' and
                                                                                             (local-name() = 'type' or
                                                                                              local-name() = 'element')]" />
  </xsl:template>

  <!-- Determine whether the WSDL part has an element attribute in either the
       input or output message of the operation in question. 
       By default assumes that the context node is a message activity. -->
  <xsl:template name="wsdl-message-part-is-element">
    <xsl:param name="messageActivity" select="." />
    <xsl:param name="part" />
    <!-- If $input = true then the name of the part in the input message type is returned,
         otherwise the output message part name is returned. -->
    <xsl:param name="input" />
    <!-- If $myRole = true then the operation is provided by this process,
         otherwise it is provided by the partner. -->
    <xsl:param name="myRole" />
    
    <xsl:variable name="partnerLinkName" select="$messageActivity/@partnerLink" />
    <xsl:variable name="operation" select="$messageActivity/@operation" />
    <xsl:variable name="partnerLink" select="(self::*[self::bpel:invoke or self::bpel:onMessage or self::bpel:receive or self::bpel:reply] |
                                              self::bpel:onEvent/bpel:scope)
                                             /ancestor-or-self::*/bpel:partnerLinks/bpel:partnerLink[@name=$partnerLinkName][1]" />
    <xsl:variable name="partnerLinkNamespace" select="$messageActivity/namespace::*[local-name() = substring-before($partnerLink/@partnerLinkType, ':')]" />
    <xsl:variable name="definitions" select="document(/bpel:process/bpel:import[@importType='http://schemas.xmlsoap.org/wsdl/']/@location)/wsdl:definitions[@targetNamespace=$partnerLinkNamespace]" />
    <xsl:variable name="portType">
      <xsl:choose>
        <xsl:when test="$myRole">
          <xsl:value-of select="substring-after($definitions/plnk:partnerLinkType[@name=substring-after($partnerLink/@partnerLinkType, ':')]/plnk:role[@name=$partnerLink/@myRole]/@portType, ':')" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="substring-after($definitions/plnk:partnerLinkType[@name=substring-after($partnerLink/@partnerLinkType, ':')]/plnk:role[@name=$partnerLink/@partnerRole]/@portType, ':')" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="message">
      <xsl:choose>
        <xsl:when test="$input">
          <xsl:value-of select="substring-after($definitions/wsdl:portType[@name=$portType]/wsdl:operation[@name=$operation]/wsdl:input/@message, ':')" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="substring-after($definitions/wsdl:portType[@name=$portType]/wsdl:operation[@name=$operation]/wsdl:output/@message, ':')" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:value-of select="boolean($definitions/wsdl:message[@name=$message]/wsdl:part[@name=$part]/@element)" />
  </xsl:template>


  <!-- Figure out what type an activity's outbound message has.
       Assumes that the context node is a message activity. -->
  <xsl:template name="outbound-message-type">
    <xsl:choose>
      <xsl:when test="self::bpel:invoke">
        <xsl:call-template name="wsdl-message-name">
          <xsl:with-param name="input" select="true()" />
          <xsl:with-param name="myRole" select="false()" />
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="self::bpel:reply">
        <xsl:call-template name="wsdl-message-name">
          <xsl:with-param name="input" select="false()" />
          <xsl:with-param name="myRole" select="true()" />
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- Figure out what type an activity's inbound message has.
       Assumes that the context node is a message activity. -->
  <xsl:template name="inbound-message-type">
    <xsl:choose>
      <xsl:when test="self::bpel:invoke">
        <xsl:call-template name="wsdl-message-name">
          <xsl:with-param name="input" select="false()" />
          <xsl:with-param name="myRole" select="false()" />
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="self::bpel:onMessage or self::bpel:receive or self::bpel:onEvent">
        <xsl:call-template name="wsdl-message-name">
          <xsl:with-param name="input" select="true()" />
          <xsl:with-param name="myRole" select="true()" />
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- Figure out what part an activity's outbound message has.
       Assumes that the context node is a message activity. -->
  <xsl:template name="outbound-message-part">
    <xsl:choose>
      <xsl:when test="self::bpel:invoke">
        <xsl:call-template name="wsdl-message-part-name">
          <xsl:with-param name="input" select="true()" />
          <xsl:with-param name="myRole" select="false()" />
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="self::bpel:reply">
        <xsl:call-template name="wsdl-message-part-name">
          <xsl:with-param name="input" select="false()" />
          <xsl:with-param name="myRole" select="true()" />
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- Figure out what part an activity's inbound message has.
       Assumes that the context node is a message activity. -->
  <xsl:template name="inbound-message-part">
    <xsl:choose>
      <xsl:when test="self::bpel:invoke">
        <xsl:call-template name="wsdl-message-part-name">
          <xsl:with-param name="input" select="false()" />
          <xsl:with-param name="myRole" select="false()" />
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="self::bpel:onMessage or self::bpel:receive or self::bpel:onEvent">
        <xsl:call-template name="wsdl-message-part-name">
          <xsl:with-param name="input" select="true()" />
          <xsl:with-param name="myRole" select="true()" />
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- Figure out what typing a named part of an activity's inbound message has.
       By default assumes that the context node is a message activity. -->
  <xsl:template name="inbound-message-part-typing">
    <xsl:param name="messageActivity" select="." />
    <xsl:param name="part" />
    <xsl:choose>
      <xsl:when test="$messageActivity/self::bpel:invoke">
        <xsl:call-template name="wsdl-message-part-typing">
          <xsl:with-param name="messageActivity" select="$messageActivity" />
          <xsl:with-param name="part" select="$part" />
          <xsl:with-param name="input" select="false()" />
          <xsl:with-param name="myRole" select="false()" />
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="$messageActivity/self::bpel:onMessage or $messageActivity/self::bpel:receive or $messageActivity/self::bpel:onEvent">
        <xsl:call-template name="wsdl-message-part-typing">
          <xsl:with-param name="messageActivity" select="$messageActivity" />
          <xsl:with-param name="part" select="$part" />
          <xsl:with-param name="input" select="true()" />
          <xsl:with-param name="myRole" select="true()" />
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- Figure out what whether a named part of an activity's inbound message is
       an element.
       By default assumes that the context node is a message activity. -->
  <xsl:template name="inbound-message-part-is-element">
    <xsl:param name="messageActivity" select="." />
    <xsl:param name="part" />
    <xsl:choose>
      <xsl:when test="$messageActivity/self::bpel:invoke">
        <xsl:call-template name="wsdl-message-part-is-element">
          <xsl:with-param name="messageActivity" select="$messageActivity" />
          <xsl:with-param name="part" select="$part" />
          <xsl:with-param name="input" select="false()" />
          <xsl:with-param name="myRole" select="false()" />
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="$messageActivity/self::bpel:onMessage or $messageActivity/self::bpel:receive or $messageActivity/self::bpel:onEvent">
        <xsl:call-template name="wsdl-message-part-is-element">
          <xsl:with-param name="messageActivity" select="$messageActivity" />
          <xsl:with-param name="part" select="$part" />
          <xsl:with-param name="input" select="true()" />
          <xsl:with-param name="myRole" select="true()" />
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- Figure out what whether a named part of an activity's outbound message is
       an element.
       By default assumes that the context node is a message activity. -->
  <xsl:template name="outbound-message-part-is-element">
    <xsl:param name="messageActivity" select="." />
    <xsl:param name="part" />
    <xsl:choose>
      <xsl:when test="$messageActivity/self::bpel:invoke">
        <xsl:call-template name="wsdl-message-part-is-element">
          <xsl:with-param name="messageActivity" select="$messageActivity" />
          <xsl:with-param name="part" select="$part" />
          <xsl:with-param name="input" select="true()" />
          <xsl:with-param name="myRole" select="false()" />
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="$messageActivity/self::bpel:reply">
        <xsl:call-template name="wsdl-message-part-is-element">
          <xsl:with-param name="messageActivity" select="$messageActivity" />
          <xsl:with-param name="part" select="$part" />
          <xsl:with-param name="input" select="false()" />
          <xsl:with-param name="myRole" select="true()" />
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- Make explicit that the message parts are copied to a temporary variable. 
       Assumes that the context node is a message activity. -->
  <xsl:template name="copy-to-parts-explicitly">
    <xsl:variable name="uniqueElementName">
      <xsl:call-template name="unique-element-name">
        <xsl:with-param name="element" select="." />
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:for-each select="bpel:toParts">
      <bpel:assign>
        <xsl:for-each select="bpel:toPart">
          <bpel:copy>
            <xsl:variable name="variableName" select="@fromVariable" />
            
            <!-- Are both source and target elements? -->
            <xsl:variable name="messagePartIsElement">
              <xsl:call-template name="outbound-message-part-is-element">
                <xsl:with-param name="messageActivity" select="../.." />
                <xsl:with-param name="part" select="@part" />
              </xsl:call-template>
            </xsl:variable>
            <xsl:if test="$messagePartIsElement = 'true' and ancestor::*/bpel:variables/bpel:variable[@name=$variableName][1]/@element">
              <xsl:attribute name="keepSrcElementName">yes</xsl:attribute>
            </xsl:if>

            <bpel:from>
              <xsl:attribute name="variable">
                <xsl:value-of select="@fromVariable" />
              </xsl:attribute>
            </bpel:from>
            <bpel:to>
              <xsl:attribute name="variable">
                <xsl:value-of select="concat($uniqueElementName, $tmp-input-message-variable-postfix)" />
              </xsl:attribute>
              <xsl:attribute name="part">
                <xsl:value-of select="@part" />
              </xsl:attribute>
            </bpel:to>
          </bpel:copy>
        </xsl:for-each>
      </bpel:assign>
    </xsl:for-each>
  </xsl:template>

  <!-- Make explicit that the message parts are copied from a temporary variable. 
       Assumes that the context node is a message activity. -->
  <xsl:template name="copy-from-parts-explicitly">
    <xsl:variable name="uniqueElementName">
      <xsl:call-template name="unique-element-name">
        <xsl:with-param name="element" select="." />
      </xsl:call-template>
    </xsl:variable>

    <xsl:for-each select="bpel:fromParts">
      <bpel:assign>
        <xsl:for-each select="bpel:fromPart">
          <bpel:copy>
            <xsl:variable name="variableName" select="@toVariable" />
            
            <!-- Are both source and target elements? -->
            <xsl:variable name="messagePartIsElement">
              <xsl:call-template name="inbound-message-part-is-element">
                <xsl:with-param name="messageActivity" select="../.." />
                <xsl:with-param name="part" select="@part" />
              </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="variableIsElement">
              <xsl:choose>
                <xsl:when test="../../self::bpel:onEvent"><xsl:value-of select="$messagePartIsElement" /></xsl:when>
                <xsl:otherwise><xsl:value-of select="ancestor::*/bpel:variables/bpel:variable[@name=$variableName][1]/@element" /></xsl:otherwise>
              </xsl:choose>
            </xsl:variable>
            <xsl:if test="$messagePartIsElement = 'true' and $variableIsElement = 'true'">
              <xsl:attribute name="keepSrcElementName">yes</xsl:attribute>
            </xsl:if>
            
            <bpel:from>
              <xsl:attribute name="variable">
                <xsl:value-of select="concat($uniqueElementName, $tmp-output-message-variable-postfix)" />
              </xsl:attribute>
              <xsl:attribute name="part">
                <xsl:value-of select="@part" />
              </xsl:attribute>
            </bpel:from>
            <bpel:to>
              <xsl:attribute name="variable">
                <xsl:value-of select="@toVariable" />
              </xsl:attribute>
            </bpel:to>
          </bpel:copy>
        </xsl:for-each>
      </bpel:assign>
    </xsl:for-each>
  </xsl:template>

  <!-- Make explicit that the input element is copied to the single part of a
       temporary variable.
       Assumes that the context node is a message activity. -->
  <xsl:template name="copy-input-element-explicitly">
    <xsl:param name="inputVariable" />
    
    <xsl:variable name="tmpInputMessageName">
      <xsl:call-template name="unique-element-name">
        <xsl:with-param name="element" select="." />
        <xsl:with-param name="postfix" select="$tmp-input-message-variable-postfix" />
      </xsl:call-template>
    </xsl:variable>
    
    <bpel:assign>
      <bpel:copy keepSrcElementName="yes">
        <bpel:from>
          <xsl:attribute name="variable">
            <xsl:value-of select="$inputVariable" />
          </xsl:attribute>
        </bpel:from>
        <bpel:to>
          <xsl:attribute name="variable">
            <xsl:value-of select="$tmpInputMessageName" />
          </xsl:attribute>
          <xsl:attribute name="part">
            <xsl:call-template name="outbound-message-part" />
          </xsl:attribute>
        </bpel:to>
      </bpel:copy>
    </bpel:assign>
  </xsl:template>

  <!-- Make explicit that the output element is copied to the single part of a
       temporary variable. 
       Assumes that the context node is a message activity. -->
  <xsl:template name="copy-output-element-explicitly">
    <xsl:param name="outputVariable" />
    
    <xsl:variable name="tmpOutputMessageName">
      <xsl:call-template name="unique-element-name">
        <xsl:with-param name="element" select="." />
        <xsl:with-param name="postfix" select="$tmp-output-message-variable-postfix" />
      </xsl:call-template>
    </xsl:variable>
    
    <bpel:assign>
      <bpel:copy keepSrcElementName="yes">
        <bpel:from>
          <xsl:attribute name="variable">
            <xsl:value-of select="$tmpOutputMessageName" />
          </xsl:attribute>
          <xsl:attribute name="part">
            <xsl:call-template name="inbound-message-part" />
          </xsl:attribute>
        </bpel:from>
        <bpel:to>
          <xsl:attribute name="variable">
            <xsl:value-of select="$outputVariable" />
          </xsl:attribute>
        </bpel:to>
      </bpel:copy>
    </bpel:assign>
  </xsl:template>

  <!-- Create temporary variables for the current messaging activity if it
       uses <toParts>, <fromParts> or element variables. 
       Assumes that the context node is a message activity. -->
  <xsl:template name="message-activity-temp-variables">
    <xsl:param name="inputVariable" />
    <xsl:param name="outputVariable" />
  
    <xsl:variable name="uniqueElementName">
      <xsl:call-template name="unique-element-name">
        <xsl:with-param name="element" select="." />
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="inputElement" select="count(ancestor::*/bpel:variables/bpel:variable[@name=$inputVariable][1]/@element) = 1" />
    <xsl:variable name="outputElement" select="count(self::*[self::bpel:invoke or self::bpel:onMessage or self::bpel:receive or self::bpel:reply]/ancestor::*/bpel:variables/bpel:variable[@name=$outputVariable][1]/@element |
                                                     self::bpel:onEvent[@element]) = 1" />

    <xsl:if test="bpel:toParts or $inputElement">
      <bpel:variable>
        <xsl:attribute name="name">
          <xsl:value-of select="concat($uniqueElementName, $tmp-input-message-variable-postfix)" />
        </xsl:attribute>
        <xsl:attribute name="messageType">
          <xsl:call-template name="outbound-message-type" />
        </xsl:attribute>
      </bpel:variable>
    </xsl:if>
    <xsl:if test="bpel:fromParts or $outputElement">
      <bpel:variable>
        <xsl:attribute name="name">
          <xsl:value-of select="concat($uniqueElementName, $tmp-output-message-variable-postfix)" />
        </xsl:attribute>
        <xsl:attribute name="messageType">
          <xsl:call-template name="inbound-message-type" />
        </xsl:attribute>
      </bpel:variable>
    </xsl:if>
  </xsl:template>
  
  <!-- Create temporary variables for the given set of message activities. -->
  <xsl:template name="message-activities-temp-variables">
    <xsl:param name="messageActivities" />

    <xsl:for-each select="$messageActivities">
      <xsl:call-template name="message-activity-temp-variables">
        <xsl:with-param name="inputVariable">
          <xsl:choose>
            <xsl:when test="self::bpel:invoke">
              <xsl:value-of select="@inputVariable" />
            </xsl:when>
            <xsl:when test="self::bpel:reply">
              <xsl:value-of select="@variable" />
            </xsl:when>
          </xsl:choose>
        </xsl:with-param>
        <xsl:with-param name="outputVariable">
          <xsl:choose>
            <xsl:when test="self::bpel:invoke">
              <xsl:value-of select="@outputVariable" />
            </xsl:when>
            <xsl:when test="self::bpel:onMessage or self::bpel:receive or self::bpel:onEvent">
              <xsl:value-of select="@variable" />
            </xsl:when>
          </xsl:choose>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>

  <!-- Decide whether temporary variables are needed for the current messaging
       activity if it uses <toParts>, <fromParts> or element variables.
       By default, assumes that the context node is a message activity. -->
  <xsl:template name="message-activity-needs-temp-variables">
    <xsl:param name="activity" select="." />
    <xsl:param name="inputVariable" />
    <xsl:param name="outputVariable" />

    <xsl:variable name="inputElement" select="count($activity/ancestor::*/bpel:variables/bpel:variable[@name=$inputVariable][1]/@element) = 1" />
    <xsl:variable name="outputElement" select="count($activity/self::*[self::bpel:invoke or self::bpel:onMessage or self::bpel:receive or self::bpel:reply]/ancestor::*/bpel:variables/bpel:variable[@name=$outputVariable][1]/@element |
                                                     $activity/self::bpel:onEvent[@element]) = 1" />

    <xsl:value-of select="$activity/bpel:toParts or $inputElement or $activity/bpel:fromParts or $outputElement" />
  </xsl:template>
  
  <!-- Decide whether temporary variables are needed for the given set of message activities. -->
  <xsl:template name="message-activities-need-temp-variables">
    <xsl:param name="messageActivities" />
    
    <xsl:choose>
      <xsl:when test="0 = count($messageActivities/*)">
        <xsl:value-of select="false()" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="first-activity" select="$messageActivities[1]" />
        <xsl:variable name="first-activity-needs-temp-variable">
          <xsl:call-template name="message-activity-needs-temp-variables">
            <xsl:with-param name="activity" select="$first-activity" />
            <xsl:with-param name="inputVariable">
              <xsl:choose>
                <xsl:when test="$first-activity[self::bpel:invoke]">
                  <xsl:value-of select="$first-activity/@inputVariable" />
                </xsl:when>
                <xsl:when test="$first-activity[self::bpel:reply]">
                  <xsl:value-of select="$first-activity/@variable" />
                </xsl:when>
              </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="outputVariable">
              <xsl:choose>
                <xsl:when test="$first-activity[self::bpel:invoke]">
                  <xsl:value-of select="$first-activity/@outputVariable" />
                </xsl:when>
                <xsl:when test="$first-activity[self::bpel:onMessage or self::bpel:receive]">
                  <xsl:value-of select="$first-activity/@variable" />
                </xsl:when>
              </xsl:choose>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:variable>
        
        <xsl:choose>
          <xsl:when test="$first-activity-needs-temp-variable = 'true'">
            <xsl:value-of select="true()" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="message-activities-need-temp-variables">
              <xsl:with-param name="messageActivities" select="$messageActivities/*[position() &gt; 1]" />
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
    
  </xsl:template>

</xsl:stylesheet>