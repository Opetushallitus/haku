<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>

<script>
$.ajax({
    url: "${contextPath}/virkailija/hakemus/${it.application.oid}/valintaView",
    type: "GET",
    success: function (data) {
            $('#hakemusValinnassa').html(data);
            $('#hakemusValinnassa').find('*').prop('disabled', true);
            $('#hakemusValinnassa').find('*').each(function(index) {
                var currId = $(this).attr('id');
                $(this).attr('id', 'valinta_'+currId);
            });
        },
    error: function (data) {
            $('#hakemusValinnassa').html("<p>Hakemuksen tietojen lataaminen valintalaskennasta epäonnistui</p>");
        }
    }
);

</script>

<div id="hakemusValinnassa">Ladataan hakemuksen tietoja valintalaskennasta</div>