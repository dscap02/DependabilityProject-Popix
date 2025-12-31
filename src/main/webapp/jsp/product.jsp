<%@ page import="com.popx.modello.ProdottoBean" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/images/logo-noborderico.png">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/styles/prod.css">
    <script src="https://kit.fontawesome.com/892069e9ac.js" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script>  var contextPath = '<%= request.getContextPath() %>'; </script>
    <script src="${pageContext.request.contextPath}/scripts/addCart.js"></script>
    <title>Popix - <%= ((ProdottoBean) request.getAttribute("prod")).getName() %></title>
</head>
<body>

<%@include file="/resources/templates/header.jsp"%>
<%
    ProdottoBean productBean = (ProdottoBean) request.getAttribute("prod");
    if(productBean == null){
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
%>
<main>
    <section class="product-container">
        <div class="product-image">
            <img src="<%= request.getContextPath() %>/getPictureServlet?id=<%= productBean.getId() %>" alt="Product image">
        </div>
        <div class="product-details">
            <h1 class="product-name"><%= productBean.getName() %></h1>
            <h2 class="product-brand"><%= productBean.getBrand() %></h2>
            <p class="product-price">€<%= productBean.getCost() %></p>
            <div class="quantity-selector">
                <label for="quantity" class="form-label">Quantity:</label>
                <input type="number" id="quantity" class="form-control" value="1" min="1" max="<%= productBean.getPiecesInStock() %>">
            </div>
            <div>
                <p><%= productBean.getDescription()%></p>
            </div>
            <button class="add-to-cart" id="addToCartButton" data-product-id="<%= productBean.getId() %>" >Add to Cart</button>
        </div>
    </section>
</main>
<%@include file="/resources/templates/footer.jsp"%>

</body>
</html>

