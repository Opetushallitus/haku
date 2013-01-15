<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- hidden custom languge row that can be cloned and
included into the table to add new languages
this always has to be the last row--%>
    <tr>
        <td>
            
                <c:out value="${element.customLanguageTitle}"/>&nbsp;
                <select>
                    <option></option>
                    <c:forEach var="scopeOption" items="${element.scopeOptions}">
                        <option value="${scopeOption.value}">${scopeOption.title}</option>
                    </c:forEach>
                </select>
                <select>
                    <option></option>
                    <c:forEach var="languageOption" items="${element.languageOptions}">
                        <option value="${languageOption.value}">${languageOption.title}</option>
                    </c:forEach>
                </select>
                
                <a href="#" class="btn-remove"></a>
        </td>
        <td>
            <div class="field-container-select">
                <select name="arvosana-1" placeholder="Valitse">
                    <option></option>
                    <c:forEach var="grade" items="${element.gradeRange}">
                        <option value="${grade.value}">${grade.title}</option>
                    </c:forEach>
                </select>
            </div>
        </td>
        <td>
            <div class="field-container-select">
                <select name="arvosana-1" placeholder="Valitse">
                    <option></option>
                    <c:forEach var="grade" items="${element.gradeRange}">
                        <option value="${grade.value}">${grade.title}</option>
                    </c:forEach>
                </select>
            </div>
        </td>
    </tr>
