<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>

<fieldset class="form-item">
    <legend class="form-row-label"><c:out value="${element.title}"/></legend>
    <div class="form-row-content">
    <c:forEach var="option" items="${element.options}">
        <div class="field-container-checkbox">
            <input type="checkbox" name="${option.id}" disabled="disabled"
                value="${option.value}" ${(categoryData[option.id] eq option.value) ? "checked=\"checked\"" : ""} ${option.attributeString}/>
            <label for="${option.id}">${option.title}</label>
        </div>
        <haku:viewChilds element="${option}"/>
    </c:forEach>
    <div id="help-${element.id}"><small>${element.help}</small></div>
    </div>
</fieldset>
