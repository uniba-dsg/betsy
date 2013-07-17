<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- This is a template library with various constants. -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable">
  
  <xsl:variable name="xpathURN">urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0</xsl:variable>
  <xsl:variable name="WS-BPEL-namespace-URI">http://docs.oasis-open.org/wsbpel/2.0/process/executable</xsl:variable>
  
  <!-- Postfixes for fresh names in different contexts. -->
  <xsl:variable name="default-message-exchange-postfix">DefaultMessageExchange</xsl:variable>
  <xsl:variable name="fresh-activity-name-postfix">FreshActivityName</xsl:variable>
  <xsl:variable name="fresh-sequence-link-postfix">FreshSequenceLink</xsl:variable>
  <xsl:variable name="tmp-condition-variable-postfix">TmpConditionVar</xsl:variable>
  <xsl:variable name="tmp-input-message-variable-postfix">TmpInputMessageVar</xsl:variable>  
  <xsl:variable name="tmp-output-message-variable-postfix">TmpOutputMessageVar</xsl:variable>  
  
</xsl:stylesheet>