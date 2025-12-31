<%@ page import="java.util.List" %>
<%@ page import="com.popx.modello.ProdottoBean" %>
<%@ page import="com.popx.modello.UserBean" %>
<%@ page import="com.popx.modello.OrdineBean" %>
<%@ page import="com.popx.modello.RigaOrdineBean" %>
<%@ page import="com.popx.persistenza.*" %>
<%@ page import="org.apache.commons.text.StringEscapeUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/images/logo-noborderico.png">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/styles/dash-user.css">
    <script>var contextPath = '<%= request.getContextPath() %>'</script>
    <script src="https://kit.fontawesome.com/892069e9ac.js" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <title>Popix - User Dashboard</title>
</head>
<body>

<%@include file="/resources/templates/header.jsp" %>

<%
    // Inizializzazione variabili e DAO
    UserDAO<UserBean> userDAO = new UserDAOImpl();
    OrdineDAO ordineDAO = new OrdineDAOImpl();
    RigaOrdineDAO rigaOrdineDAO = new RigaOrdineDAOImpl();

    // Verifica dell'utente loggato e controllo del ruolo
    String email = (String) session.getAttribute("userEmail");
    if (email == null) {
        response.sendRedirect(request.getContextPath() + "/Login.jsp");
        return;
    }

    UserBean userBean = userDAO.getUserByEmail(email);
    if (!userBean.getRole().equals("User")) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
    }

    // Recupero della pagina corrente dai parametri di richiesta
    int currentPage = 1; // Default alla prima pagina
    if (request.getParameter("page") != null) {
        try {
            currentPage = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException e) {
            currentPage = 1;
        }
    }

    // Definizione dei parametri di paginazione
    int recordsPerPage = 5; // Numero di ordini per pagina
    int totalRecords = ordineDAO.countOrdiniByCliente(email); // Conteggio totale degli ordini
    int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

    ProdottoDAO prodottoDAO = new ProdottoDAOImpl();
    ProdottoBean prodottoBean = null;

    // Variabili safe per paginazione (calcolate una sola volta)
    String safeCurrentPage = StringEscapeUtils.escapeHtml4(String.valueOf(currentPage));
    String safePrevPage = StringEscapeUtils.escapeHtml4(String.valueOf(Math.max(1, currentPage - 1)));
    String safeNextPage = StringEscapeUtils.escapeHtml4(String.valueOf(Math.min(totalPages, currentPage + 1)));

    // Recupero degli ordini paginati per l'utente
    List<OrdineBean> ordini = ordineDAO.getOrdiniByClientePaginati(email, currentPage, recordsPerPage);
%>

<div class="container mt-5">
    <h1 class="heading"><span>Dashboard</span> Utente</h1>

    <table class="table table-striped shadow-sm">
        <thead class="table-dark">
        <tr>
            <th>ID Ordine</th>
            <th>Prodotto</th>
            <th>Costo Unitario (€)</th>
            <th>Quantità</th>
            <th>Data</th>
        </tr>
        </thead>
        <tbody>
        <%
            for (OrdineBean ordine : ordini) {
                List<RigaOrdineBean> righeOrdine = rigaOrdineDAO.getRigheOrdineByOrdineId(ordine.getId());
                for (RigaOrdineBean riga : righeOrdine) {
                    prodottoBean = prodottoDAO.getProdottoById(riga.getProdottoId());

                    // Calcolo variabili "safe" per ogni riga (una sola volta per riga)
                    String safeOrdineId = StringEscapeUtils.escapeHtml4(String.valueOf(ordine.getId()));
                    String safeProductName = prodottoBean.getName() != null ? StringEscapeUtils.escapeHtml4(prodottoBean.getName()) : "";
                    String safeUnitaryCost = StringEscapeUtils.escapeHtml4(String.valueOf(riga.getUnitaryCost()));
                    String safeQuantity = StringEscapeUtils.escapeHtml4(String.valueOf(riga.getQuantity()));
                    String safeOrderDate = ordine.getDataOrdine() != null ? StringEscapeUtils.escapeHtml4(String.valueOf(ordine.getDataOrdine())) : "";
        %>
        <tr>
            <td><%= safeOrdineId %></td>
            <td><%= safeProductName %></td> <!-- Nome prodotto escappato -->
            <td><%= safeUnitaryCost %></td>
            <td><%= safeQuantity %></td>
            <td><%= safeOrderDate %></td>
        </tr>
        <%
                }
            }
        %>
        </tbody>
    </table>

    <!-- Navigazione pagine -->
    <nav aria-label="Navigazione pagine" class="mt-4">
        <ul class="pagination justify-content-center">
            <li class="page-item <%= currentPage == 1 ? "disabled" : "" %>">
                <a class="page-link" href="?page=<%= safePrevPage %>">Precedente</a>
            </li>
            <%
                for (int i = 1; i <= totalPages; i++) {
                    String safeI = StringEscapeUtils.escapeHtml4(String.valueOf(i));
            %>
            <li class="page-item <%= currentPage == i ? "active" : "" %>">
                <a class="page-link" href="?page=<%= safeI %>"><%= safeI %></a>
            </li>
            <%
                }
            %>
            <li class="page-item <%= currentPage == totalPages ? "disabled" : "" %>">
                <a class="page-link" href="?page=<%= safeNextPage %>">Successivo</a>
            </li>
        </ul>
    </nav>
</div>

<%@include file="/resources/templates/footer.jsp" %>

</body>
</html>
