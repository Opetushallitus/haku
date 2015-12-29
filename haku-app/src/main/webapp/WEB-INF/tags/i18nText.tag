<%@ tag description="i18nText" body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="value" required="true" type="fi.vm.sade.haku.oppija.lomake.domain.I18nText" %>
<%@ attribute name="escape" required="false" type="java.lang.String" %>
<%@ tag trimDirectiveWhitespaces="true" %>
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
<c:set var="escape" value="${(empty escape) ? 'true' : escape}" />
<c:if test="${not empty value}"><c:out value="${value.translations[requestScope['fi_vm_sade_oppija_language']]}"
                                       escapeXml="${escape}" default=""/></c:if>
