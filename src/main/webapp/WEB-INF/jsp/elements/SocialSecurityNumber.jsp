<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>

<%--
  ~ Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
  ~
  ~ This program is free software:  Licensed under the EUPL, Version 1.1 or - as
  ~ soon as they will be approved by the European Commission - subsequent versions
  ~ of the EUPL (the "Licence");
  ~
  ~ You may not use this work except in compliance with the Licence.
  ~ You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ European Union Public Licence for more details.
  --%>

<c:set var="ssnElement" value="${element}"/>

<c:set var="element" value="${ssnElement.ssn}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/elements/TextQuestion.jsp"/>

<c:set var="element" value="${ssnElement.sex}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/elements/Radio.jsp"/>

<script>
    (function() {
        var ssnId = "<c:out value="${ssnElement.ssn.id}"/>";
        $("#" + ssnId).change(function() {
            var maleReg   = /\d{6}[-+aA]\d{2}[13579]\w/;
            var femaleReg = /\d{6}[-+aA]\d{2}[02468]\w/;
            if (maleReg.test($("#" + ssnId).val())) {
                $("#<c:out value="${ssnElement.maleId}"/>").attr("checked", true);
            }
            if (femaleReg.test($("#" + ssnId).val())) {
                $("#<c:out value="${ssnElement.femaleId}"/>").attr("checked", true);
            }
        });
    }());
</script>

