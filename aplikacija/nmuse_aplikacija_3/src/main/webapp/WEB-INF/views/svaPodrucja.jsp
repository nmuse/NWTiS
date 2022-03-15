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
                $("#podrucja").DataTable();
            });</script>
        <title>Pregled područja</title>
    </head>
    <body>
        <h1>Pregled područja</h1>
        <table class="display" id="podrucja">
            <thead>
                <tr>
                    <th>Područje</th>
                    <th>Dodaj područje</th>
                    <th>Ukloni područje</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="p" items="${svaPodrucja}">
                    <tr>
                        <td>${p}</td>
                        <td>
                            <form method="POST" action="${pageContext.servletContext.contextPath}/mvc/dodajPodrucje">
                                <input type="hidden" name="korisnikTrazi" value="${korisnikTrazi}">
                                <input type="hidden" name="podrucje" value="${p}">
                                <input type="submit" value="Dodaj">
                            </form>
                        </td>
                        <td>
                            <form method="POST" action="${pageContext.servletContext.contextPath}/mvc/ukloniPodrucje">
                                <input type="hidden" name="korisnikTrazi" value="${korisnikTrazi}">
                                <input type="hidden" name="podrucje" value="${p}">
                                <input type="submit" value="Ukloni">
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </body>
</html>
