<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.popx.modello.ProdottoBean" %>
<%@ page import="com.popx.persistenza.ProdottoDAOImpl" %>
<%@ page import="com.popx.modello.UserBean" %>
<%@ page import="com.popx.persistenza.UserDAOImpl" %>
<%@ page import="com.popx.persistenza.UserDAO" %>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/images/logo-noborderico.png">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/styles/style-cart.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script>var contextPath = '<%= request.getContextPath()%>'; </script>
    <script src="https://kit.fontawesome.com/892069e9ac.js" crossorigin="anonymous"></script>
    <script src="${pageContext.request.contextPath}/scripts/updateCart.js"></script>
    <script src="${pageContext.request.contextPath}/scripts/removeItem.js"></script>
    <title>Carrello</title>
</head>
<body>

<%@include file="/resources/templates/header.jsp"%>

<main>
    <div class="container">
        <section class="cart">
            <h2>Il tuo carrello</h2>
            <div class="cart-items">
                <%
                    ProdottoDAOImpl prodottoDao = new ProdottoDAOImpl();
                    List<ProdottoBean> cart = null;
                    String userEmail = (String) session.getAttribute("userEmail");

                    if (userEmail != null) {
                        UserDAO<UserBean> userDAO = new UserDAOImpl();
                        UserBean userBean = userDAO.getUserByEmail(userEmail);

                        if (!userBean.getRole().equals("User")) {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN);
                            return;
                        }
                    }
                    cart = (List<ProdottoBean>) session.getAttribute("cart");

                    if (userEmail != null && cart == null) {
                        // Recupera il carrello dal database per l'utente loggato
                        try {
                            cart = prodottoDao.getCartByUserEmail(userEmail);
                            session.setAttribute("cart", cart);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else  {
                        // Usa il carrello dalla sessione se l'utente non è loggato
                        cart = (List<ProdottoBean>) session.getAttribute("cart");
                    }

                    if (cart != null && !cart.isEmpty()) {
                        for (ProdottoBean product : cart) {
                %>
                <div class="cart-item">
                    <img src="<%= request.getContextPath() %>/getPictureServlet?id=<%= product.getId()%>" alt="Product image">
                    <div class="item-details">
                        <h3><%= product.getName() %></h3>
                        <p>Disponibilità: <%= product.getPiecesInStock() %></p>
                        <div class="quantity-control">
                            <input type="number" class="quantity-input" value="<%= product.getQty() %>"
                                    min="1" max="<%= product.getPiecesInStock() %>">

                            <button class="update-qty" data-id="<%= product.getId() %>">Aggiorna</button>
                        </div>
                        <p>Prezzo: $<%= product.getCost() %></p>
                    </div>
                    <button class="remove-item" data-id="<%= product.getId() %>"><i class="fas fa-trash-alt"></i></button>
                </div>
                <%
                    }
                } else {
                %>
                <p>Il tuo carrello è vuoto.</p>
                <%
                    }
                %>
            </div>
            <div class="cart-summary">
                <h3>Riepilogo</h3>
                <div class="summary-details">
                    <%
                        double sum = 0;
                        String formattedSum = "0.00";
                        if (cart != null) {
                            for (ProdottoBean product : cart) {
                                sum += product.getCost() * product.getQty();
                            }
                            formattedSum = String.format("%.2f", sum);
                        }
                    %>
                    <p id="sum"> Totale: <%= formattedSum %> </p>
                </div>
                <a href="Checkout.jsp"><button class="checkout-btn" id="checkout">CHECKOUT</button></a>
            </div>
        </section>
    </div>
</main>

<%@include file="/resources/templates/footer.jsp"%>
</body>
</html>
