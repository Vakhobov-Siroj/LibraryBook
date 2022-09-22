<%@ page import="uz.jl.library.service.UserService" %>
<%@ page import="java.util.Objects" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet"
      integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">


<html>
<head>
    <title>Library</title>


    <meta name="viewport" content="width=device-width, initial-scale=1">


    <%@include file="../util.jsp" %>
    <%@include file="../links.jsp" %>

</head>
<body>
<%@include file="../Menu.jsp" %>


<div>

    <%
        String ErrorMassage = null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("ErrorMassageSetting")) {
                ErrorMassage = cookie.getValue();
            }
        }

        if (ErrorMassage != null) { %>


    <h4 style="color:red;">Bro password don't change setting something went wrong please try again</h4>
    <% }%>
</div>

<div id="carouselExampleControls" class="carousel slide" data-bs-ride="carousel">

    <div class="carousel-inner">
        <div class="carousel-item active">
            <img src="img?filepath=${img2}"  width="80%" height="100%" class="d-block w-100" alt="...">
        </div>
<%--        <div class="carousel-item">--%>
<%--            <img src="img?filepath=${img2}" width="100%" height="100%" class="d-block w-100" alt="...">--%>
<%--        </div>--%>
<%--        <div class="carousel-item">--%>
<%--            <img src="img?filepath=${img2}" width="100%" height="100%" class="d-block w-100"--%>
<%--                 alt="...">--%>
<%--        </div>--%>
    </div>
    <button class="carousel-control-prev" type="button" data-bs-target="#carouselExampleControls" data-bs-slide="prev">
        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
        <span class="visually-hidden">Previous</span>
    </button>
    <button class="carousel-control-next" type="button" data-bs-target="#carouselExampleControls" data-bs-slide="next">
        <span class="carousel-control-next-icon" aria-hidden="true"></span>
        <span class="visually-hidden">Next</span>
    </button>
</div>

</body>
</html>
