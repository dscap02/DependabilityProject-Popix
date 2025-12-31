<%@ page import="com.popx.persistenza.ProdottoDAO" %>
<%@ page import="com.popx.modello.ProdottoBean" %>
<%@ page import="com.popx.persistenza.ProdottoDAOImpl" %>
<%@ page import="org.apache.commons.text.StringEscapeUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/images/logo-noborderico.png">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/styles/style-add.css">
    <script src="https://kit.fontawesome.com/892069e9ac.js" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script>var contextPath = '<%=request.getContextPath()%>';</script>
    <script src="${pageContext.request.contextPath}/scripts/modifyProd.js"></script>
    <title>Admin - Modifica Prodotto</title>
</head>
<body>

<%@include file="/resources/templates/header.jsp" %>

<%
    String email = (String) session.getAttribute("userEmail");

    ProdottoBean prodottoBean;
    if (email != null) {
        ProdottoDAO prodottoDAO = new ProdottoDAOImpl();
        prodottoBean = prodottoDAO.getProdottoById(request.getParameter("id"));

        if (prodottoBean == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Prodotto non trovato.");
            return;
        }
    } else {
        response.sendRedirect(request.getContextPath() + "/jsp/Login.jsp");
        return;
    }

    // Calcola una sola volta le variabili HTML-escaped per output
    String safeName = prodottoBean.getName() != null ? StringEscapeUtils.escapeHtml4(prodottoBean.getName()) : "";
    String safeId = prodottoBean.getId() != null ? StringEscapeUtils.escapeHtml4(String.valueOf(prodottoBean.getId())) : "";
    // getCost è double (primitivo) -> usare String.valueOf senza controllo su null
    String safeCost = StringEscapeUtils.escapeHtml4(String.valueOf(prodottoBean.getCost()));
    String safeBrand = prodottoBean.getBrand() != null ? StringEscapeUtils.escapeHtml4(prodottoBean.getBrand()) : "";
    String safeFigure = prodottoBean.getFigure() != null ? StringEscapeUtils.escapeHtml4(prodottoBean.getFigure()) : "";
    // getPiecesInStock è int (primitivo) -> usare String.valueOf senza controllo su null
    String safeQty = StringEscapeUtils.escapeHtml4(String.valueOf(prodottoBean.getPiecesInStock()));
    String safeDescription = prodottoBean.getDescription() != null ? StringEscapeUtils.escapeHtml4(prodottoBean.getDescription()) : "";
    String safeCurrentImgSrc = (prodottoBean.getImg() != null && prodottoBean.getId() != null) ? StringEscapeUtils.escapeHtml4(String.valueOf(prodottoBean.getId())) : "";
    String rawImageUrl = request.getContextPath() + "/getPictureServlet?id=" + (prodottoBean.getId() != null ? String.valueOf(prodottoBean.getId()) : "");
    String safeImageUrl = StringEscapeUtils.escapeHtml4(rawImageUrl);
%>

<h2>Modifica Prodotto</h2>

<form id="productForm" class="form-horizontal" enctype="multipart/form-data" accept-charset="UTF-8" method="post" action="${pageContext.request.contextPath}/updateProductServlet">
    <div class="form-row">
        <div class="form-group">
            <label for="name">Nome</label>
            <input type="text" id="name" name="name" value="<%= safeName %>" required>
        </div>
        <div class="form-group">
            <label for="idProduct">ID</label>
            <input type="text" id="idProduct" name="idProduct" value="<%= safeId %>" required readonly>
        </div>
        <div class="form-group">
            <label for="price">Prezzo</label>
            <input type="number" id="price" name="price" step="0.01" value="<%= safeCost %>" required>
        </div>
    </div>
    <div class="form-row">
        <div class="form-group">
            <label for="brand">Brand</label>
            <input type="text" id="brand" name="brand" value="<%= safeBrand %>" required>
        </div>
        <div class="form-group">
            <label for="figure">Personaggio</label>
            <input type="text" id="figure" name="figure" value="<%= safeFigure %>" required>
        </div>
    </div>
    <div class="form-row">
        <div class="form-group">
            <label for="qty">Quantità</label>
            <input type="number" id="qty" name="qty" value="<%= safeQty %>" required>
        </div>
        <div class="form-group">
            <h4>Immagine Attuale</h4>
            <img src="<%= safeImageUrl %>" alt="Product Image" id="productImage" width="150">
            <input type="hidden" id="current_img_src" name="current_img_src" value="<%= safeCurrentImgSrc %>">
            <br><label for="img_src">Carica Immagine</label>
            <input type="file" id="img_src" name="img_src" accept="image/*">
        </div>
    </div>
    <div class="form-row">
        <div class="form-group full-width">
            <label for="description">Descrizione</label>
            <textarea id="description" name="description" rows="4" required><%= safeDescription %></textarea>
        </div>
    </div>
    <div class="form-row">
        <button type="submit">Salva Modifiche</button>
    </div>
</form>

<%@include file="/resources/templates/footer.jsp" %>

</body>
</html>
