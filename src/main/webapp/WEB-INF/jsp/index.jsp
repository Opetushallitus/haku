<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="top/top.jsp"/>

    <section class="content-container">

        <div class="grid16-8">
            <div class="module">
                <figure class="image set-left" style="width:33%;">
                    <img src="content/Poju3.jpg" alt=""/>
                    <figcaption>Lorem ipsum dolor sit amet</figcaption>
                </figure>
                <h1>Lorem ipsum dolor sit amet consectuer adipiscing elit</h1>

                <p>
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam tortor nisi, egestas id
                    pellentesque ac, scelerisque in tortor. Morbi accumsan libero erat. Quisque nisl erat, fringilla
                    quis ullamcorper vel, viverra eu leo. Nulla facilisi. Fusce a leo id tellus molestie imperdiet vel
                    ut augue. Suspendisse interdum malesuada iaculis. Sed et urna ante, id varius ipsum. Fusce imperdiet
                    sapien convallis purus mattis euismod. Quisque et metus sit amet nulla pharetra consequat at vel
                    tellus. Proin vulputate eros at quam rutrum id dignissim magna dictum.
                </p>

                <p>
                    Sed consequat mattis nisi, sit amet imperdiet sapien dignissim non. Etiam vitae velit odio. In hac
                    habitasse platea dictumst. Donec ut lacus eget diam sodales pharetra a eget quam. Donec facilisis mi
                    eget nisi ultricies eleifend. In eleifend metus ut arcu lobortis malesuada. Cum sociis natoque
                    penatibus et magnis dis parturient montes, nascetur ridiculus mus. Proin dictum augue sit amet nibh
                    rutrum imperdiet.
                </p>

                <div class="clear"></div>
            </div>

            <div class="module">
                <figure class="image set-right" style="width:25%;">
                    <img src="content/IMG_0551.jpg" alt=""/>
                    <figcaption>Lorem ipsum dolor sit amet</figcaption>
                </figure>
                <span class="h4">Tiedote</span>

                <h2>Lorem ipsum dolor sit amet, consectetur adipiscing elit</h2>

                <p>
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam tortor nisi, egestas id
                    pellentesque ac, scelerisque in tortor. Morbi accumsan libero erat. Quisque nisl erat, fringilla
                    quis ullamcorper vel, viverra eu leo. Nulla facilisi. Fusce a leo id tellus molestie imperdiet vel
                    ut augue. Suspendisse interdum malesuada iaculis. Sed et urna ante, id varius ipsum. Fusce imperdiet
                    sapien convallis purus mattis euismod. Quisque et metus sit amet nulla pharetra consequat at vel
                    tellus. Proin vulputate eros at quam rutrum id dignissim magna dictum.
                    <a href="#">Lue lisää</a>
                </p>

                <div class="clear"></div>
            </div>

            <div class="module">
                <figure class="image set-right" style="width:33%;">
                    <img src="content/_MG_4961.jpg" alt=""/>
                    <figcaption>Lorem ipsum dolor sit amet</figcaption>
                </figure>
                <span class="h4">Artikkeli</span>

                <h2>Fusce a leo id tellus molestie imperdiet vel ut augue. </h2>

                <p>
                    Sed consequat mattis nisi, sit amet imperdiet sapien dignissim non. Etiam vitae velit odio. In hac
                    habitasse platea dictumst. Donec ut lacus eget diam sodales pharetra a eget quam. Donec facilisis mi
                    eget nisi ultricies eleifend. In eleifend metus ut arcu lobortis malesuada. Cum sociis natoque
                    penatibus et magnis dis parturient montes, nascetur ridiculus mus. Proin dictum augue sit amet nibh
                    rutrum imperdiet. <a href="#">Lue lisää</a>
                </p>


                <div class="clear"></div>
            </div>

            <div class="module">
                <h3>In eleifend metus ut arcu lobortis malesuada.</h3>
                <figure class="video" style="">
                    <img src="content/tytot2.jpg" alt=""/>
                    <figcaption>Lorem ipsum dolor sit amet</figcaption>
                </figure>
                <h1>Lorem ipsum dolor sit amet consectuer adipiscing elit</h1>

                <div class="clear"></div>
            </div>


        </div>

        <div class="grid16-4">

            <div class="module bottom-border">
                <h3>Hakukalenteri</h3>

                <ul class="unstyled">
                    <li><span class="uppercase">Yhteishaku</span> nuorten ammatilliseen koulutukseen ja
                        lukiokoulutukseen 27.2.-16.3.
                    </li>
                    <li><span class="uppercase">Yhteishaku</span> ammattikorkeakouluihin ja yliopistoihin 5.3.-3.4.</li>
                    <li><span class="uppercase">Lorem upsum</span> dolor sit amet</li>
                    <li><span class="uppercase">Lorem upsum</span> dolor sit amet</li>
                </ul>

            </div>

            <div class="module">
                <h4>Päivän kuva</h4>
                <figure class="image" style="">
                    <img src="content/Tervetuloa.jpg" alt=""/>
                    <figcaption>Lorem ipsum dolor sit amet</figcaption>
                </figure>

                <div class="clear"></div>
            </div>

        </div>

        <div class="grid16-4">
            <div class="module bottom-border">
                <h3>Blogikirjoitus</h3>
                <figure class="image set-left" style="width:25%;">
                    <img src="content/IMG_0187.jpg" alt=""/>
                </figure>
                Taina Tainanen pohtii Tulevaisuusseminaarissa esitettyä näkemystä korkeakoulujen yhteistyömallista...
                <br/>
                <a href="#">Lue lisää</a>

                <div class="clear"></div>
            </div>

            <div class="module bottom-border">
                <h4>Suositeltu linkki</h4>
                Lorem ipsum dolor sit amet, consectetur adipiscing elit
            </div>

            <div class="module bottom-border">
                <h4>Suositeltu linkki</h4>
                Lorem ipsum dolor sit amet, consectetur adipiscing elit
            </div>

            <div class="module bottom-border">

                <h3>Vastaa kyselyyn</h3>

                <form id="poll">
                    <fieldset class="form-item">
                        <legend class="form-item-label">Onko opiskelu kivaa?</legend>
                        <div class="form-item-content">

                            <div class="field-container-radio">
                                <input type="radio" name="poll" value="1" id="poll-option-1"/>
                                <label for="poll-option-1">Samaa mieltä</label>
                            </div>

                            <div class="field-container-radio">
                                <input type="radio" name="poll" value="2" id="poll-option-2"/>
                                <label for="poll-option-2">Erittäin paljon samaa mieltä!</label>
                            </div>

                        </div>
                        <div class="clear"></div>
                    </fieldset>


                </form>
                <div class="clear"></div>
            </div>

            <div class="module">
                Banneri
            </div>
        </div>

        <div class="clear"></div>
    </section>

</section>
<jsp:include page="top/footer.jsp" />

</div>
</div>
</body>
</html>