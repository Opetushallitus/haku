<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="foo">
       <script language="javascript">
                function triggerRule(){
                   $('.foo').empty();
                   $('.foo').append('<input type="hidden" id="${element.id}" name="${element.id}" value="ok">');
                   $('#checkbox_value').on('click', function submit(event){
                         $('#checkbox_value').closest("form").submit()
                      });
                }




                $(document).ready(triggerRule());
            </script>
    <c:choose>
        <c:when test="${categoryData[element.id] eq 'ok'}">
            <c:set var="child" value="${element.related[element.id]}"/>
            <c:set var="element" value="${element.related[element.id]}" scope="request"/>
            <jsp:include page="${child.type}.jsp"/>
        </c:when>
        <c:otherwise>

        <noscript>
             <input type="submit" id="${element.id}" name="${element.id}" value="ok"/>
        </noscript>
        </c:otherwise>

    </c:choose>

</div>
