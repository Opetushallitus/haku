<%@ tag description="breadcrumbs" body-content="empty" pageEncoding="UTF-8" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<nav id="navigation" class="grid16-16">
    <ul class="level1">
        <li><a href="#" class=""><span><fmt:message key="virkailija.haku.organisaation.tiedot"/></span></a></li>
        <li>
            <a href="index.html" class="current"><span><fmt:message key="virkailija.haku.koulutustarjonta"/></span></a>
            <ul class="level2">
                <li><a href="#" class=""><fmt:message key="virkailija.haku.koulutuksen.tiedot"/></a></li>
                <li><a href="#" class=""><fmt:message key="virkailija.haku.koulutuksen.toteutus"/></a></li>
                <li><a href="#" class=""><fmt:message key="virkailija.haku.organisaation.kuvailevat.tiedot"/></a></li>
                <li><a href="#" class=""><fmt:message key="virkailija.haku.jarjestamissopimukset"/></a></li>
                <li><a href="#" class=""><fmt:message key="virkailija.haku.jarjestamisluvat"/></a></li>
            </ul>
        </li>
        <li><a href="#" class=""><span><fmt:message key="virkailija.haku.valintaperusteet"/></span></a></li>
        <li><a href="#" class=""><span><fmt:message key="virkailija.haku.koulutussuunnittelu"/></span></a></li>
        <li><a href="#" class=""><span><fmt:message key="virkailija.haku.sisallonhallinta"/></span></a></li>
    </ul>
</nav>
