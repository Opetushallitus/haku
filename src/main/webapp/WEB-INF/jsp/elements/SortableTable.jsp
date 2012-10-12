<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<table class="preference-sort">
    <tbody>
        <c:forEach var="child" items="${element.children}" varStatus="status">
            <tr>
                <td class="index">
                    <c:out value="${child.title}"/>
                    <br/>
                    <c:if test="${not status.first}">
                        <button class="up sort" data-id="${child.id}" data-target="${element.children[status.index - 1].id}" type="button">
                            <span>
                                <span><c:out value="${element.moveUpLabel}"/></span>
                            </span>
                        </button>
                        <br/>
                    </c:if>
                    <c:if test="${not status.last}">
                        <button class="down sort" data-id="${child.id}" data-target="${element.children[status.index + 1].id}" type="button">
                            <span>
                                <span><c:out value="${element.moveDownLabel}"/></span>
                            </span>
                        </button>
                    </c:if>
                </td>
                <td>
                    <c:set var="index" value="${status.count}" scope="request"/>
                    <c:set var="sortableItem" value="${child}" scope="request"/>
                    <jsp:include page="${sortableItem.type}.jsp"/>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>
<script type="text/javascript" src="/haku/resources/javascript/sortabletable.js"></script>
<script type="text/javascript" src="/haku/resources/javascript/preferencerow.js"></script>