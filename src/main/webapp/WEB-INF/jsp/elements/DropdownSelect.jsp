<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>
<c:set var="styleBaseClass" value="${element.inline ? 'form-row' : 'form-item'}"/>
<div class="${styleBaseClass}">
    <label class="${styleBaseClass}-label" for="label-${element.id}">${element.title}</label>
    <div class="${styleBaseClass}-content">
    <select ${element.attributeString}>
        <c:forEach var="option" items="${element.options}">
            <c:set value="${element.id}.${option.id}" var="optionId" scope="page"/>
            <option name="${optionId}"
                    value="${option.value}" ${(categoryData[element.id] eq option.value) ? "selected=\"selected\"" : ""} ${option.attributeString}>${option.title}</option>
        </c:forEach>
    </select>
    <small>${element.help}</small>
    </div>
    <div class="clear"></div>

    <haku:viewChilds element="${element}"/>
</div>




