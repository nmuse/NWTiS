<%-- 
    Document   : registracijaKorisnika
    Created on : May 3, 2021, 4:22:25 PM
    Author     : NWTiS_4
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

        <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.20/css/jquery.dataTables.css"/>
        <link rel="stylesheet" type="text/css" href="//code.jquery.com/ui/1.12.1/themes/smoothness/jquery-ui.css"/>
        <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
        <script src="//code.jquery.com/jquery-1.12.4.js"></script>
        <script src="//code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
        <script type="text/javascript" src="https://cdn.datatables.net/1.10.20/js/jquery.dataTables.js"></script>
        <script type="text/javascript" class="init">$(document).ready(function () {
                $("#aerodromi").DataTable();
            });</script>
        <title>Rad s aerodromima</title>
    </head>
    <body>
        <h1>Rad s aerodromima</h1>
        <table class="display" id="aerodromi">
            <thead>
                <tr>
                    <th>ICAO</th>
                    <th>naziv</th>
                    <th>Država</th>
                    <th>Geografska širina</th>
                    <th>Greografska dužina</th>
                    <th>Detalji</th>
                    <th>Prati aerodrom</th>
                    <th>Obriši aerodrom</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="a" items="${aerodromi}">
                    <tr>
                        <td>${a.icao}</td>
                        <td>${a.naziv}</td>
                        <td>${a.drzava}</td>
                        <td>${a.lokacija.latitude}</td>
                        <td>${a.lokacija.longitude}</td>
                        <td>
                            <form method="POST" action="${pageContext.servletContext.contextPath}/mvc/prateAerodrom">
                                <input type="hidden" name="icao" value="${a.icao}">
                                <input type="submit" value="Pregledaj">
                            </form>
                        </td>
                        <td>
                            <form method="POST" action="${pageContext.servletContext.contextPath}/mvc/dodajAerodrom">
                                <input type="hidden" name="icao" value="${a.icao}">
                                <input type="submit" value="Dodaj">
                            </form>
                        </td>
                        <td>
                            <form method="POST" action="${pageContext.servletContext.contextPath}/mvc/ukloniAerodrom">
                                <input type="hidden" name="icao" value="${a.icao}">
                                <input type="submit" value="Ukloni">
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </body>
</html>
