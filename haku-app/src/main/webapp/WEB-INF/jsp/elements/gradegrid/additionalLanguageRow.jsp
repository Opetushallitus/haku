<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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

<%-- hidden custom languge row that can be cloned and
included into the table to add new languages
this always has to be the last row--%>
<tr>
    <td>

        <c:out value="${element.customLanguageTitle}"/>&nbsp;
        <select required="required">
            <option></option>
            <c:forEach var="scopeOption" items="${element.scopeOptions}">
                <option value="${scopeOption.value}"><haku:i18nText value="${scopeOption.i18nText}"/></option>
            </c:forEach>
        </select>
        <select required="required">
            <option></option>
            <c:forEach var="languageOption" items="${element.languageOptions}">
                <option value="${languageOption.value}"><haku:i18nText value="${languageOption.i18nText}"/></option>
            </c:forEach>
        </select>

        <a href="#" class="btn-remove"></a>
    </td>
    <td>
        <div class="field-container-select">
            <select name="arvosana-1" required="required">
                <option></option>
                <c:forEach var="grade" items="${element.gradeRange}">
                    <option value="${grade.value}"><haku:i18nText value="${grade.i18nText}"/></option>
                </c:forEach>
            </select>
        </div>
    </td>
    <td>
        <div class="field-container-select">
            <select name="arvosana-1" required="required">
                <c:forEach var="grade" items="${element.gradeRange}">
                    <option value="${grade.value}"><haku:i18nText value="${grade.i18nText}"/></option>
                </c:forEach>
            </select>
        </div>
    </td>
</tr>
