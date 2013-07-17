<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Move the <scope>-parts of an <invoke> into an explicit enclosing <scope>. -->
<!-- Also, make temporary variables and assignments, due to the use of <toParts>,
     <fromParts>, and/or references to element variables, explicit. -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable">

  <xsl:output indent="yes" method="xml" />

  <xsl:include href="to-from-parts-element-variables.xsl" />

  <!-- Copy all elements and attributes -->
  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>

  <!-- Unfold invoke -->
  <xsl:template match="bpel:invoke">
    <xsl:variable name="inputVariable" select="@inputVariable" />
    <xsl:variable name="outputVariable" select="@outputVariable" />

    <xsl:variable name="inputElement" select="count(ancestor::*/bpel:variables/bpel:variable[@name=$inputVariable][1]/@element) = 1" />
    <xsl:variable name="outputElement" select="count(ancestor::*/bpel:variables/bpel:variable[@name=$outputVariable][1]/@element) = 1" />

    <xsl:choose>
      <xsl:when test="bpel:toParts or $inputElement or bpel:fromParts or $outputElement or bpel:catch or bpel:catchAll or bpel:compensationHandler">
        <bpel:scope>
          <xsl:if test="bpel:toParts or $inputElement or bpel:fromParts or $outputElement">
            <bpel:variables>
              <xsl:call-template name="message-activities-temp-variables">
                <xsl:with-param name="messageActivities" select="." />
              </xsl:call-template>
            </bpel:variables>
          </xsl:if>

          <xsl:if test="bpel:catch or bpel:catchAll">
            <bpel:faultHandlers>
              <xsl:copy-of select="bpel:catch" />
              <xsl:copy-of select="bpel:catchAll" />
            </bpel:faultHandlers>
          </xsl:if>

          <xsl:copy-of select="bpel:compensationHandler" />
          
          <xsl:choose>
            <xsl:when test="bpel:toParts or bpel:fromParts or $inputElement or $outputElement">
              <bpel:sequence>
                <!-- Transform toParts into an assignment, if present -->
                <xsl:if test="bpel:toParts">
                  <xsl:call-template name="copy-to-parts-explicitly" />
                </xsl:if>

                <!-- Create assignment to copy element variable to single part -->
                <xsl:if test="$inputElement">
                  <xsl:call-template name="copy-input-element-explicitly">
                    <xsl:with-param name="inputVariable" select="$inputVariable" />
                  </xsl:call-template>
                </xsl:if>

                <xsl:copy>
                  <xsl:copy-of select="@*[not(namespace-uri() = '' and
                                              (local-name() = 'inputVariable' or
                                               local-name() = 'outputVariable' or
                                               local-name() = 'portType'))]" />

                  <xsl:choose>
                    <xsl:when test="bpel:toParts or $inputElement">
                      <xsl:call-template name="attribute-with-unique-element-name">
                        <xsl:with-param name="attributeName" select="'inputVariable'" />
                        <xsl:with-param name="element" select="." />
                        <xsl:with-param name="postfix" select="$tmp-input-message-variable-postfix" />
                      </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:copy-of select="@inputVariable" />
                    </xsl:otherwise>
                  </xsl:choose>

                  <xsl:choose>
                    <xsl:when test="bpel:fromParts or $outputElement">
                      <xsl:call-template name="attribute-with-unique-element-name">
                        <xsl:with-param name="attributeName" select="'outputVariable'" />
                        <xsl:with-param name="element" select="." />
                        <xsl:with-param name="postfix" select="$tmp-output-message-variable-postfix" />
                      </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:copy-of select="@outputVariable" />
                    </xsl:otherwise>
                  </xsl:choose>

                  <xsl:apply-templates select="*[not(self::bpel:catch or
                                                     self::bpel:catchAll or
                                                     self::bpel:compensationHandler or
                                                     self::bpel:toParts or
                                                     self::bpel:fromParts)]"/>
                </xsl:copy>

                <!-- Transform fromParts into an assignment, if present -->
                <xsl:if test="bpel:fromParts">
                  <xsl:call-template name="copy-from-parts-explicitly" />
                </xsl:if>

                <!-- Create assignment to copy single part to element variable -->
                <xsl:if test="$outputElement">
                  <xsl:call-template name="copy-output-element-explicitly">
                    <xsl:with-param name="outputVariable" select="$outputVariable" />
                  </xsl:call-template>
                </xsl:if>
              </bpel:sequence>
            </xsl:when>
            
            <xsl:otherwise>
              <xsl:copy>
                <xsl:copy-of select="@*[not(namespace-uri() = '' and
                                            local-name() = 'portType')]" />
                <xsl:apply-templates select="*[not(self::bpel:catch or
                                                   self::bpel:catchAll or
                                                   self::bpel:compensationHandler)]"/>
              </xsl:copy>
            </xsl:otherwise>
          </xsl:choose>
    
        </bpel:scope>
      </xsl:when>
      
      <xsl:otherwise>
        <xsl:copy>
          <xsl:copy-of select="@*[not(namespace-uri() = '' and
                                      local-name() = 'portType')]" />
          <xsl:apply-templates />
        </xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>