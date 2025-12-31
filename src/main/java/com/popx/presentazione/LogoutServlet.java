package com.popx.presentazione;

import com.popx.modello.ProdottoBean;
import com.popx.persistenza.ProdottoDAO;
import com.popx.persistenza.ProdottoDAOImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private final ProdottoDAO prodottoDAO;
    private static final Logger LOGGER = Logger.getLogger(LogoutServlet.class.getName());

    // produzione
    public LogoutServlet() {
        this.prodottoDAO = new ProdottoDAOImpl();
    }

    // test (dependency injection)
    public LogoutServlet(ProdottoDAO prodottoDAO) {
        this.prodottoDAO = prodottoDAO;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        List<ProdottoBean> cart = (List<ProdottoBean>) session.getAttribute("cart");

        if (cart != null) {
            String userEmail = (String) session.getAttribute("userEmail");

            if (userEmail != null) {
                try {
                    prodottoDAO.saveCartToDatabase(userEmail, cart);
                    session.removeAttribute("cart");
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "SQL error saving cart during logout", e);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Errore durante il salvataggio del carrello.");
                    return;
                }
            }
        }

        session.invalidate();  // Invalidare la sessione e quindi il logout
        response.sendRedirect(request.getContextPath() + "/jsp/HomePage.jsp");  // Redirige alla HomePage

    }
}
