<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>

<script>
$.get("${contextPath}/virkailija/hakemus/${it.application.oid}/valintaView",
        function (data) {
            $('#hakemusValinnassa').html(data);
            $('#hakemusValinnassa').find('*').prop('disabled', true);
        }
);

</script>

<div id="hakemusValinnassa">here be the hakemus</div>