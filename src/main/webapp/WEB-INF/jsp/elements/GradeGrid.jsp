<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script type="text/javascript" src="/haku/resources/javascript/gradegrid.js"></script>

	<table id="gradegrid-table" class="applicant-grades">
		<thead>
			<tr>
				<th></th>
				<th colspan="2"><c:out value="${element.registryGradesTitle}"/></th>
				<th colspan="2"><c:out value="${element.alteringGradesTitle}"/></th>
			</tr>
			<tr>
				<td>Oppiaine(s)</td>
				<td><c:out value="${element.commonSubjectColumnTitle}"/></td>
				<td><c:out value="${element.optionalSubjectColumnTitle}"/></td>
				<td><c:out value="${element.commonSubjectColumnTitle}"/></td>
				<td><c:out value="${element.optionalSubjectColumnTitle}"/></td>
			</tr>
		</thead>
		<tbody>
		    <c:forEach var="subject" items="${element.subjectsBeforeLanguages}">
		        <c:set var="subject" value="${subject}" scope="request"/>
			    <tr>
				    <td><jsp:include page="gradegrid/SubjectRow.jsp"/></td>
				    <td>8</td>
				    <td></td>
				    <td>
				        <jsp:include page="gradegrid/gradeselect.jsp"/>
				    </td>
				    <td>
					    <jsp:include page="gradegrid/gradeselect.jsp"/>
				    </td>
			    </tr>
            </c:forEach>

            <c:forEach var="language" items="${element.languages}">
                <c:set var="language" value="${language}" scope="request"/>
                <tr class="gradegrid-language-row">
                    <td><jsp:include page="gradegrid/LanguageRow.jsp"/></td>
                	<td>8</td>
                	<td></td>
                	<td>
                	    <jsp:include page="gradegrid/gradeselect.jsp"/>
                	</td>
                	<td>
                	    <jsp:include page="gradegrid/gradeselect.jsp"/>
                	</td>
                </tr>
            </c:forEach>
            <tr>
                <td colspan=5>
                    <p id="add_language_button" >Lisaa kieli (s)</p>
                </td>
            </tr>

            <c:forEach var="subject" items="${element.subjectsAfterLanguages}">
                <c:set var="subject" value="${subject}" scope="request"/>
            	<tr>
            	    <td><jsp:include page="gradegrid/SubjectRow.jsp"/></td>
            		<td>8</td>
            		<td></td>
            		<td>
            		    <jsp:include page="gradegrid/gradeselect.jsp"/>
            	    </td>
            		<td>
            		    <jsp:include page="gradegrid/gradeselect.jsp"/>
            		</td>
                </tr>
            </c:forEach>

		</tbody>
	</table>
