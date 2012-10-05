<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!-- gradeSelectId has to be set -->
<div class="field-container-select">
    <select id="${gradeSelectId}" name="${gradeSelectId}" placeholder="Valitse">
        <option></option>
        <option>Ei arvosanaa</option>
        <c:forEach var="grade" items="${element.gradeRange}">
            <option ${(categoryData[gradeSelectId] eq grade) ? "selected=\"selected\"" : ""}>${grade}</option>
        </c:forEach>
    </select>
</div>