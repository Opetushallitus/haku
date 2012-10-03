<%@ taglib uri="http://java.sun.com/jsp/jstl/core"
           prefix="c" %>
<fieldset class="form-row">
    <legend class="form-row-label"><c:out value="${element.title}"/></legend>
    <div class="form-row-content">

    <c:forEach var="option" items="${element.options}" varStatus="status">
        <div class="field-container-radio">
            <c:set var="id" value="${element.id}.${option.id}"/>
            <input  id="${id}" type="radio" name="${element.id}"
                value="${option.value}" ${(categoryData[element.id] eq option.value) ? "checked=\"checked\"" : ""} ${option.attributeString}/>
            <label for="${id}">${option.title}</label>
        </div>
    </c:forEach>

    </div>
    <div class="clear"></div>
</fieldset>
