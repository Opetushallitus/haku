<xsl:stylesheet version="2.0"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:types="http://publication.tarjonta.sade.vm.fi/types"
                exclude-result-prefixes="types xsi xs fn"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>


    <xsl:template name="searchResult">
        <ul class="result set-left" style="display: inline-block; margin-right: 20px">
            <li>
                <a class="form-row-link bold">
                    <xsl:attribute name="href">tarjontatiedot/<xsl:value-of select="types:Identifier"/>
                    </xsl:attribute>
                    <xsl:value-of select="types:Title"/>
                </a>
            </li>


            <!--<c:forEach var="key" items="${item['LOIIndexes']}">
                            <c:set var="loi" value="${item[key]}"/>

                            <li><a href="tarjontatiedot/mihinkähäntämänpitäisiosoittaa"
                                   class="form-row-link left-intend-2">- ${loi['LOSName']}, ${loi['LOSQualification']}</a>     </li>
                        </c:forEach>
            -->

        </ul>

    </xsl:template>

</xsl:stylesheet>