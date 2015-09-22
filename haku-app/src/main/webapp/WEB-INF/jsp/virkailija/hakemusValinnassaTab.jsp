<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>

<iframe id="hakemusValinnassa"
        src="http://localhost:9090${contextPath}/virkailija/hakemus/${it.application.oid}/valintaView"
        width="100%"
        frameborder="0">
</iframe>
<script>
    setInterval(function() {
        var frame = $('#hakemusValinnassa');
        frame.height(frame.contents().find('body').height() + 30);
        frame.contents().find('input,select,button').prop('disabled', true);
    }, 3000);
</script>
