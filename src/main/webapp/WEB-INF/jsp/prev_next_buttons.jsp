<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:choose>
    <c:when test="${category.hasPrev}"><input class="nav-prev" name="nav-prev" type="submit" value="Edellinen"/></c:when>
</c:choose>
<c:choose>
    <c:when test="${category.hasNext}"><input class="nav-next" name="nav-next" type="submit" value="Seuraava"/></c:when>
    <c:when test="${!category.hasNext}"><input class="nav-save" name="nav-save" type="submit" value="Tallenna"/></c:when>
</c:choose>
