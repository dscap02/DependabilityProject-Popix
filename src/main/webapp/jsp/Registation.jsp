<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <link rel="icon" type="site-icon" href="${pageContext.request.contextPath}/resources/images/logo-noborderico.png">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/styles/style-reg.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script>  var contextPath = '<%= request.getContextPath() %>'; </script>
    <script src="${pageContext.request.contextPath}/scripts/validation.js"></script>

    <title>Registrati</title>
</head>
<body>

<%@ include file="/resources/templates/header.jsp" %>

<div class="container d-flex justify-content-center align-items-center min-vh-100">
    <div class="box-area row border rounded-5 p-4 bg-white shadow" style="max-width: 500px;">
        <h2 class="text-center mb-4">Crea un account</h2>

        <form id="form-reg" method="post">
            <div data-mdb-input-init class="form-outline mb-4">
                <label>
                    <input type="text" class="form-control form-control-lg bg-light fs-6" placeholder="Nome utente" name="username" id="username">
                </label>
            </div>

            <div data-mdb-input-init class="form-outline mb-4">
                <label>
                    <input type="email" class="form-control form-control-lg bg-light fs-6" placeholder="Email" name="mail" id="mail">
                </label>
            </div>

            <div data-mdb-input-init class="form-outline mb-4">
                <input type="password" class="form-control form-control-lg bg-light fs-6" placeholder="Password" name="password" id="password">
            </div>

            <div data-mdb-input-init class="form-outline mb-4">
                <input type="password" class="form-control form-control-lg bg-light fs-6" placeholder="Ripeti la password" name="password-repeat" id="repeatPassword">
            </div>

            <div class="input-group mb-4">
                <button type="submit" class="btn btn-lg w-100 fs-6" style="background-color: #9966ff; color: white;">Registrati</button>
            </div>

            <p class="text-center text-muted mt-5 mb-0">Possiedi già un account? <a href="#" class="fw-bold text-body"><u>Login</u></a></p>
        </form>
    </div>
</div>

<%@ include file="/resources/templates/footer.jsp" %>

</body>
</html>
