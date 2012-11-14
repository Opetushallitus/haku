<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="/WEB-INF/tags/functions.tld"%>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>
<div id="${element.id}">
    <c:set var="key" value="${element.relatedElementId}"/>
    <script type="text/javascript">
        (function(){  
            $("[name=\"${key}\"]").change(function(event){
                var childIds = [<c:forEach var="child" items="${element.children}" varStatus="status">"${child.id}"${not status.last ? ', ' : ''}</c:forEach>],
                ruleChilds = $("#${element.id} .rule-childs");
                if ($(this).val().search("${element.expression}") !== -1) {
                    if (ruleChilds.html().trim() === "") {
                        ruleData.getRuleChild(childIds, 0, ruleChilds);
                    }
                } else {
                    ruleChilds.html("");
                }
            });
            
            var ruleData = {
                getRuleChild : function(childIds, index, ruleChilds) {
                    $.get('/haku/lomake/${hakemusId.applicationPeriodId}/${hakemusId.formId}/' + childIds[index],
                        function(data) {
                            ruleChilds.append(data);
                            if (childIds.length - 1 > index) {
                                ruleData.getRuleChild(childIds, ++index, ruleChilds);
                            } else {
                                formReplacementsApi.replaceElements();
                            }
                        });
                }
            };
        })();
    </script>
    <div class="rule-childs">
        <c:choose>
            <c:when test="${not empty categoryData[key]}">
                <c:if test="${fn:evaluate(categoryData[key], element.expression)}">
                    <haku:viewChilds element="${element}"/>
                </c:if>
            </c:when>
        </c:choose>
    </div>
    <noscript>
        <input type="submit" id="enabling-submit" name="enabling-submit" value="Ok"/>
    </noscript>
</div>
