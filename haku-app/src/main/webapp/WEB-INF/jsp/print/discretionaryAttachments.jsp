<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

 <c:if test="${not empty discretionaryAttachmentAOIds}">
    <section id="discretionaryAttachments">
        <hr>
        <h3><fmt:message key="lomake.tulostus.liitteet"/></h3>
        <p><fmt:message key="lomake.tulostus.liitteet.harkinnanvaraisuus"/></p>
        <div id="discretionaryAttachmentAddresses"></div>
    </section>
    <script type="text/javascript">
        var discretionaryAttachmentAOIds = [<c:forEach var="aoId" items="${discretionaryAttachmentAOIds}" varStatus="status">"${aoId}"${not status.last ? ', ' : ''}</c:forEach>];
        <c:if test="${fn:containsIgnoreCase(it.koulutusinformaatioBaseUrl, 'http') or fn:startsWith(it.koulutusinformaatioBaseUrl, '/')}">
            var koulutusinformaatioBaseUrl =  "${it.koulutusinformaatioBaseUrl}";
        </c:if>
        <c:if test="${not fn:containsIgnoreCase(it.koulutusinformaatioBaseUrl, 'http') and not fn:startsWith(it.koulutusinformaatioBaseUrl, '/')}">
            var koulutusinformaatioBaseUrl =  location.protocol + "//${it.koulutusinformaatioBaseUrl}";
        </c:if>
        var deliveryDeadlineLabel = '<fmt:message key="lomake.tulostus.liitteet.deadline"/>';
        $(document).ready(function () {
            var $discretionaryAttachments = $("#discretionaryAttachmentAddresses");
            for (var i in discretionaryAttachmentAOIds) {
                $.getJSON(koulutusinformaatioBaseUrl + '/ao/' + discretionaryAttachmentAOIds[i], function(data) {
                    if (data.attachmentDeliveryAddress) {
                        var addrsHtml = '<address>', addrs = data.attachmentDeliveryAddress, provider = data.provider;
                        if (provider) {
							if (provider.name) {
								addrsHtml = addrsHtml.concat(provider.name + '<br/>');
							}
                        }
                        if (addrs.streetAddress) {
                            addrsHtml = addrsHtml.concat(addrs.streetAddress + '<br/>');
                        }
                        if (addrs.streetAddress2) {
                            addrsHtml = addrsHtml.concat(addrs.streetAddress2 + '<br/>');
                        }
                        if (addrs.postalCode) {
                            addrsHtml = addrsHtml.concat(addrs.postalCode + ' ');
                        }
                        if (addrs.postOffice) {
                            addrsHtml = addrsHtml.concat(addrs.postOffice);
                        }
                        if (data.attachmentDeliveryDeadline) {
                            var deadline = new Date(data.attachmentDeliveryDeadline);
                            var date = deadline.getDate();
                            var month = deadline.getMonth() + 1;
                            var year = deadline.getFullYear();
                            addrsHtml = addrsHtml.concat('<br/>' + deliveryDeadlineLabel + ' ' + date + '.' + month + ' ' + year);
                        }
                        addrsHtml = addrsHtml.concat('</address><br/>');
                        $discretionaryAttachments.append(addrsHtml);
                    }
                });
            }
        });
    </script>
 </c:if>
