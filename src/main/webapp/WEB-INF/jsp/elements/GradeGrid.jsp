<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


	<table class="applicant-grades">
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
		    <c:forEach var="child" items="${element.children}">
            <c:set var="element" value="${child}" scope="request"/>

			<tr>
				<td><jsp:include page="${child.type}.jsp"/></td>
				<td>8</td>
				<td></td>
				<td>
					<div class="field-container-select">
						<select name="arvosana-1" placeholder="Valitse" id="arvosana-1">
							<option></option>
							<option>Ei arvosanaa</option>
							<option>10</option>
							<option>9</option>
							<option>8</option>
							<option>7</option>
							<option>6</option>
							<option>5</option>
							<option>4</option>
						</select>
					</div>
				</td>
				<td>
					<div class="field-container-select">
						<select name="arvosana-1" placeholder="Valitse" id="arvosana-2">
							<option></option>
							<option>Ei arvosanaa</option>
							<option>10</option>
							<option>9</option>
							<option>8</option>
							<option>7</option>
							<option>6</option>
							<option>5</option>
							<option>4</option>
						</select>
					</div>
				</td>
			</tr>
            </c:forEach>

		</tbody>
	</table>
