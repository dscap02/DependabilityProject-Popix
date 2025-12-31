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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/styles/style-checkout.css">
    <script src="https://kit.fontawesome.com/892069e9ac.js" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script src="${pageContext.request.contextPath}/scripts/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/scripts/checkoutValidation.js"></script>
    <script src="${pageContext.request.contextPath}/scripts/checkout.js"></script>
    <script>var contextPath = '<%= request.getContextPath() %>';</script>
    <title>Checkout</title>
</head>
<body>
<%@ include file="/resources/templates/header.jsp" %>
<%
    String email = (String) session.getAttribute("userEmail");

    if (email != null) {
        UserDAO<UserBean> userDAO = new UserDAOImpl();
        UserBean userBean = userDAO.getUserByEmail(email);

        if (!userBean.getRole().equals("User")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
    } else {
        // Aggiungi lo SweetAlert e reindirizzamento immediato
        out.println("<script>");
        out.println("Swal.fire({");
        out.println("  icon: 'error',");
        out.println("  title: 'Accesso Negato',");
        out.println("  text: 'Devi essere loggato come cliente per fare il checkout.'");
        out.println("}).then((result) => {");
        out.println("  if (result.isConfirmed) {");
        out.println("    window.location.href = '" + request.getContextPath() + "/jsp/Login.jsp';");
        out.println("  }");
        out.println("});");
        out.println("</script>");
        return;
    }
%>

<div class="container mt-5">
    <h1 class="text-center mb-4">Checkout</h1>
    <form id="checkoutForm" action="${pageContext.request.contextPath}/CheckoutServlet" method="post" onsubmit="return validateForm()">
        <div class="row">
            <!-- Sezione Info Personali -->
            <div class="col-md-6">
                <h2 class="mb-3">Informazioni Personali</h2>
                <div class="mb-3">
                    <label for="name" class="form-label">Nome</label>
                    <input type="text" class="form-control" id="name" name="name" placeholder="Inserisci il tuo nome">
                </div>
                <div class="mb-3">
                    <label for="surname" class="form-label">Cognome</label>
                    <input type="text" class="form-control" id="surname" name="surname" placeholder="Inserisci il tuo cognome">
                </div>
                <div class="mb-3">
                    <label for="country" class="form-label">Paese</label>
                    <input type="text" class="form-control" id="country" name="country" placeholder="Inserisci il tuo paese">
                </div>
                <div class="mb-3">
                    <label for="city" class="form-label">Città</label>
                    <input type="text" class="form-control" id="city" name="city" placeholder="Inserisci la tua città">
                </div>
                <div class="mb-3">
                    <label for="address" class="form-label">Indirizzo</label>
                    <input type="text" class="form-control" id="address" name="address" placeholder="Inserisci il tuo indirizzo">
                </div>
                <div class="mb-3">
                    <label for="cap" class="form-label">CAP</label>
                    <input type="text" id="cap" name="cap" placeholder="Inserisci il CAP (5 cifre numeriche)">
                </div>
                <div class="mb-3">
                    <label for="birthday_date" class="form-label">Data di Nascita</label>
                    <input type="date" class="form-control" id="birthday_date" name="birthday_date">
                </div>
                <div class="mb-3">
                    <label for="cellulare" class="form-label">Cellulare</label>
                    <input type="text" id="cellulare" name="cellulare" placeholder="Inserisci il tuo numero di cellulare (10 cifre numeriche)">
                </div>
                <div class="mb-3">
                    <label for="cliente_email" class="form-label">Email</label>
                    <input type="email" class="form-control" id="cliente_email" name="cliente_email" value="<%= email %>" readonly>
                </div>
            </div>

            <!-- Sezione Metodo di Pagamento -->
            <div class="col-md-6">
                <h2 class="mb-3">Metodo di Pagamento</h2>
                <div class="mb-3">
                    <label for="card_number" class="form-label">Numero Carta</label>
                    <input type="text" class="form-control" id="card_number" name="card_number" maxlength="16" placeholder="Inserisci il numero della carta (13-16 cifre)">
                </div>
                <div class="mb-3">
                    <label for="cvc" class="form-label">CVC</label>
                    <input type="text" id="cvc" name="cvc" maxlength="4" placeholder="Inserisci il CVC (3-4 cifre numeriche)">
                </div>
                <div class="mb-3">
                    <label for="owner" class="form-label">Titolare Carta</label>
                    <input type="text" class="form-control" id="owner" name="owner" placeholder="Inserisci il nome del titolare della carta">
                </div>
                <div class="mb-3">
                    <label for="expiration" class="form-label">Data di Scadenza</label>
                    <input type="month" class="form-control" id="expiration" name="expiration" placeholder="Inserisci la data di scadenza">
                </div>
            </div>
        </div>
        <div class="text-center mt-4">
            <button type="submit" class="btn btn-primary btn-lg">Conferma Ordine</button>
        </div>
    </form>
</div>

<%@ include file="/resources/templates/footer.jsp" %>

</body>
</html>
