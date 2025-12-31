<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.popx.modello.ProdottoBean" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.commons.text.StringEscapeUtils" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/images/logo-noborderico.png">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css"
          crossorigin="anonymous">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/styles/style-prods.css">

    <script src="https://kit.fontawesome.com/892069e9ac.js" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

    <script>
        var contextPath = '<%= request.getContextPath() %>';
    </script>

    <title>Catalogo - Popix</title>

    <!-- spostato qui per conformità: tutti gli script devono essere dichiarati nel <head> -->
    <script src="${pageContext.request.contextPath}/scripts/cartHandler.js"></script>
</head>

<body>

<%@ include file="/resources/templates/header.jsp" %>

<div class="container mt-4">
    <form method="get" action="${pageContext.request.contextPath}/getProductsServlet">
        <div class="row">
            <div class="col-md-4">
                <label for="category" class="form-label">Brand</label>
                <select name="category" id="category" class="form-select">
                    <option value=""
                            <%= request.getParameter("category") == null || request.getParameter("category").isEmpty()
                                    ? "selected" : "" %>>
                        Tutti
                    </option>
                    <option value="Naruto" <%= "Naruto".equals(request.getParameter("category")) ? "selected" : "" %>>
                        Naruto
                    </option>
                    <option value="Dragon Ball" <%= "Dragon Ball".equals(request.getParameter("category")) ? "selected" : "" %>>
                        Dragon Ball
                    </option>
                    <option value="Disney" <%= "Disney".equals(request.getParameter("category")) ? "selected" : "" %>>
                        Disney
                    </option>
                    <option value="One Piece" <%= "One Piece".equals(request.getParameter("category")) ? "selected" : "" %>>
                        One Piece
                    </option>
                    <option value="My Hero Academia"
                            <%= "My Hero Academia".equals(request.getParameter("category")) ? "selected" : "" %>>
                        My Hero Academia
                    </option>
                </select>
            </div>

            <div class="col-md-4">
                <label for="price" class="form-label">Prezzo</label>
                <select name="price" id="price" class="form-select">
                    <option value=""
                            <%= request.getParameter("price") == null || request.getParameter("price").isEmpty()
                                    ? "selected" : "" %>>
                        Tutti
                    </option>
                    <option value="low" <%= "low".equals(request.getParameter("price")) ? "selected" : "" %>>
                        Basso → Alto
                    </option>
                    <option value="high" <%= "high".equals(request.getParameter("price")) ? "selected" : "" %>>
                        Alto → Basso
                    </option>
                </select>
            </div>

            <div class="col-md-4 d-flex align-items-end">
                <button type="submit" class="btn btn-primary">Filtra</button>
            </div>
        </div>
    </form>
</div>

<%
    List<ProdottoBean> prodotti = (List<ProdottoBean>) request.getAttribute("products");
    int totalPages = (int) request.getAttribute("totalPages");
    int currentPage = (int) request.getAttribute("currentPage");

    String rawCategory = request.getParameter("category");
    String rawPrice = request.getParameter("price");

    String category = rawCategory != null ? StringEscapeUtils.escapeHtml4(rawCategory) : "";
    String price = rawPrice != null ? StringEscapeUtils.escapeHtml4(rawPrice) : "";
%>

<div class="container mt-4">
    <div class="row">
        <%
            if (prodotti != null && !prodotti.isEmpty()) {
                for (ProdottoBean prodotto : prodotti) {
                    String safeName = StringEscapeUtils.escapeHtml4(prodotto.getName());
        %>
        <div class="col-md-4">
            <div class="card mb-4 shadow-sm">
                <a href="${pageContext.request.contextPath}/getProduct?id=<%= prodotto.getId() %>">
                    <img src="${pageContext.request.contextPath}/getPictureServlet?id=<%= prodotto.getId() %>"
                         class="card-img-top"
                         alt="<%= safeName %>">
                </a>
                <div class="card-body">
                    <h5 class="card-title"><%= safeName %></h5>
                    <p class="card-text">Prezzo: €<%= prodotto.getCost() %></p>
                    <button class="btn btn-primary add-to-cart"
                            data-id="<%= prodotto.getId() %>">
                        Aggiungi al carrello
                    </button>
                </div>
            </div>
        </div>
        <%
            }
        } else {
        %>
        <div class="col-12">
            <p class="text-center">Nessun prodotto trovato.</p>
        </div>
        <%
            }
        %>
    </div>
</div>

<div class="container">
    <nav aria-label="Page navigation">
        <ul class="pagination justify-content-center">
            <li class="page-item <%= currentPage == 1 ? "disabled" : "" %>">
                <a class="page-link"
                   href="?page=<%= currentPage - 1 %>&category=<%= category %>&price=<%= price %>">
                    Precedente
                </a>
            </li>

            <%
                for (int i = 1; i <= totalPages; i++) {
            %>
            <li class="page-item <%= currentPage == i ? "active" : "" %>">
                <a class="page-link"
                   href="?page=<%= i %>&category=<%= category %>&price=<%= price %>">
                    <%= i %>
                </a>
            </li>
            <%
                }
            %>

            <li class="page-item <%= currentPage == totalPages ? "disabled" : "" %>">
                <a class="page-link"
                   href="?page=<%= currentPage + 1 %>&category=<%= category %>&price=<%= price %>">
                    Successivo
                </a>
            </li>
        </ul>
    </nav>
</div>

<%@ include file="/resources/templates/footer.jsp" %>


</body>
</html>
