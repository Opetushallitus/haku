<!--
  ~ Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
  ~
  ~ This program is free software:  Licensed under the EUPL, Version 1.1 or - as
  ~ soon as they will be approved by the European Commission - subsequent versions
  ~ of the EUPL (the "Licence");
  ~
  ~ You may not use this work except in compliance with the Licence.
  ~ You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  ~ European Union Public Licence for more details.
  -->
<xsl:stylesheet version="2.0"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:types="http://publication.tarjonta.sade.vm.fi/types"
                exclude-result-prefixes="types xsi xs fn f"
                xmlns:f="Functions"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


    <xsl:function name="f:getProperty" as="xs:string?">
        <xsl:param name="key" as="xs:string"/>
        <xsl:variable name="lines" as="xs:string*" select="
           for $x in
             for $i in tokenize($properties, '\n')[matches(., '^[^!#]')] return
               tokenize($i, '=')
             return translate(normalize-space($x), '\', '')"/>
        <xsl:sequence select="$lines[index-of($lines, $key)+1]"/>
    </xsl:function>


</xsl:stylesheet>