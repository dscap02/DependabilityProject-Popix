<%@ page import="java.util.List" %>
<%@ page import="com.popx.modello.OrdineBean" %>
<%@ page import="com.popx.modello.RigaOrdineBean" %>
<%@ page import="com.popx.persistenza.*" %>
<%@ page import="com.popx.modello.UserBean" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/images/logo-noborderico.png">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/styles/dash-gest.css">
    <script>var contextPath = '<%= request.getContextPath() %>'</script>
    <script src="https://kit.fontawesome.com/892069e9ac.js" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

    <!-- Spostato qui per conformità: script inline necessario per updateStatus -->
    <script>
        function updateStatus(orderId) {
            var newStatus = document.getElementById("statusSelect_" + orderId).value;
            var url = contextPath + "/UpdateOrderStatus?id=" + orderId + "&status=" + newStatus;
            fetch(url, { method: 'GET' })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        Swal.fire('Successo', 'Status aggiornato!', 'success');
                    } else {
                        Swal.fire('Errore', 'Errore nell\'aggiornamento dello status.', 'error');
                    }
                });
        }
    </script>

    <title>Popix - Gestore Dashboard</title>
</head>
<body>

<%@include file="/resources/templates/header.jsp" %>

<%
    // Inizializzazione variabili e DAO
    OrdineDAO ordineDAO = new OrdineDAOImpl();
    RigaOrdineDAO rigaOrdineDAO = new RigaOrdineDAOImpl();
    ProdottoDAO prodottoDAO = new ProdottoDAOImpl();
    UserDAO<UserBean> userDAO = new UserDAOImpl();

    String email = (String) session.getAttribute("userEmail");
    if (email == null) {
        response.sendRedirect(request.getContextPath() + "/Login.jsp");
        return;
    }

    UserBean userBean = userDAO.getUserByEmail(email);
    if (!userBean.getRole().equals("Gestore")) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
    }

    // Recupero della pagina corrente dai parametri di richiesta
    int currentPage = 1; // Default alla prima pagina
    if (request.getParameter("page") != null) {
        currentPage = Integer.parseInt(request.getParameter("page"));
    }

    // Definizione dei parametri di paginazione
    int recordsPerPage = 10; // Numero di ordini per pagina
    int totalRecords = ordineDAO.countTuttiOrdini(); // Conteggio totale degli ordini
    int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

    // Recupero degli ordini paginati
    List<OrdineBean> ordini = ordineDAO.getOrdiniPaginati(currentPage, recordsPerPage);
%>

<div class="container mt-5">
    <h1 class="heading"><span>Dashboard</span> Gestore</h1>

    <table class="table table-striped shadow-sm">
        <thead class="table-dark">
        <tr>
            <th>Cliente</th>
            <th>ID Ordine</th>
            <th>Subtotal (€)</th>
            <th>Status</th>
            <th>Azioni</th>
        </tr>
        </thead>
        <tbody>
        <%
            for (OrdineBean ordine : ordini) {
                List<RigaOrdineBean> righeOrdine = rigaOrdineDAO.getRigheOrdineByOrdineId(ordine.getId());
                double subtotal = 0;
                for (RigaOrdineBean riga : righeOrdine) {
                    subtotal += riga.getUnitaryCost() * riga.getQuantity();
                }
        %>
        <tr>
            <td><%= ordine.getCustomerEmail() %></td>
            <td><%= ordine.getId() %></td>
            <td><%= String.format("%.2f", subtotal) %></td>
            <td>
                <select id="statusSelect_<%= ordine.getId() %>" class="form-control">
                    <option value="In elaborazione" <%= ordine.getStatus().equals("In elaborazione") ? "selected" : "" %>>In elaborazione</option>
                    <option value="Spedito" <%= ordine.getStatus().equals("Spedito") ? "selected" : "" %>>Spedito</option>
                    <option value="In consegna" <%= ordine.getStatus().equals("In consegna") ? "selected" : "" %>>In consegna</option>
                    <option value="Consegnato" <%= ordine.getStatus().equals("Consegnato") ? "selected" : "" %>>Consegnato</option>
                </select>
            </td>
            <td>
                <button class="btn btn-primary" onclick="updateStatus(<%= ordine.getId() %>)">Conferma</button>
            </td>
        </tr>
        <%
            }
        %>
        </tbody>
    </table>

    <!-- Navigazione pagine -->
    <nav aria-label="Navigazione pagine" class="mt-4">
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
