<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>
<c:set var="styleBaseClass" value="${element.inline ? 'form-row' : 'form-item'}"/>
<tr>
    <td>
        <fieldset class="${styleBaseClass}">
            <c:out value="${element.title}"/>
            <c:forEach var="option" items="${element.options}">
                <div class="field-container-checkbox">
                    <input type="checkbox" name="${option.id}" disabled="true"
                           value="${option.value}" ${(categoryData[option.id] eq option.value) ? "checked=\"checked\"" : ""}/>
                    <label for="${option.id}">${option.title}</label>
                </div>
            </c:forEach>
        </fieldset>
    </td>
</tr>
<haku:viewChilds element="${element}"/>
