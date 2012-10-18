<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
	<table id="gradegrid-table" class="applicant-grades">
		<thead>
			<tr>
				<th></th>
				<th colspan="2"><c:out value="${element.gradesTitle}"/></th>
			</tr>
			<tr>
				<td><c:out value="${element.subjectTitle}"/></td>
				<td><c:out value="${element.commonSubjectColumnTitle}"/></td>
				<td><c:out value="${element.optionalSubjectColumnTitle}"/></td>
			</tr>
		</thead>
		<tbody>
		    <!-- subjects that are listed before languages -->
		    <c:forEach var="subject" items="${element.subjectsBeforeLanguages}">
		        <c:set var="subject" value="${subject}" scope="request"/>
			    <tr>
				    <td><jsp:include page="gradegrid/SubjectRow.jsp"/></td>
				    <td>
				        <c:set var="gradeSelectId" value="common-${subject.id}" scope="request"/>
				        <jsp:include page="gradegrid/gradeselectPreview.jsp"/>
				    </td>
				    <td>
                        <c:set var="gradeSelectId" value="optional-${subject.id}" scope="request"/>
                	    <jsp:include page="gradegrid/gradeselectPreview.jsp"/>
				    </td>
			    </tr>
            </c:forEach>

            <%-- languages --%>
            <c:forEach var="language" items="${element.languages}">
                <c:set var="language" value="${language}" scope="request"/>
                <tr class="gradegrid-language-row">
                    <td><jsp:include page="gradegrid/LanguageRowPreview.jsp"/></td>
                	<td>
                	    <c:set var="gradeSelectId" value="common-${language.id}" scope="request"/>
                	    <jsp:include page="gradegrid/gradeselectPreview.jsp"/>
                	</td>
                	<td>
                	    <c:set var="gradeSelectId" value="optional-${language.id}" scope="request"/>
                	    <jsp:include page="gradegrid/gradeselectPreview.jsp"/>
                	</td>
                </tr>
            </c:forEach>

            <%-- custom selected languages --%>
            <c:forEach var="entry" items="${categoryData}">

                <c:if test="${fn:startsWith(entry.key,'custom-scope')}">
                <c:set var="customIndex" value="${fn:substringAfter(entry.key, 'custom-scope_')}" scope="page"/>

                <c:set var="customScopeKey" value="custom-scope_${customIndex}" scope="page"/>
                <c:set var="customLanguageKey" value="custom-language_${customIndex}" scope="page"/>
                <c:set var="customCommonGradeKey" value="custom-commongrade_${customIndex}" scope="page"/>
                <c:set var="customOptionalGradeKey" value="custom-optionalgrade_${customIndex}" scope="page"/>

                    <tr class="gradegrid-language-row gradegrid-custom-language-row">
                                    <td><c:out value="${element.customLanguageTitle}"/>
                                    <select id="${customScopeKey}" name="${customScopeKey}">
                                        <option></option>
                                        <c:forEach var="scopeOption" items="${element.scopeOptions}">
                                            <option value="${scopeOption.value}"
                                            ${(categoryData[customScopeKey] eq scopeOption.value) ? "selected=\"selected\"" : ""}>
                                            ${scopeOption.title}</option>
                                        </c:forEach>
                                    </select>
                                    <select id="${customLanguageKey}" name="${customLanguageKey}">
                                        <option></option>
                                        <c:forEach var="languageOption" items="${element.languageOptions}">
                                            <option value="${languageOption.value}"
                                            ${(categoryData[customLanguageKey] eq languageOption.value) ? "selected=\"selected\"" : ""}>
                                            ${languageOption.title}</option>
                                        </c:forEach>
                                    </select>
                                    </td>
                                    <td>
                                        <div class="field-container-select">
                                            <select id="${customCommonGradeKey}" name="${customCommonGradeKey}" placeholder="Valitse">
                                            <option></option>
                                            <c:forEach var="grade" items="${element.gradeRange}">
                            	                <option value="${grade.value}" ${(categoryData[customCommonGradeKey] eq grade.value) ? "selected=\"selected\"" : ""}>${grade.title}</option>
                                    	    </c:forEach>
                        	    </select>
                            </div>
                        </td>
                        <td>
                            <div class="field-container-select">
                                <select id="${customOptionalGradeKey}" name="${customOptionalGradeKey}" placeholder="Valitse">
                                    <option></option>
                                    <c:forEach var="grade" items="${element.gradeRange}">
                            	                <option value="${grade.value}" ${(categoryData[customOptionalGradeKey] eq grade.value) ? "selected=\"selected\"" : ""}>${grade.title}</option>
                                    </c:forEach>
                        	    </select>
                            </div>
                        </td>
                    </tr>
                </c:if>

            </c:forEach>
            <%-- subjects that are listed after languages --%>
            <c:forEach var="subject" items="${element.subjectsAfterLanguages}">
                <c:set var="subject" value="${subject}" scope="request"/>
            	<tr>
            	    <td>
            	        <jsp:include page="gradegrid/SubjectRow.jsp"/>
            	    </td>
            		<td>
            		    <c:set var="gradeSelectId" value="common-${subject.id}" scope="request"/>
            		    <jsp:include page="gradegrid/gradeselectPreview.jsp"/>
            	    </td>
            		<td>
            		    <c:set var="gradeSelectId" value="optional-${subject.id}" scope="request"/>
            		    <jsp:include page="gradegrid/gradeselectPreview.jsp"/>
            		</td>
                </tr>
            </c:forEach>

            <%-- subjects that are specific to the education selected by the user --%>
            <c:forEach var="subject" items="${additionalQuestionList}">
                <c:set var="subject" value="${subject}" scope="request"/>
                <tr>
                    <td>
                        <jsp:include page="gradegrid/SubjectRow.jsp"/>
                    </td>
                    <td>
                        <c:set var="gradeSelectId" value="common-${subject.id}" scope="request"/>
                        <jsp:include page="gradegrid/gradeselectPreview.jsp"/>
                    </td>
                    <td>
                        <c:set var="gradeSelectId" value="optional-${subject.id}" scope="request"/>
                        <jsp:include page="gradegrid/gradeselectPreview.jsp"/>
                    </td>
                </tr>
            </c:forEach>
		</tbody>
	</table>
