<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/images/logo-noborderico.png">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/styles/style-log.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script>  var contextPath = '<%= request.getContextPath() %>'; </script>
    <script src="${pageContext.request.contextPath}/scripts/login-validation.js"></script>
    <title>Login</title>
</head>
<body>

<%@include file="/resources/templates/header.jsp"%>

<div class="container d-flex justify-content-center align-items-center min-vh-100">
    <div class="box-area row border rounded-5 p-4 bg-white shadow" style="max-width: 500px;">
        <h2 class="text-center mb-4">Accedi!</h2>

        <form id="login-form" method="post" >
            <div data-mdb-input-init class="form-outline mb-4">
                <label>
                    <input type="email" class="form-control form-control-lg bg-light fs-6" placeholder="Email" name="email" id="email">
                </label>
            </div>

            <div data-mdb-input-init class="form-outline mb-4">
                <label>
                    <input type="password" class="form-control form-control-lg bg-light fs-6" placeholder="Password" name="password" id="password">
                </label>
            </div>

            <div class="input-group mb-4">
                <button type="submit" class="btn btn-lg w-100 fs-6" style="background-color: #9966ff; color: white;">Login</button>
            </div>

            <p class="text-center text-muted mt-5 mb-0">Non hai un account?  <a href="Registation.jsp"
                                                                                class="fw-bold text-body"><u>Registrati</u></a></p>

        </form>
    </div>
</div>

<%@include file="/resources/templates/footer.jsp"%>

</body>
</html>
