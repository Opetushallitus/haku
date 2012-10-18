<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"  %>
<c:choose>
    <c:when test="${category.hasPrev}">
        <div class="set-left">
            <button class="left" name="nav-prev" type="submit" value="true"><span><span><s:message code="lomake.button.previous" /></span></span></button>
        </div>
    </c:when>
</c:choose>
<div class="set-right">
<c:choose>
    <c:when test="${category.hasNext}">
        <button class="right" name="nav-next" type="submit" value="true"><span><span><s:message code="lomake.button.next" /></span></span></button>
    </c:when>
    <c:when test="${!category.hasNext}">
        <button class="right" name="nav-save" type="submit" value="true"><span><span><s:message code="lomake.button.save" /></span></span></button>
    </c:when>
</c:choose>
</div>
<div class="clear"></div>
