<%@ page import="com.popx.modello.ProdottoBean" %>
<%@ page import="com.popx.persistenza.ProdottoDAO" %>
<%@ page import="com.popx.persistenza.ProdottoDAOImpl" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/images/logo-noborderico.png">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/styles/homepage.css">
    <script src="https://kit.fontawesome.com/892069e9ac.js" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script>  var contextPath = '<%= request.getContextPath() %>'; </script>
    <script src="${pageContext.request.contextPath}/scripts/addCartHomePage.js"></script>
    <title>Pop!x</title>
</head>
<body>
<%@include file="/resources/templates/header.jsp"%>
<section class="home" id="home">
    <div class="content">
        <h3>Pop!X</h3>
        <span>Scopri tutte le esclusive</span>
        <p>Potrai trovare tutti i personaggi collezionabili<br>più amati del momento a prezzi incredibili!<br>Innamorati del tuo prossimo regalo!
        </p>
        <a href="#" class="btn">Shop now</a>
    </div>
</section>
<!-- Home end -->

<!-- About start -->
<section class="about" id="about">
    <h1 class="heading"><span>About</span> us</h1>
    <div class="row">
        <div class="photo-container">
            <img src="${pageContext.request.contextPath}/resources/images/us.png" alt="Image description">
            <div class="text-overlay">
            </div>
        </div>
        <div class="content">
            <h3>Perchè sceglierci?</h3>
            <p>Ci impegniamo a fornire solo Funko Pop originali e di alta qualità,
                garantendo che ogni collezione sia autentica e ben conservata.
                La soddisfazione dei clienti è al centro di tutto ciò che facciamo. Siamo qui per assisterti in ogni fase, dall'acquisto alla consegna, per garantire
                un'esperienza di shopping serena.
                Scegli il nostro negozio per la tua prossima aggiunta alla tua collezione Funko Pop e scopri
                perché siamo la destinazione preferita per gli appassionati di tutto il mondo.</p>
        </div>

    </div>
</section>

<section class="products" id="products">
    <h1 class="heading">La nostra<span> selezione</span></h1>
    <div class="box-container">
        <%
            // Recupera i 3 prodotti casuali dal database
            ProdottoDAO prodottoDAO = new ProdottoDAOImpl();
            List<ProdottoBean> prodotti = prodottoDAO.getRandomProducts(3);
            for (int i = 0; i < prodotti.size(); i++) {
                ProdottoBean prodotto = prodotti.get(i);
        %>
        <!-- Product <%= i+1 %> -->
        <div class="box product<%= i+1 %>">
            <div class="image">
                <a href="${pageContext.request.contextPath}/getProduct?id=<%= prodotto.getId() %>"><img src="<%= request.getContextPath() + "/getPictureServlet?id=" + prodotto.getId() %>" alt="Product Image">
                </a>
            </div>
            <div class="content">
                <h3><%= prodotto.getName() %></h3>
                <div class="price">€<%= prodotto.getCost() %></div>
            </div>
        </div>
        <%
            }
        %>
    </div>
</section>

<%@include file="/resources/templates/footer.jsp"%>

</body>
</html>
