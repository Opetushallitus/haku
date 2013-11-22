<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="popup-dialog-wrapper" id="postProcessApplicationDialog" style="z-index:1000;display:none;">
    <span class="popup-dialog-close">&#8203;</span>

    <div class="popup-dialog">
        <span class="popup-dialog-close">&#8203;</span>

        <div class="popup-dialog-header">
            <h3><fmt:message key="virkailija.hakemus.postProcess.varmistus"/></h3>
        </div>
        <div class="popup-dialog-content">
            <form method="post" id="postProcessForm" action="${contextPath}/virkailija/hakemus/${application.oid}/postProcess">
                <div class="margin-bottom-2">
                    <p>
                        <fmt:message key="virkailija.hakemus.postProcess.viesti"/>
                    </p>
                </div>
                <div>
                    <button class="small" name="nav-send" value="true" data-po-hide="postProcess">
                        <fmt:message key="virkailija.hakemus.syota.peru"/>
                    </button>
                    <button id="submit-dialog" class="primary set-right small" type="submit">
                        <fmt:message key="virkailija.hakemus.syota.laheta"/>
                    </button>
                </div>
                <div class="clear"></div>
            </form>
        </div>
    </div>
</div>
