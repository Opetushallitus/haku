<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="/WEB-INF/tags/functions.tld"%>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>
<div id="${element.id}">
    <c:set var="key" value="${element.relatedElementId}"/>
    <c:if test="${parent ne null}">
        <script type="text/javascript">
            (function(){  
                $("[name=\"${key}\"]").change(function(event){
                    if ($(this).val().search("${element.expression}") !== -1) {
                        $.get('/haku/lomake/${hakemusId.applicationPeriodId}/${hakemusId.formId}/${element.id}', {"${key}" : $(this).val()},
                            function(data) {
                                $("#${element.id}").replaceWith(data);
                                formReplacementsApi.replaceElements();
                            });
                    } else {
                        $("#${element.id}").html("");
                    }
                });
            })();
        </script>
    </c:if>
    <c:choose>
        <c:when test="${not empty categoryData[key]}">
            <c:if test="${fn:evaluate(categoryData[key], element.expression)}">
                <haku:viewChilds element="${element}"/>
            </c:if>
        </c:when>
        <c:otherwise>
            <noscript>
                <input type="submit" id="enabling-submit" name="enabling-submit" value="Ok"/>
            </noscript>
        </c:otherwise>
    </c:choose>
</div>
