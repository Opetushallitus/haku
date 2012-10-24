<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div id="authentication">
    <div class="heading">
        <h2>Kirjautuminen</h2>
    </div>
    <div class="login-content">
        <form action="/haku/j_spring_security_check" method="POST">
            
            <legend class="h3">KÄYTTÄJÄTUNNUS</legend>
            <input name="j_username" type="text"/>
        
            <legend class="h3">SALASANA</legend>
            <input name="j_password" type="password"/>
            <div>
                <input name="login" value="Kirjaudu" type="submit"/>
            </div>
        </form>
        
        <a href="#">Unohtuiko salasana?</a>

        <div class="clear"></div>
        <a href="#">Rekisteröidy palveluun</a>

        <div class="clear"></div>
        <a href="#" class="close-login-popup">Sulje</a>
        <a href="#" class="helplink">?</a>
    </div>
</div>