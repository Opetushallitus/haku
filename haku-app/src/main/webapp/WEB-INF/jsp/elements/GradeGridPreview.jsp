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
<!--
<table id="gradegrid-table" class="applicant-grades">
<thead>
-->
<tr>
    <th></th>
    <th colspan="2"><fmt:message key="lomake.component.gradegrid.gradesTitle"/></th>
</tr>
<tr>
    <td><fmt:message key="lomake.component.gradegrid.subjectTitle"/></td>
    <td><fmt:message key="lomake.component.gradegrid.commonSubjectColumnTitle"/></td>
    <td><fmt:message key="lomake.component.gradegrid.optionalSubjectColumnTitle"/></td>
    <td><fmt:message key="lomake.component.gradegrid.second.optionalSubjectColumnTitle"/></td>
</tr>
<!--
</thead>
<tbody>
-->
<!-- subjects that are listed before languages -->
<haku:subjectRows subjects="${element.subjectsBeforeLanguages}" element="${element}" data="${categoryData}"/>

<%-- languages --%>
<c:forEach var="language" items="${element.languages}">
    <tr class="gradegrid-language-row">
        <td>
            <haku:languageSelect language="${language}" data="${categoryData}"
                                 options="${element.languageOptions}" preview="${preview}"/>
        </td>
        <td>
            <haku:gradeSelect id="common-${language.id}" data="${categoryData}" preview="${preview}"
                              options="${element.gradeRange}"/>
        </td>
        <td>
            <haku:gradeSelect id="optional-common-${language.id}" data="${categoryData}"
                              preview="${preview}" options="${element.gradeRange}"/>
        </td>
        <td>
            <haku:gradeSelect id="second-optional-common-${language.id}" data="${categoryData}"
                              preview="${preview}" options="${element.gradeRange}"/>
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
        <c:set var="customSecondOptionalGradeKey" value="custom-secondoptionalgrade_${customIndex}" scope="page"/>

        <tr class="gradegrid-language-row gradegrid-custom-language-row">
            <td><c:out value="${element.customLanguageTitle}"/>&nbsp;
                <c:forEach var="scopeOption" items="${element.scopeOptions}">
                    <c:if test="${(categoryData[customScopeKey] eq scopeOption.value)}">
                        <haku:i18nText value="${scopeOption.i18nText}"/>&nbsp;
                    </c:if>
                </c:forEach>
                <c:forEach var="languageOption" items="${element.languageOptions}">
                    <c:if test="${(categoryData[customLanguageKey] eq languageOption.value)}">
                        <haku:i18nText value="${languageOption.i18nText}"/>
                    </c:if>
                </c:forEach>
            </td>
            <td>
                <c:forEach var="grade" items="${element.gradeRange}">
                    <c:if test="${(categoryData[customCommonGradeKey] eq grade.value)}">
                        <haku:i18nText value="${grade.i18nText}"/>
                    </c:if>
                </c:forEach>
            </td>
            <td>
                <c:forEach var="grade" items="${element.gradeRange}">
                    <c:if test="${(categoryData[customOptionalGradeKey] eq grade.value)}">
                        <haku:i18nText value="${grade.i18nText}"/>
                    </c:if>
                </c:forEach>
            </td>
            <td>
                <c:forEach var="grade" items="${element.gradeRange}">
                    <c:if test="${(categoryData[customSecondOptionalGradeKey] eq grade.value)}">
                        <haku:i18nText value="${grade.i18nText}"/>
                    </c:if>
                </c:forEach>
            </td>
        </tr>
    </c:if>

</c:forEach>
<%-- subjects that are listed after languages --%>
<haku:subjectRows subjects="${element.subjectsAfterLanguages}" element="${element}" data="${categoryData}"/>

<%-- subjects that are specific to the education selected by the user --%>
<haku:subjectRows subjects="${additionalQuestionList}" element="${element}" data="${categoryData}"/>
<!--
</tbody>
-->
<!--</table>-->
