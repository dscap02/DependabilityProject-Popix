<%@ page import="java.util.List" %>
<%@ page import="com.popx.modello.ProdottoBean" %>
<%@ page import="com.popx.modello.UserBean" %>
<%@ page import="com.popx.persistenza.UserDAO" %>
<%@ page import="com.popx.persistenza.UserDAOImpl" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/images/logo-noborderico.png">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/styles/style-admin.css">
    <script>var contextPath = '<%= request.getContextPath() %>'</script>
    <script src="https://kit.fontawesome.com/892069e9ac.js" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script src="${pageContext.request.contextPath}/scripts/removeProduct.js"></script>
    <title>Popix - Admin Dashboard</title>
</head>
<body>

<%@include file="/resources/templates/header.jsp" %>

<%
    // Verifica dell'utente loggato e controllo del ruolo
    String email = (String) session.getAttribute("userEmail");

    if (email != null) {
        UserDAO<UserBean> userDAO = new UserDAOImpl();
        UserBean userBean = userDAO.getUserByEmail(email);

        if (!userBean.getRole().equals("Admin")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
    } else {
        response.sendRedirect(request.getContextPath() + "/Login.jsp");
        return;
    }

    // Recupera i prodotti passati come attributo dalla Servlet
    List<ProdottoBean> prodottoBeans = (List<ProdottoBean>) request.getAttribute("products");

    if (prodottoBeans == null || prodottoBeans.isEmpty()) {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore durante il recupero dei prodotti.");
        return;
    }

    // Configurazione paginazione
    int totalPages = (int) request.getAttribute("totalPages");
    int currentPage = (int) request.getAttribute("currentPage");



%>

<div class="container">
    <h1 class="my-4">Dashboard Admin</h1>

    <table class="table table-striped">
        <thead>
        <tr>
            <th>ID</th>
            <th>Nome Prodotto</th>
            <th>Prezzo (€)</th>
            <th>Quantità</th>
            <th>Azioni</th>
        </tr>
        </thead>
        <tbody>
        <%
            for (ProdottoBean product : prodottoBeans) {
        %>
        <tr>
            <td><%= product.getId() %></td>
            <td><%= product.getName() %></td>
            <td><%= product.getCost() %></td>
            <td><%= product.getPiecesInStock() %></td>
            <td>
                <div class="btn-group">

                    <a href="${pageContext.request.contextPath}/jsp/ModifyProduct.jsp?id=<%= product.getId() %>">
                        <button>Modifica</button>
                    </a>

                    <button id="deleteProductBtn" data-id="<%= product.getId() %>">Elimina</button>
                </div>
            </td>
        </tr>
        <%
            }
        %>
        </tbody>
    </table>

    <a href="${pageContext.request.contextPath}/jsp/AddNewProduct.jsp"><button class="btn-add-product" id="addProdBtn">Aggiungi Prodotto</button></a>

    <!-- Navigazione pagine -->
    <nav aria-label="Page navigation example" class="mt-4">
        <ul class="pagination justify-content-center">
            <li class="page-item <%= currentPage == 1 ? "disabled" : "" %>">
                <a class="page-link" href="?page=<%= currentPage - 1 %>">Precedente</a>
            </li>
            <%
                for (int i = 1; i <= totalPages; i++) {
            %>
            <li class="page-item <%= currentPage == i ? "active" : "" %>">
                <a class="page-link" href="?page=<%= i %>"><%= i %></a>
            </li>
            <%
                }
            %>
            <li class="page-item <%= currentPage == totalPages ? "disabled" : "" %>">
                <a class="page-link" href="?page=<%= currentPage + 1 %>">Successivo</a>
            </li>
        </ul>
    </nav>
</div>

<%@include file="/resources/templates/footer.jsp" %>

</body>
</html>
