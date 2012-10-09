<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!-- gradeSelectId has to be set -->
<div class="field-container-select">
    <select id="${gradeSelectId}" name="${gradeSelectId}" placeholder="Valitse">
        <option></option>
        <c:forEach var="grade" items="${element.gradeRange}">
            <option value="${grade.value}" ${(categoryData[gradeSelectId] eq grade.value) ? "selected=\"selected\"" : ""}>${grade.title}</option>
        </c:forEach>
    </select>
</div>