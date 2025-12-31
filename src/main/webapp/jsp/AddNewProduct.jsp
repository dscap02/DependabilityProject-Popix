<%@ page import="com.popx.persistenza.UserDAO" %>
<%@ page import="com.popx.modello.UserBean" %>
<%@ page import="com.popx.persistenza.UserDAOImpl" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Admin – Aggiungi Prodotto</title>

    <link rel="icon" type="image/x-icon"
          href="${pageContext.request.contextPath}/resources/images/logo-noborderico.png">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css"
          integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65"
          crossorigin="anonymous">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/styles/style-add.css">

    <script src="https://kit.fontawesome.com/892069e9ac.js"
            crossorigin="anonymous"></script>

    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

    <script>
        var contextPath = '<%= request.getContextPath() %>';
    </script>

    <script src="${pageContext.request.contextPath}/scripts/addProd.js"></script>
</head>
<body>

<%@ include file="/resources/templates/header.jsp" %>

<%
    String email = (String) session.getAttribute("userEmail");

    if (email != null) {
        UserDAO<UserBean> userDAO = new UserDAOImpl();
        UserBean userBean = userDAO.getUserByEmail(email);

        if (!"Admin".equals(userBean.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
    }
%>

<h2>Aggiungi un prodotto</h2>

<form id="productForm"
      class="form-horizontal"
      enctype="multipart/form-data"
      accept-charset="UTF-8"
      method="post"
      action="${pageContext.request.contextPath}/addProductServlet">

    <!-- form invariato -->
</form>

<%@ include file="/resources/templates/footer.jsp" %>

</body>
</html>
