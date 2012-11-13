<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>
<c:set var="styleBaseClass" value="${element.inline ? 'form-row' : 'form-item'}"/>
<div class="${styleBaseClass}">
    <label id="label-${element.id}" for="${element.id}" class="${styleBaseClass}-label">${element.title}</label>
    <div class="${styleBaseClass}-content">
        <div class="field-container-text">
            <input ${element.attributeString} value="${categoryData[element.id]}"/><span class="required_field">${errorMessages[element.id]}</span>
        </div>
        <div id="help-${element.id}"><small>${element.help}</small></div>
    </div>
    <div class="clear"></div>
    <haku:viewChilds element="${element}"/>
</div>

