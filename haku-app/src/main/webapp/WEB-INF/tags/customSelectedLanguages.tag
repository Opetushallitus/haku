<%@ tag description="Creates subject rows" body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ tag trimDirectiveWhitespaces="true" %>

<%@ attribute name="data" required="true" type="java.util.Map" %>
<%@ attribute name="gradeGrid" required="true" type="fi.vm.sade.oppija.lomake.domain.elements.custom.GradeGrid" %>

<c:forEach var="entry" items="${data}">

    <c:if test="${fn:startsWith(entry.key,'custom-scope')}">
        <c:set var="customIndex" value="${fn:substringAfter(entry.key, 'custom-scope_')}" scope="page"/>

        <tr class="dynamic">
            <td><fmt:message key="lomake.component.gradegrid.custom.language.Label"/>
                <haku:errorMessage id="custom-language_${customIndex}"/>
                <haku:customLang id="custom-scope_${customIndex}" items="${gradeGrid.scopeOptions}"
                                 data="${categoryData}"/>
                <haku:customLang id="custom-language_${customIndex}" items="${gradeGrid.languageOptions}"
                                 data="${categoryData}"/>
                <a href="#" class="btn-remove"></a>
            </td>
            <td>
                <haku:gradeSelect id="custom-commongrade_${customIndex}"
                                  data="${categoryData}" options="${gradeGrid.gradeRange}" showEmptyOption="true"/>
            </td>
            <td>
                <haku:gradeSelect id="custom-optionalgrade_${customIndex}"
                                  data="${categoryData}" options="${gradeGrid.gradeRange}"/>
            </td>
            <td>
                <haku:gradeSelect id="custom-secondoptionalgrade_${customIndex}"
                                  data="${categoryData}" options="${gradeGrid.gradeRange}"/>
            </td>
        </tr>
    </c:if>

</c:forEach>
