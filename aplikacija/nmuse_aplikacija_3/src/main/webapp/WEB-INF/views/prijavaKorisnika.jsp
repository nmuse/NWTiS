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
        <title>Prijava</title>
    </head>
    <body>
        <h1>Prijava</h1>
        <form method="POST" action="${pageContext.servletContext.contextPath}/mvc/prijavaKorisnika">
            <table>
                <tr>
                    <td>Korisničko ime:</td>
                    <td><input type="text" name="korisnik"/></td>
                </tr>
                <tr>
                    <td>Lozinka:</td>
                    <td><input type="password" name="lozinka"/></td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td><input type="submit" value="Prijavi me"/></td>
                </tr>
            </table>
        </form>
    </body>
</html>
