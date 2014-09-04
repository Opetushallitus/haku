<%@ page session="false"%>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<input type="hidden" ${element.attributeString} id="${element.id}" name="${element.id}" value="${element.value}"/>
<haku:viewChilds element="${element}"/>
