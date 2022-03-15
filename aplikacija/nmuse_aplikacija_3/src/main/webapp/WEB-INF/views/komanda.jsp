<%-- 
    Document   : prijavaKorisnika
    Created on : May 3, 2021, 4:22:12 PM
    Author     : NWTiS_4
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Slanje komande</title>
    </head>
    <body>
        <h1>Slanje komande</h1>
        <form method="POST" action="${pageContext.servletContext.contextPath}/mvc/obradaKomande">
            <table>
                <tr>
                    <td>Upišite komandu:</td>
                    <td><input size="50" type="text" name="komanda" value="${komanda}"/></td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td><input type="submit" value="Pošalji"/></td>
                </tr>
            </table>
        </form>
    </body>
</html>
