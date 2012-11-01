<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"  %>

<div class="set-left">
    <c:choose>
        <c:when test="${category.hasPrev}">
            <button class="left" name="nav-prev" type="submit" value="true"><span><span><spring:message code="lomake.button.previous" /></span></span></button>
        </c:when>
    </c:choose>
    <c:if test="${not empty sessionScope['username']}">
        <button name="save-draft" type="submit" value="true"><span><span><spring:message code="lomake.button.saveasdraft" /></span></span></button>
    </c:if>
</div>
<div class="set-right">
<c:choose>
    <c:when test="${category.hasNext}">
        <button class="right" name="nav-next" type="submit" value="true">
            <span>
                <span>
                    <c:choose>
                        <c:when test="${category.next.preview}">
                            <spring:message code="lomake.button.preview" />
                        </c:when>
                        <c:otherwise>
                            <spring:message code="lomake.button.next" />
                        </c:otherwise>
                    </c:choose>
                </span>
            </span>
        </button>
    </c:when>
    <c:otherwise>
        <button class="right" name="nav-save" type="submit" value="true"><span><span><spring:message code="lomake.button.save" /></span></span></button>
    </c:otherwise>
</c:choose>
</div>
<div class="clear"></div>
