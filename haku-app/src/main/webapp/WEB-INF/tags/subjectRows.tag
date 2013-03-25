<%@ tag description="Creates subject rows" body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ tag trimDirectiveWhitespaces="true" %>

<%@ attribute name="subjects" required="true" type="java.util.List" %>
<%@ attribute name="element" required="true" type="fi.vm.sade.oppija.lomake.domain.elements.custom.GradeGrid" %>
<%@ attribute name="data" required="true" type="java.util.Map" %>
<%@ attribute name="extraOptionalGrades" required="true" type="java.lang.Boolean" %>

<c:forEach var="subject" items="${subjects}">
    <tr>
        <td>
            <haku:i18nText value="${subject.i18nText}"/>
        </td>
        <td>
            <haku:gradeSelect id="common-${subject.id}" options="${element.gradeRange}" data="${data}"
                              showEmptyOption="true"/>
        </td>
        <td>
            <haku:gradeSelect id="optional-common-${subject.id}" options="${element.gradeRange}"
                              data="${data}"/>
        </td>
        <c:if test="${extraOptionalGrades}">
            <td>
                <haku:gradeSelect id="second-optional-common-${subject.id}" options="${element.gradeRange}"
                                  data="${data}"/>
            </td>
        </c:if>
    </tr>
</c:forEach>
