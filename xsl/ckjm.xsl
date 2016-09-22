<?xml version="1.0" encoding="ISO-8859-1"?>
<!--
 (C) Copyright 2005 Julien Rentrop, Diomidis Spinellis

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

-->

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
<html>
<head>
  <title>CKJM Chidamber and Kemerer Java Metrics</title>
  <meta name="Generator" content="$Id: \\dds\\src\\Research\\ckjm.RCS\\xsl\\ckjm.xsl,v 1.3 2005/10/15 09:03:57 dds Exp $" />
  <style type="text/css">
      body {
        font:normal 68% verdana,arial,helvetica;
        color:#000000;
      }
      table {
        width: 100%;
      }

      table tr td, tr th {
        font:normal 68% verdana,arial,helvetica;
      }
      table.details tr th{
        font-weight: bold;
        text-align:left;
        background:#a6caf0;
      }
      table.details tr td{
        background:#eeeee0;
      }

      p {
        line-height:1.5em;
        margin-top:0.5em; margin-bottom:1.0em;
        margin-left:2em;
        margin-right:2em;
      }
      h1 {
        margin: 0px 0px 5px; font: 165% verdana,arial,helvetica
      }
      h2 {
        margin-top: 1em; margin-bottom: 0.5em; font: bold 125% verdana,arial,helvetica
      }
      </style>
</head>
<body>
<h1>CKJM Chidamber and Kemerer Java Metrics</h1>
<p align="right">Designed for use with <a href="http://www.dmst.aueb.gr/dds/sw/ckjm/">CKJM</a> and <a href="http://ant.apache.org">Ant</a>.</p>

<hr size="2"/>

<h2>Summary</h2>
<table class="details">
<tr>
<xsl:for-each select="/ckjm/class[1]/*">
  <th><xsl:value-of select="name()"/></th>
</xsl:for-each>
</tr>
<xsl:for-each select="/ckjm/class">
<xsl:sort select="name" data-type="text" order="ascending"/>
<tr>
  <xsl:for-each select="*">
  <td><xsl:value-of select="text()"/></td>
  </xsl:for-each>
</tr>
</xsl:for-each>
</table>

</body>
</html>
</xsl:template>

</xsl:stylesheet>
