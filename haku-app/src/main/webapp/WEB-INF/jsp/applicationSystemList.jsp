<%@ page session="false" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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

<!DOCTYPE HTML>
<html lang="fi">
<head>
    <META http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta charset="utf-8"/>
    <title>Haut</title>
    <script src="${contextPath}/haku-app/resources/jquery/jquery.min.js" type="text/javascript"></script>
    <haku:icons/>
</head>
<body>
<form>
<input type="checkbox" id="published" checked="checked"><label for="published">Vain julkaistu</label><br />
<input type="checkbox" id="open" checked="checked"><label for="open">Vain haku käynnissä</label><br />
<input type="text" id="q" /><br />
</form>
<script type="text/javascript">
function filterApplicationSystems() {
    var published = $('#published').prop('checked');
    var open = $('#open').prop('checked');
    var q = $('#q').val();
    $('li.as').show();
    if (published || open) {
        $('li.as').hide();
        $('li.as').each(function() {
            var isPublished = $('ul > li.asState:contains("JULKAISTU")', this).text();
            var isOpen = $('a.open', this).text();

            if (published && open) {
                if (isPublished && isOpen) {
                    $(this).show();
                }
            } else if ((published && isPublished) || (open && isOpen)) {
                $(this).show();
            }
        });
    }
    if (q) {
        $('li.as').each(function() {
            var re = new RegExp(q, 'i');
            var text = $(this).text();
            if (!text.match(re)) {
                $(this).hide();
            }
        });
    }
}

$(document).ready(function(){
    $('#published').click(function() {
        filterApplicationSystems();
    });
    $('#open').click(function() {
        filterApplicationSystems();
    });
    $('#q').keyup(function() {
        filterApplicationSystems();
    });
    filterApplicationSystems();
    $('#q').focus();
});
</script>
<ul>
    <c:forEach var="applicationSystem" items="${it.applicationSystems}">
        <li class="as">
            <c:choose>
                <c:when test="${applicationSystem.active}">
                    <a class="open" id="${applicationSystem.id}" href="${applicationSystem.id}"><haku:i18nText
                            value="${applicationSystem.name}"/></a> &nbsp;Haku käynnissä!
                </c:when>
                <c:otherwise>
                    <haku:i18nText value="${applicationSystem.name}"/>
                </c:otherwise>
            </c:choose>
            <c:if test="${not empty it.generatorUrl}">
                <a href="${it.generatorUrl}${applicationSystem.id}">Generoi lomake</a>
            </c:if>

            <ul>
                <li> oid: ${applicationSystem.id}</li>
                <li class="asState"> state: ${applicationSystem.state}</li>
                <li> lastGenerated: <fmt:formatDate value="${applicationSystem.lastGenerated}" pattern="yyyy-MM-dd HH:mm:ss" /></li>
            </ul>
        </li>
    </c:forEach>
</ul>
</body>
</html>
