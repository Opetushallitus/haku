<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<input type="hidden" ${element.attributeString} value="${element.value}"/>
<haku:viewChilds element="${element}"/>