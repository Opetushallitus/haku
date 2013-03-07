<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
        <th colspan="4"><fmt:message key="lomake.component.gradegrid.gradesTitle"/></th>
    </tr>
    <tr>
        <td><fmt:message key="lomake.component.gradegrid.subjectTitle"/></td>
        <td><fmt:message key="lomake.component.gradegrid.commonSubjectColumnTitle"/></td>
        <td><fmt:message key="lomake.component.gradegrid.optionalSubjectColumnTitle"/></td>
        <td><fmt:message key="lomake.component.gradegrid.second.optionalSubjectColumnTitle"/></td>
    </tr>
    </thead>
    <tbody>
    <!-- subjects that are listed before languages -->
    <haku:subjectRows subjects="${element.subjectsBeforeLanguages}" element="${element}" data="${categoryData}"/>

    <%-- languages --%>
    <c:forEach var="language" items="${element.languages}">
        <tr data-gradegrid-row="'{}'">
            <td>
                <haku:languageSelect language="${language}" data="${categoryData}"
                                     options="${element.languageOptions}"/>
            </td>
            <td>
                <haku:gradeSelect id="common-${language.id}" options="${element.gradeRange}"
                                  data="${categoryData}" showEmptyOption="true"/>
            </td>
            <td>
                <c:if test="${language.optionalGrades}">
                    <haku:gradeSelect id="optional-common-${language.id}" options="${element.gradeRange}"
                                      data="${categoryData}"/>
                </c:if>
            </td>
            <td>
                <c:if test="${language.optionalGrades}">
                    <haku:gradeSelect id="second-optional-common-${language.id}" options="${element.gradeRange}"
                                      data="${categoryData}"/>
                </c:if>
            </td>
        </tr>
    </c:forEach>

    <%-- custom selected languages --%>
    <haku:customSelectedLanguages data="${categoryData}" gradeGrid="${element}"/>


    <!-- add new language row -->
    <tr id="add-lang">
        <td colspan=4>
            <button id="add_language_button" class="link" type="button"><fmt:message
                    key="lomake.component.gradegrid.addLanguageLabel"/></button>
        </td>
    </tr>

    <%-- subjects that are listed after languages --%>
    <haku:subjectRows subjects="${element.subjectsAfterLanguages}" element="${element}" data="${categoryData}"/>

    <%-- subjects that are specific to the education selected by the user --%>
    <haku:subjectRows subjects="${additionalQuestionList}" element="${element}" data="${categoryData}"/>

    </tbody>
</table>
<script>
    var gradegrid_settings = {
        contextPath: "${pageContext.request.contextPath}",
        applicationSystemId: "${it.hakemusId.applicationPeriodId}",
        formId: "${it.hakemusId.formId}",
        elementId: "${element.id}"
    };
</script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/javascript/gradegrid.js"></script>
