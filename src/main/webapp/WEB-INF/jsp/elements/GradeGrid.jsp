<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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
<fmt:setBundle basename="messages"/>
<table id="gradegrid-table" class="applicant-grades">
    <thead>
    <tr>
        <th></th>
        <th colspan="2"><fmt:message key="lomake.component.gradegrid.gradesTitle"/></th>
    </tr>
    <tr>
        <td><fmt:message key="lomake.component.gradegrid.subjectTitle"/></td>
        <td><fmt:message key="lomake.component.gradegrid.commonSubjectColumnTitle"/></td>
        <td><fmt:message key="lomake.component.gradegrid.optionalSubjectColumnTitle"/></td>
    </tr>
    </thead>
    <tbody>
    <!-- subjects that are listed before languages -->
    <c:forEach var="subject" items="${element.subjectsBeforeLanguages}">
        <c:set var="subject" value="${subject}" scope="request"/>
        <tr>
            <td>
                <jsp:include page="gradegrid/SubjectRow.jsp"/>
            </td>
            <td>
                <c:set var="gradeSelectId" value="common-${subject.id}" scope="request"/>
                <jsp:include page="gradegrid/gradeselect.jsp"/>
            </td>
            <td>
                <c:set var="gradeSelectId" value="optional-${subject.id}" scope="request"/>
                <jsp:include page="gradegrid/gradeselect.jsp"/>
            </td>
        </tr>
    </c:forEach>

    <%-- languages --%>
    <c:forEach var="language" items="${element.languages}">
        <c:set var="language" value="${language}" scope="request"/>
        <tr class="gradegrid-language-row">
            <td>
                <jsp:include page="gradegrid/LanguageRow.jsp"/>
            </td>
            <td>
                <c:set var="gradeSelectId" value="common-${language.id}" scope="request"/>
                <jsp:include page="gradegrid/gradeselect.jsp"/>
            </td>
            <td>
                <c:set var="gradeSelectId" value="optional-${language.id}" scope="request"/>
                <jsp:include page="gradegrid/gradeselect.jsp"/>
            </td>
        </tr>
    </c:forEach>

    <%-- custom selected languages --%>
    <c:forEach var="entry" items="${categoryData}">

        <c:if test="${fn:startsWith(entry.key,'custom-scope')}">
            <c:set var="customIndex" value="${fn:substringAfter(entry.key, 'custom-scope_')}" scope="page"/>

            <c:set var="customScopeKey" value="custom-scope_${customIndex}" scope="page"/>
            <c:set var="customLanguageKey" value="custom-language_${customIndex}" scope="page"/>
            <c:set var="customCommonGradeKey" value="custom-commongrade_${customIndex}" scope="page"/>
            <c:set var="customOptionalGradeKey" value="custom-optionalgrade_${customIndex}" scope="page"/>

            <tr class="gradegrid-language-row gradegrid-custom-language-row">
                <td><c:out value="${element.customLanguageTitle}"/>
                    <select id="${customScopeKey}" name="${customScopeKey}">
                        <option></option>
                        <c:forEach var="scopeOption" items="${element.scopeOptions}">
                            <option value="${scopeOption.value}" ${(categoryData[customScopeKey] eq scopeOption.value) ? "selected=\"selected\"" : ""}>
                                <haku:i18nText value="${scopeOption.i18nText}"/></option>
                        </c:forEach>
                    </select>
                    <select id="${customLanguageKey}" name="${customLanguageKey}">
                        <option></option>
                        <c:forEach var="languageOption" items="${element.languageOptions}">
                            <option value="${languageOption.value}"
                                ${(categoryData[customLanguageKey] eq languageOption.value) ? "selected=\"selected\"" : ""}>
                                <haku:i18nText value="${languageOption.i18nText}"/></option>
                        </c:forEach>
                    </select>
                </td>
                <td>
                    <div class="field-container-select">
                        <select id="${customCommonGradeKey}" name="${customCommonGradeKey}" placeholder="Valitse">
                            <option></option>
                            <c:forEach var="grade" items="${element.gradeRange}">
                                <option value="${grade.value}" ${(categoryData[customCommonGradeKey] eq grade.value) ? "selected=\"selected\"" : ""}>
                                    <haku:i18nText value="${grade.i18nText}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                </td>
                <td>
                    <div class="field-container-select">
                        <select id="${customOptionalGradeKey}" name="${customOptionalGradeKey}" placeholder="Valitse">
                            <option></option>
                            <c:forEach var="grade" items="${element.gradeRange}">
                                <option value="${grade.value}" ${(categoryData[customOptionalGradeKey] eq grade.value) ? "selected=\"selected\"" : ""}>
                                    <haku:i18nText value="${grade.i18nText}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                </td>
            </tr>
        </c:if>

    </c:forEach>

    <!-- add new language row -->
    <tr>
        <td colspan=3>
            <button id="add_language_button" class="link" type="button"><fmt:message
                    key="lomake.component.gradegrid.addLanguageLabel"/></button>
        </td>
    </tr>

    <%-- subjects that are listed after languages --%>
    <c:forEach var="subject" items="${element.subjectsAfterLanguages}">
        <c:set var="subject" value="${subject}" scope="request"/>
        <tr>
            <td>
                <jsp:include page="gradegrid/SubjectRow.jsp"/>
            </td>
            <td>
                <c:set var="gradeSelectId" value="common-${subject.id}" scope="request"/>
                <jsp:include page="gradegrid/gradeselect.jsp"/>
            </td>
            <td>
                <c:set var="gradeSelectId" value="optional-${subject.id}" scope="request"/>
                <jsp:include page="gradegrid/gradeselect.jsp"/>
            </td>
        </tr>
    </c:forEach>

    <%-- subjects that are specific to the education selected by the user --%>
    <c:forEach var="subject" items="${additionalQuestionList}">
        <c:set var="subject" value="${subject}" scope="request"/>
        <tr>
            <td>
                <jsp:include page="gradegrid/SubjectRow.jsp"/>
            </td>
            <td>
                <c:set var="gradeSelectId" value="common-${subject.id}" scope="request"/>
                <jsp:include page="gradegrid/gradeselect.jsp"/>
            </td>
            <td>
                <c:set var="gradeSelectId" value="optional-${subject.id}" scope="request"/>
                <jsp:include page="gradegrid/gradeselect.jsp"/>
            </td>
        </tr>
    </c:forEach>

    </tbody>
</table>
<script>
    var gradegrid_settings = {
        contextPath: "${pageContext.request.contextPath}",
        applicationSystemId: "${hakemusId.applicationPeriodId}",
        formId: "${hakemusId.formId}",
        elementId: "${element.id}"
    };
</script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/javascript/gradegrid.js"></script>
