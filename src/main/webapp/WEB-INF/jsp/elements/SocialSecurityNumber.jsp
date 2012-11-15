<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>

<c:set var="ssnElement" value="${element}"/>

<c:set var="element" value="${ssnElement.ssn}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/elements/TextQuestion.jsp"/>

<c:set var="element" value="${ssnElement.sex}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/elements/Radio.jsp"/>

<script>
    (function() {
        var ssnId = "<c:out value="${ssnElement.ssn.id}"/>";
        $("#"+ssnId).change(function() {
            var maleReg = /\d{6}\S\d{2}[13579]\w/;
            var femaleReg = /\d{6}\S\d{2}[2468]\w/;
            if (maleReg.test($("#"+ssnId).val())) {
                $("#<c:out value="${ssnElement.maleId}"/>").attr("checked", true);
            }
            if (femaleReg.test($("#"+ssnId).val())) {
                $("#<c:out value="${ssnElement.femaleId}"/>").attr("checked", true);
            }
        });
    }());
</script>
 
