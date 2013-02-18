<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%--
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
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ European Union Public Licence for more details.
  --%>

<!DOCTYPE html>
<fmt:setBundle basename="messages"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<c:set var="additionalQuestions" value="${it.additionalQuestions}" scope="request"/>
<c:set var="additionalInfo" value="${it.application.additionalInfo}" scope="request"/>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
        <meta charset="utf-8"/>
        <link rel="stylesheet"
              href="${contextPath}/resources/jquery-ui-theme/jquery-ui-1.8.23.custom.css"
              type="text/css">
        <link href="${contextPath}/resources/css/virkailija.css" type="text/css" rel="stylesheet"/>
        <title>Opetushallitus</title>
        <script src="${contextPath}/resources/jquery/jquery.min.js"></script>
        <script src="${contextPath}/resources/jquery/jquery-ui-1.8.23.custom.min.js"></script>

        <!--[if gte IE 9]>
        <style type="text/css">
            .tabs .tab span {
                filter: none;
            }
        </style>
        <![endif]-->
    </head>
    <body>
        <div id="viewport">
            <div id="wrapper">
                <header id="siteheader">
                    <div class="primarylinks">
                        <a href="#">Oppijan verkkopalvelu</a> &nbsp;
                        <a href="#">Virkailijan työpöytä</a>
                    </div>
                    <div class="secondarylinks">
                        <a href="#">Omat tiedot</a> &nbsp;
                        <a href="#">Viestintä</a> &nbsp;
                        <a href="#">Asiakaspalvelu</a> &nbsp;
                        <a href="#">Tukipalvelut</a>
                    </div>
                </header>
                <nav id="navigation" class="grid16-16">
                    <ul class="level1">
                        <li><a href="#" class=""><span>Organisaation tiedot</span></a></li>
                        <li>
                            <a href="index.html" class="current"><span>Koulutustarjonta</span></a>
                            <ul class="level2">
                                <li><a href="#" class="">Koulutuksen tiedot</a></li>
                                <li><a href="#" class="">Koulutuksen toteutus ja hakukohde</a></li>
                                <li><a href="#" class="">Organisaation kuvailevat tiedot</a></li>
                                <li><a href="#" class="">Järjestämissopimukset</a></li>
                                <li><a href="#" class="">Järjestämisluvat</a></li>
                            </ul>
                        </li>
                        <li><a href="#" class=""><span>Valintaperusteet</span></a></li>
                        <li><a href="#" class=""><span>Koulutussuunnittelu</span></a></li>
                        <li><a href="#" class=""><span>Sisällönhallinta</span></a></li>
                    </ul>
                    <div class="clear"></div>
                </nav>
                <div id="breadcrumbs">
                    <ul>
                        <li><span><a href="#">Koulutustarjonta</a></span></li>
                        <li><span><a href="#">Hakemuksen esikatselu</a></span></li>
                    </ul>
                </div>
                <section class="grid16-16 margin-top-2">
                    <form class="form" method="post">
                        <fieldset>
                        <button class="save" type="submit"><span><span>
                        <fmt:message key="lomake.button.save"/></span></span></button>
                        <legend class="h3">Syötettävät tiedot</legend>
                        <hr/>
                                <c:forEach var="question" items="${additionalQuestions.allQuestions}">

                                            <div class="form-row">
                                                <label id="label-${question.key}" for="${question.key}" class="form-row-label"><c:out value='${question.key}'/></label>
                                                <div class="form-row-content">
                                                    <c:choose>
                                                        <c:when test="${question.type eq 'DESIMAALILUKU'}">
                                                            <input type="text" name="${question.key}" value="<c:out value='${additionalInfo[question.key]}'/>" pattern="^\d+\.?\d*$"/>
                                                        </c:when>
                                                        <c:when test="${question.type eq 'KOKONAISLUKU'}">
                                                            <input type="text" name="${question.key}" value="<c:out value='${additionalInfo[question.key]}'/>" pattern="^\d+$"/>
                                                        </c:when>
                                                        <c:when test="${question.type eq 'MERKKIJONO'}">
                                                            <input type="text" name="${question.key}" value="<c:out value='${additionalInfo[question.key]}'/>"/>
                                                        </c:when>
                                                        <c:when test="${question.type eq 'TOTUUSARVO'}">
                                                            <input type="radio" name="${question.key}" value="true" ${(additionalInfo[question.key] eq 'true') ? "checked=\"checked\" " : " "}>Kyllä</input>
                                                            <input type="radio" name="${question.key}" value="false" ${(additionalInfo[question.key] eq 'false') ? "checked=\"checked\" " : " "}>Ei</input>
                                                        </c:when>
                                                    </c:choose>
                                                </div>
                                                <div class="clear"></div>
                                            </div>
                                </c:forEach>
                    </fieldset>
                    </form>
              </section>
            </div>
        </div>
    </body>
</html>