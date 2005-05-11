<?xml version="1.0" encoding="ISO-8859-1"?>
<!--
 (C) Copyright 2005 Julien Rentrop, Diomidis Spinellis

 Permission to use, copy, and distribute this software and its
 documentation for any purpose and without fee is hereby granted,
 provided that the above copyright notice appear in all copies and that
 both that copyright notice and this permission notice appear in
 supporting documentation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
 WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.

 $Id: \\dds\\src\\Research\\ckjm.RCS\\xsl\\ckjm_extra.xsl,v 1.1 2005/05/11 21:14:18 dds Exp $

-->


<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- Number of items to display in the top lists -->
<xsl:variable name="top" select="25"/>

<xsl:template match="/">
<html>
<head>
  <title>CKJM Chidamber and Kemerer Java Metrics</title>
  <style type="text/css">
      body {
        font:normal 68% verdana,arial,helvetica;
        color:#000000;
      }
      table {
        width: 100%;
      }

      table tr td, tr th {
          font-size: 68%;
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
      h3 {
        margin-bottom: 0.5em; font: bold 115% verdana,arial,helvetica
      }
      h4 {
        margin-bottom: 0.5em; font: bold 100% verdana,arial,helvetica
      }
      h5 {
        margin-bottom: 0.5em; font: bold 100% verdana,arial,helvetica
      }
      h6 {
        margin-bottom: 0.5em; font: bold 100% verdana,arial,helvetica
      }
      .Error {
        font-weight:bold; color:red;
      }
      .Failure {
        font-weight:bold; color:purple;
      }
      .Properties {
        text-align:right;
      }
      </style>
</head>
<body>
<h1>CKJM Chidamber and Kemerer Java Metrics</h1>
<p align="right">Designed for use with <a href="http://www.dmst.aueb.gr/dds/sw/ckjm/">CKJM</a> and <a href="http://ant.apache.org">Ant</a>.</p>

<hr size="2"/>

<table>
<tr>
  <td><a name="NVsummary"><h2>Summary</h2></a></td>
  <td align="right">
    [<a href="#NVsummary">summary</a>]
    [<a href="#NVwmc">wmc</a>]
    [<a href="#NVcbo">cbo</a>]
    [<a href="#NVlcom">lcom</a>]
    [<a href="#NVrfc">rfc</a>]
    [<a href="#NVdit">dit</a>]
    [<a href="#NVnoc">noc</a>]
    [<a href="#NVexplanations">explanations</a>]
  </td>
</tr>
</table>

<table class="details">
<tr>
  <th>Class</th>
  <th>WMC</th>
  <th>CBO</th>
  <th>LCOM</th>
  <th>RFC</th>
  <th>DIT</th>
  <th>NOC</th>
</tr>
<xsl:for-each select="ckjm/class">
<xsl:sort select="name" data-type="text" order="ascending"/>
<tr>
  <td><xsl:value-of select="name"/></td>
  <td><xsl:value-of select="wmc"/></td>
  <td><xsl:value-of select="cbo"/></td>
  <td><xsl:value-of select="lcom"/></td>
  <td><xsl:value-of select="rfc"/></td>
  <td><xsl:value-of select="dit"/></td>
  <td><xsl:value-of select="noc"/></td>
</tr>
</xsl:for-each>
</table>

<table>
<tr>
  <td>
    <a name="NVwmc">
     <h2>Top <xsl:value-of select="$top"/> Weighted Methods per Class (WMC)</h2>
     </a>
  </td>
  <td align="right">
    [<a href="#NVsummary">summary</a>]
    [<a href="#NVwmc">wmc</a>]
    [<a href="#NVcbo">cbo</a>]
    [<a href="#NVlcom">lcom</a>]
    [<a href="#NVrfc">rfc</a>]
    [<a href="#NVdit">dit</a>]
    [<a href="#NVnoc">noc</a>]
    [<a href="#NVexplanations">explanations</a>]
  </td>
</tr>
</table>

<table class="details">
<tr>
  <th>Class</th>
  <th>WMC</th>
  <th>CBO</th>
  <th>LCOM</th>
  <th>RFC</th>
  <th>DIT</th>
  <th>NOC</th>
</tr>
<xsl:apply-templates select="ckjm/class">
<xsl:sort select="wmc" data-type="number" order="descending"/>
</xsl:apply-templates>
</table>

<table>
<tr>
  <td>
    <a name="NVcbo">
      <h2>Top <xsl:value-of select="$top"/> Coupling Between Objects (CBO)</h2>
    </a>
  </td>
  <td align="right">
    [<a href="#NVsummary">summary</a>]
    [<a href="#NVwmc">wmc</a>]
    [<a href="#NVcbo">cbo</a>]
    [<a href="#NVlcom">lcom</a>]
    [<a href="#NVrfc">rfc</a>]
    [<a href="#NVdit">dit</a>]
    [<a href="#NVnoc">noc</a>]
    [<a href="#NVexplanations">explanations</a>]
  </td>
</tr>
</table>

<table class="details">
<tr>
  <th>Class</th>
  <th>WMC</th>
  <th>CBO</th>
  <th>LCOM</th>
  <th>RFC</th>
  <th>DIT</th>
  <th>NOC</th>
</tr>
<xsl:apply-templates select="ckjm/class">
<xsl:sort select="cbo" data-type="number" order="descending"/>
</xsl:apply-templates>
</table>

<table>
<tr>
  <td>
    <a name="NVlcom">
      <h2>Top <xsl:value-of select="$top"/> Lack of Cohesions in Methods (LCOM)</h2>
    </a>
  </td>
  <td align="right">
    [<a href="#NVsummary">summary</a>]
    [<a href="#NVwmc">wmc</a>]
    [<a href="#NVcbo">cbo</a>]
    [<a href="#NVlcom">lcom</a>]
    [<a href="#NVrfc">rfc</a>]
    [<a href="#NVdit">dit</a>]
    [<a href="#NVnoc">noc</a>]
    [<a href="#NVexplanations">explanations</a>]
  </td>
</tr>
</table>

<table class="details">
<tr>
  <th>Class</th>
  <th>WMC</th>
  <th>CBO</th>
  <th>LCOM</th>
  <th>RFC</th>
  <th>DIT</th>
  <th>NOC</th>
</tr>
<xsl:apply-templates select="ckjm/class">
<xsl:sort select="lcom" data-type="number" order="descending"/>
</xsl:apply-templates>
</table>

<table>
<tr>
  <td>
    <a name="NVrfc">
      <h2>Top <xsl:value-of select="$top"/> Response For Class (RFC)</h2>
    </a>
  </td>
  <td align="right">
    [<a href="#NVsummary">summary</a>]
    [<a href="#NVwmc">wmc</a>]
    [<a href="#NVcbo">cbo</a>]
    [<a href="#NVlcom">lcom</a>]
    [<a href="#NVrfc">rfc</a>]
    [<a href="#NVdit">dit</a>]
    [<a href="#NVnoc">noc</a>]
    [<a href="#NVexplanations">explanations</a>]
  </td>
</tr>
</table>

<table class="details">
<tr>
  <th>Class</th>
  <th>WMC</th>
  <th>CBO</th>
  <th>LCOM</th>
  <th>RFC</th>
  <th>DIT</th>
  <th>NOC</th>
</tr>
<xsl:apply-templates select="ckjm/class">
<xsl:sort select="rfc" data-type="number" order="descending"/>
</xsl:apply-templates>
</table>

<table>
<tr>
  <td>
    <a name="NVdit">
      <h2>Top <xsl:value-of select="$top"/> Depth of Inheritance Tree (DIT)</h2>
    </a>
  </td>
  <td align="right">
    [<a href="#NVsummary">summary</a>]
    [<a href="#NVwmc">wmc</a>]
    [<a href="#NVcbo">cbo</a>]
    [<a href="#NVlcom">lcom</a>]
    [<a href="#NVrfc">rfc</a>]
    [<a href="#NVdit">dit</a>]
    [<a href="#NVnoc">noc</a>]
    [<a href="#NVexplanations">explanations</a>]
  </td>
</tr>
</table>

<table class="details">
<tr>
  <th>Class</th>
  <th>WMC</th>
  <th>CBO</th>
  <th>LCOM</th>
  <th>RFC</th>
  <th>DIT</th>
  <th>NOC</th>
</tr>
<xsl:apply-templates select="ckjm/class">
<xsl:sort select="dit" data-type="number" order="descending"/>
</xsl:apply-templates>
</table>

<table>
<tr>
  <td>
    <a name="NVnoc">
      <h2>Top <xsl:value-of select="$top"/> Number of Children (NOC)</h2>
    </a>
  </td>
  <td align="right">
    [<a href="#NVsummary">summary</a>]
    [<a href="#NVwmc">wmc</a>]
    [<a href="#NVcbo">cbo</a>]
    [<a href="#NVlcom">lcom</a>]
    [<a href="#NVrfc">rfc</a>]
    [<a href="#NVdit">dit</a>]
    [<a href="#NVnoc">noc</a>]
    [<a href="#NVexplanations">explanations</a>]
  </td>
</tr>
</table>

<table class="details">
<tr>
  <th>Class</th>
  <th>WMC</th>
  <th>CBO</th>
  <th>LCOM</th>
  <th>RFC</th>
  <th>DIT</th>
  <th>NOC</th>
</tr>
<xsl:apply-templates select="ckjm/class">
<xsl:sort select="noc" data-type="number" order="descending"/>
</xsl:apply-templates>
</table>

<table>
<tr>
  <td>
    <a name="NVexplanations">
      <h2>Explanations</h2>
    </a>
  </td>
  <td align="right">
    [<a href="#NVsummary">summary</a>]
    [<a href="#NVwmc">wmc</a>]
    [<a href="#NVcbo">cbo</a>]
    [<a href="#NVlcom">lcom</a>]
    [<a href="#NVrfc">rfc</a>]
    [<a href="#NVdit">dit</a>]
    [<a href="#NVnoc">noc</a>]
    [<a href="#NVexplanations">explanations</a>]
  </td>
</tr>
</table>
<dl>
<dt>WMC - Weighted methods per class</dt><dd>
A class's <em>weighted methods per class</em> WMC
metric is simply the sum of the complexities of its methods.
As a measure of complexity we can use the cyclomatic complexity,
or we can abritrarily assign a complexity value of 1 to each method.
The <em>ckjm</em> program assigns a complexity value of 1 to each method,
and therefore the value of the WMC is equal to the number of methods
in the class.
</dd><dt>DIT - Depth of Inheritance Tree</dt><dd>
The <em>depth of inheritance tree</em> (DIT) metric provides
for each class a measure of the inheritance levels from the object
hierarchy top.
In Java where all classes inherit Object the minimum value
of DIT is 1.
</dd><dt>NOC - Number of Children</dt><dd>

A class's <em>number of children</em> (NOC)
metric simply
measures the number of immediate descendants of the class.
</dd><dt>CBO - Coupling between object classes</dt><dd>
The <em>coupling between object classes</em> (CBO)
metric represents the number of classes coupled to a given
class.
This coupling can occur through method calls,
field accesses, inheritance, arguments, return types,
and exceptions.
</dd><dt>RFC - Response for a Class</dt><dd>
The metric called the <em>response for a class</em> (RFC)
measures the number of different
methods that can be executed when an object of that
class receives a message (when a method is invoked
for that object).
Ideally, we would want to find for each method
of the class, the methods that class will call,
and repeat this for each called method,
calculating what is called the <em>transitive closure</em>

of the method's call graph.
This process can however be both expensive and quite
inaccurate.
In <em>ckjm</em>, we calculate a rough
approximation to the response set
by simply inspecting method calls within the class's
method bodies.
This simplification was also used in the 1994 Chidamber and Kemerer
description of the metrics.
</dd><dt>LCOM - Lack of cohesion in methods</dt><dd>
A class's <em>lack of cohesion in methods</em> (LCOM)
metric counts the sets of methods in a class that are not related
through the sharing of some of the class's fields.
The original definition of this metric
(which is the one used in <em>ckjm</em>)
considers all pairs of a class's methods.
In some of these pairs both methods access at
least one common field of the class, while in
other pairs the two methods to not share any
common field accesses.
The lack of cohesion in methods is then calculated
by subtracting from the number of method pairs
that don't share a field access the number of
method pairs that do.
Note that subsequent definitions
of this metric used as a measurement basis the number
of disjoint graph components of the class's methods.
Others modified the definition of connectedness to
include calls between the methods of the class.
The program <em>ckjm</em> follows the original (1994) definition
by Chidamber and Kemerer.
</dd>
</dl>

</body>
</html>
</xsl:template>

<xsl:template match="class">
<xsl:if test="position() &lt;= $top">
<tr>
  <td><xsl:value-of select="name"/></td>
  <td><xsl:value-of select="wmc"/></td>
  <td><xsl:value-of select="cbo"/></td>
  <td><xsl:value-of select="lcom"/></td>
  <td><xsl:value-of select="rfc"/></td>
  <td><xsl:value-of select="dit"/></td>
  <td><xsl:value-of select="noc"/></td>
</tr>
</xsl:if>
</xsl:template>

</xsl:stylesheet>
