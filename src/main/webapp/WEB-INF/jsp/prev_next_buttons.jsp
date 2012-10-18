<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:choose>
    <c:when test="${category.hasPrev}">
        <div class="set-left">
            <button class="left" name="nav-prev" type="submit" value="true"><span><span>Edellinen</span></span></button>
        </div>
    </c:when>
</c:choose>
<div class="set-right">
<c:choose>
    <c:when test="${category.hasNext}">
        <button class="right" name="nav-next" type="submit" value="true"><span><span>Seuraava</span></span></button>
    </c:when>
    <c:when test="${!category.hasNext}">
        <button class="right" name="nav-save" type="submit" value="true"><span><span>Tallenna</span></span></button>
    </c:when>
</c:choose>
</div>
<div class="clear"></div>
