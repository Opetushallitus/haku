<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>
<div class="form-row">
    <label id="label-${element.id}" for="${element.id}" class="form-row-label"><c:out value="${element.title}"/></label>
    <div class="form-row-content">
        <div class="field-container-text">
            <input type="text" ${element.attributeString} value="${categoryData[element.id]}" class="postal-code"/><span class="required_field">${errorMessages[element.id]}</span>
            <input type="hidden" value="${categoryData['postitoimipaikka']}" name="postitoimipaikka" class="post-office"/>
            <span class="post-office"><c:out value="${categoryData['postitoimipaikka']}"/></span>
        </div>
        <div id="help-${element.id}"><small><c:out value="${element.help}"/></small></div>
    </div>
    <div class="clear"></div>
    <haku:viewChilds element="${element}"/>
</div>
<script type="text/javascript" src="/haku/resources/javascript/postalcode.js"></script>
