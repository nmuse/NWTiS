<%-- 
    Document   : index
    Created on : May 3, 2021, 4:13:07 PM
    Author     : NWTiS_4
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Nikola Muše - Projekt</title>
    </head>
    <body>
        <h1>Nikola Muše - Projekt</h1>
        <ul>
            <li>
                <a href="${pageContext.servletContext.contextPath}/mvc/korisnik/registracijaKorisnika">Registracija korisnika</a>
            </li>
            <li>
                <a href="${pageContext.servletContext.contextPath}/mvc/korisnik/prijavaKorisnika">Prijava korisnika</a>
            </li>
            <li>
                <a href="${pageContext.servletContext.contextPath}/mvc/korisnik/radSpodrucjima">Rad s područjima</a>
            </li>
            <li>
                <a href="${pageContext.servletContext.contextPath}/mvc/korisnik/aerodromi">Rad s aerodromima</a>
            </li>
            <li>
                <a href="${pageContext.servletContext.contextPath}/mvc/korisnik/komanda">Slanje slobodno upisane komande</a>
            </li>
        </ul>
    </body>
</html>
