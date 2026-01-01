<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">

    <title>Registrati</title>

    <link rel="icon" type="image/x-icon"
          href="${pageContext.request.contextPath}/resources/images/logo-noborderico.png">

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Bootstrap -->
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css"
          integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65"
          crossorigin="anonymous">

    <!-- Custom CSS -->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/styles/style-reg.css">

    <!-- SweetAlert -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

    <!-- Context path -->
    <script>
        var contextPath = '<%= request.getContextPath() %>';
    </script>

    <!-- Registration JS -->
    <script src="${pageContext.request.contextPath}/scripts/validation.js"></script>
</head>

<body>

<div class="container d-flex justify-content-center align-items-center min-vh-100">
    <div class="box-area row border rounded-5 p-4 bg-white shadow" style="max-width: 500px;">
        <h2 class="text-center mb-4">Crea un account</h2>

        <form id="form-reg" method="post" novalidate>

            <!-- USERNAME -->
            <div data-mdb-input-init class="form-outline mb-4">
                <label for="username" class="form-label visually-hidden">Nome utente</label>
                <input type="text"
                       class="form-control form-control-lg bg-light fs-6"
                       placeholder="Nome utente"
                       name="username"
                       id="username"
                       required>
            </div>

            <!-- EMAIL -->
            <div data-mdb-input-init class="form-outline mb-4">
                <label for="mail" class="form-label visually-hidden">Email</label>
                <input type="email"
                       class="form-control form-control-lg bg-light fs-6"
                       placeholder="Email"
                       name="mail"
                       id="mail"
                       required>
            </div>

            <!-- PASSWORD -->
            <div data-mdb-input-init class="form-outline mb-4">
                <label for="password" class="form-label visually-hidden">Password</label>
                <input type="password"
                       class="form-control form-control-lg bg-light fs-6"
                       placeholder="Password"
                       name="password"
                       id="password"
                       required>
            </div>

            <!-- REPEAT PASSWORD -->
            <div data-mdb-input-init class="form-outline mb-4">
                <label for="repeatPassword" class="form-label visually-hidden">Ripeti la password</label>
                <input type="password"
                       class="form-control form-control-lg bg-light fs-6"
                       placeholder="Ripeti la password"
                       name="password-repeat"
                       id="repeatPassword"
                       required>
            </div>

            <!-- SUBMIT -->
            <div class="input-group mb-4">
                <button type="submit"
                        class="btn btn-lg w-100 fs-6"
                        style="background-color: #9966ff; color: white;">
                    Registrati
                </button>
            </div>

            <!-- LOGIN LINK -->
            <p class="text-center text-muted mt-5 mb-0">
                Possiedi già un account?
                <a href="Login.jsp" class="fw-bold text-body"><u>Login</u></a>
            </p>

        </form>
    </div>
</div>

</body>
</html>
