<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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

<c:if test="${infobox != null}">
    <div class="infobox">
        <h3><c:out value="${infobox.header}"/></h3>
        <ul class="minimal">
            <c:forEach var="item" items="${infobox.items}">
                <li class="heading"><c:out value="${item.header}"/></li>
                <li class="emphasized"><c:out value="${item.content}"/></li>
            </c:forEach>
            <c:if test="${infobox.footer != null}">
                <li class="set-right"><c:out value="${infobox.footer}"/></li>
            </c:if>
        </ul>
    </div>
</c:if>
