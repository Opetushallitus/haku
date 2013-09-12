<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="/WEB-INF/tld/functions.tld" %>


<section>
    <hr>
    <h3><haku:i18nText value="${element.i18nText}"/></h3>
    <table>
        <tbody>
        <haku:viewChilds element="${element}"/>
        </tbody>
    </table>
</section>
