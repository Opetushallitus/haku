<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>

    <jsp:include page="head.jsp"/>
    
<body>
<div id="viewport">
    <div id="overlay"></div>
    <div id="site">

        <jsp:include page="siteheader.jsp"/>

        <jsp:include page="navigation.jsp"/>

        <div class="search">
            <form>
                <input type="text" name="term" required="required" value="${parameters.term}"/>
                <input type="submit" value="Hae"/>
            </form>
        </div>

        <section id="vapaasanahaku" class="content-container">


            <div class="grid16-12">

                <div class="tabs">
                    <a href="#" data-tabs-group="applicationtabs" data-tabs-id="haut" class="tab current"><span>Koulutuksia (<c:out value="${searchResult.size}"/> kpl)</span></a>
                    <a href="#" data-tabs-group="applicationtabs" data-tabs-id="koulutukset" class="tab"><span>Koulutustietoa (12 kpl)</span></a>
                    <a href="#" data-tabs-group="applicationtabs" data-tabs-id="hakukohteet" class="tab"><span>Tarinoita (3 kpl)</span></a>
                </div>


                <section id="koulutuksia" class="tabsheets">

                    <form id="hakutulokset" action="" method="post">
                        <div class="grid16-6">
                            <jsp:include page="filters.jsp"/>
                        </div>

                        <div class="grid16-10">
                            <h1>Hakusana: <c:out value="${parameters.term}"/></h1>


                            <div class="field-container-select set-right">
                                <select name="Aakkosjarjestys" placeholder="Järjestä aakkosittain" id="Aakkosjarjestys">
                                    <option name="Aakkosjarjestys.Aakkosjarjestys.laskeva" value="Kevät 2013"
                                            selected="selected" id="Aakkosjarjestys-laskeva">laskeva
                                    </option>
                                    <option name="Aakkosjarjestys.Aakkosjarjestys.nouseva" value="Syksy 2014"
                                            id="Aakkosjarjestys-nouseva">nouseva
                                    </option>
                                </select>
                            </div>

                            <button class="set-right">
                                <span><span>aakkosjärjestys</span></span>
                            </button>
                            <c:forEach var="item" items="${searchResult.items}">
                                <div class="form-row">
                                    <ul class="minimal set-left">
                                        <a href="tarjontatiedot/${item['tunniste']}"><c:out
                                                value="${item['nimi']}"/></a>
                                    </ul>
                                    <div class="set-right"><input type="checkbox"/><span class="label">Lisää muistilistaan</span>
                                    </div>
                                </div>
                            </c:forEach>

                        </div>
                        <div class="clear"></div>
                    </form>
                </section>
            </div>

            <aside id="sidemenu" class="grid16-4">
                <jsp:include page="notelist.jsp"/>
                <jsp:include page="compare.jsp"/>
                <jsp:include page="authentication.jsp"/>
            </aside>

            <div class="clear"></div>
        </section>
    </div>
    <footer></footer>
</div>
</body>
</html>




