<%@ taglib uri="http://java.sun.com/jsp/jstl/core"
           prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>
<fieldset class="form-row">
    <legend class="form-row-label"><c:out value="${element.title}"/></legend>
    <div class="form-row-content">

    <c:set var="value" value="${(empty value) ? categoryData[element.id] : value}"/>
    <c:forEach var="option" items="${element.options}" varStatus="status">
        <div class="field-container-radio">
            <c:set var="id" value="${element.id}_${option.id}"/>
            <input id="${id}" type="radio" name="${element.id}"
                value="${option.value}" ${(!empty disabled) ? "disabled=\"true\" " : " "} ${(value eq option.value) ? "checked=\"checked\" " : " "} ${option.attributeString}/>
            <label for="${option.id}">${option.title}</label>
            <div id="help-${element.id}-${option.id}"><small>${option.help}</small></div>
        </div>
    </c:forEach>

    </div>
    <div class="clear"></div>
    <haku:viewChilds element="${element}"/>
</fieldset>
