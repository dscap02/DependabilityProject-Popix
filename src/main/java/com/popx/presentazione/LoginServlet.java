package com.popx.presentazione;

import com.popx.servizio.AuthenticationService;
import com.popx.modello.UserBean;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private AuthenticationService authService;

    // Costruttore usato in produzione
    public LoginServlet() {
        this.authService = new AuthenticationService();
    }

    // Costruttore usato nei test (dependency injection)
    LoginServlet(AuthenticationService authService) {
        this.authService = authService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            UserBean user = authService.login(email, password);

            HttpSession session = request.getSession(true);
            session.setAttribute("role", user.getRole());
            session.setAttribute("userEmail", user.getEmail());

            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Sbagliato email o password.");
        }
    }
}
