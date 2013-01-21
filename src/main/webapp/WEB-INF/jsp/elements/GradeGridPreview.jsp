<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"  %>
	<!--
    <table id="gradegrid-table" class="applicant-grades">
		<thead>
          -->
			<tr>
				<th></th>
				<th colspan="2"><spring:message code="lomake.component.gradegrid.gradesTitle"/></th>
			</tr>
			<tr>
				<td><spring:message code="lomake.component.gradegrid.subjectTitle"/></td>
				<td><spring:message code="lomake.component.gradegrid.commonSubjectColumnTitle"/></td>
				<td><spring:message code="lomake.component.gradegrid.optionalSubjectColumnTitle"/></td>
			</tr>
            <!--
		</thead>
		<tbody>
        -->
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
                        <td><c:out value="${element.customLanguageTitle}"/>&nbsp;
                        <c:forEach var="scopeOption" items="${element.scopeOptions}">
                            <c:if test="${(categoryData[customScopeKey] eq scopeOption.value)}">
                                    <c:out value="${scopeOption.title}"/>&nbsp;
                            </c:if>
                        </c:forEach>
                        <c:forEach var="languageOption" items="${element.languageOptions}">
                            <c:if test="${(categoryData[customLanguageKey] eq languageOption.value)}">
                                    <c:out value="${languageOption.title}"/>
                            </c:if>
                        </c:forEach>
                        </td>
                        <td>
                            <c:forEach var="grade" items="${element.gradeRange}">
                                <c:if test="${(categoryData[customCommonGradeKey] eq grade.value)}">
                                        <c:out value="${grade.title}"/>
                                </c:if>
                            </c:forEach>
                        </td>
                        <td>
                            <c:forEach var="grade" items="${element.gradeRange}">
                                <c:if test="${(categoryData[customOptionalGradeKey] eq grade.value)}">
                                        <c:out value="${grade.title}"/>
                                </c:if>
                            </c:forEach>
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
            <!--
		</tbody>
    -->
	<!--</table>-->
