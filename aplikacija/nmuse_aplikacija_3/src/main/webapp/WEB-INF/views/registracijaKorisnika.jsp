<%-- 
    Document   : registracijaKorisnika
    Created on : May 3, 2021, 4:22:25 PM
    Author     : NWTiS_4
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Registracija</title>
    </head>
    <body>
        <h1>Registracija</h1>
        <form method="POST" action="${pageContext.servletContext.contextPath}/mvc/registracijaKorisnika">
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
                    <td>Ponovljena lozinka:</td>
                    <td><input type="password" name="lozinka2"/></td>
                </tr>
                <tr>
                    <td>Ime</td>
                    <td><input type="text" name="ime"/></td>
                </tr>
                <tr>
                    <td>Prezime</td>
                    <td><input type="text" name="prezime"/></td>
                </tr>
                <tr>
                    <td>Email</td>
                    <td><input type="email" name="email"/></td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td><input type="submit" name="Registriraj me"/></td>
                </tr>
            </table>
        </form>
    </body>
</html>
