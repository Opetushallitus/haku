<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"  %>
<c:set var="baseUrl" value="/haku/lomake/${hakemusId.applicationPeriodId}/${hakemusId.formId}" scope="request"/>
<c:choose>
    <c:when test="${vaihe.hasPrev}">
        <form method="get" action="${baseUrl}/${vaihe.prev.id}">
            <div class="set-left">
                <button class="left" name="nav-prev" type="submit" value="true"><span><span><spring:message code="lomake.button.previous" /></span></span></button>
            </div>
        </form>
    </c:when>
</c:choose>
<div class="set-right">
    <c:choose>
        <c:when test="${vaihe.hasNext}">
            <form method="get" action="${baseUrl}/${vaihe.next.id}">
                <div class="set-left">
                    <button class="right" name="nav-next" type="submit" value="true"><span><span><spring:message code="lomake.button.next" /></span></span></button>
                </div>
            </form>
        </c:when>
        <c:otherwise>
            <form method="post" action="${baseUrl}/send">
                <div class="set-left">
                    <button class="right" name="nav-send" type="submit" value="true"><span><span><spring:message code="lomake.button.send" /></span></span></button>
                </div>
            </form>
        </c:otherwise>
    </c:choose>
</div>
<div class="clear"></div>
