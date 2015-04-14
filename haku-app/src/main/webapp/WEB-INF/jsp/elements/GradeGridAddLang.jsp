<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%--
  ~ Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
  ~
  ~ This program is free software:  Licensed under the EUPL, Version 1.1 or - as
  ~ soon as they will be approved by the European Commission - subsequent versions
  ~ of the EUPL (the "Licence");
  ~
  ~ You may not use this work except in compliance with the Licence.
  ~ You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ European Union Public Licence for more details.
  --%>
<c:choose>
    <c:when test="${virkailijaEdit}">
        <jsp:include page="GradeGridAddLangPreview.jsp" />
    </c:when>
    <c:otherwise>
        <select aria-labelledby="${element.id}" id="${element.id}-add-lang-select"></select>
        <button ${element.attributeString} id="${element.id}" class="link" type="button"><haku:i18nText value="${element.i18nText}"/></button>
        <script>
            $(document).ready(function () {

                $("#${element.id}").on('click', function (event) {
                    var select = $("#${element.id}-add-lang-select");
                    var selectedOption = select.children(":selected");
                    var tr = $("#" + selectedOption.val());
                    tr.show();
                    tr.find('*').removeAttr("disabled");
                    selectedOption.remove();
                    if (select.children().length == 0) {
                        $(this).closest('tr').hide();
                    }
                });

                var select = $("#${element.id}-add-lang-select");
                $("tr[data-group=${element.id}]:hidden").each(
                        function (index, item) {
                            var option = $('<option>&nbsp;</option>');
                            option.html($(item).children('td:first').text());
                            option.val($(item).attr('id'));
                            select.append(option);
                        }
                )
            })
        </script>
    </c:otherwise>
</c:choose>