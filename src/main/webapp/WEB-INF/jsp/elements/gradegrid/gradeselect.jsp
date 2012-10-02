<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="field-container-select">
    <select name="arvosana-1" placeholder="Valitse">
        <option></option>
	    <option>Ei arvosanaa</option>
        <c:forEach var="grade" items="${element.gradeRange}">
	        <option>${grade}</option>
	    </c:forEach>
	</select>
</div>