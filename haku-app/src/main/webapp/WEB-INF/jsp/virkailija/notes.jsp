<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
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
<script>var contextPath = "${contextPath}"</script>
<script src="${contextPath}/resources/javascript/virkailija/notes.js" type="text/javascript"></script>

<fieldset id="notes">
    <legend class="h3"><fmt:message key="virkailija.hakemus.kommentit"/></legend>

    <sec:authorize access="hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD')">
        <form action="${contextPath}/virkailija/hakemus/${application.oid}/note" method="post">
            <textarea name="note-text" id="note-text"></textarea>
            <input id="note-create" class="button primary small" type="submit"
                   value="<fmt:message key="virkailija.hakemus.kommentit.uusi"/>"/>
        </form>
    </sec:authorize>
    <c:forEach var="note" items="${application.notes}">
        <div class="note-content">
            <span class="note-date"><fmt:formatDate value="${note.added}" pattern="dd.MM.yyyy HH:mm:ss"/></span>
            <span class="note-text"><c:out value="${note.noteText}"/></span>
            <!-- /hakemus/note/user/{user}/name -->
            <span class="note-user" id="note-user-${note.user}" user="${note.user}"></span>
        </div>
    </c:forEach>
</fieldset>
