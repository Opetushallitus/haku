<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>
<div class="form-row">
    <label id="label-${element.id}" for="${element.id}" class="form-row-label">${element.title}</label>
    <div class="form-row-content">
        <div class="field-container-text">
            <input ${element.attributeString} value="${categoryData[element.id]}"/><span class="required_field">${errorMessages[element.id]}</span>
        </div>
        <small>${element.help}</small>
    </div>
    <div class="clear"></div>
    <haku:viewChilds element="${element}"/>
</div>

