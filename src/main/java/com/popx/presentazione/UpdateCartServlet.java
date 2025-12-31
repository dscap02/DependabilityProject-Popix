package com.popx.presentazione;

import com.popx.persistenza.ProdottoDAO;
import com.popx.persistenza.ProdottoDAOImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/UpdateCartServlet")
public class UpdateCartServlet extends HttpServlet {

    private final ProdottoDAO prodottoDAO;

    // 👉 costruttore production
    public UpdateCartServlet() {
        this.prodottoDAO = new ProdottoDAOImpl();
    }

    // 👉 costruttore test
    public UpdateCartServlet(ProdottoDAO prodottoDAO) {
        this.prodottoDAO = prodottoDAO;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String productId = request.getParameter("productId");
        int qty;

        try {
            qty = Integer.parseInt(request.getParameter("qty"));
        } catch (NumberFormatException e) {
            qty = 1;
        }

        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");

        try {
            prodottoDAO.updateProductQtyInCart(session, productId, qty);

            if (userEmail != null) {
                prodottoDAO.updateCartProductQuantityInDatabase(userEmail, productId, qty);
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\": true}");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                    "{\"success\": false, \"message\": \"Errore nell'aggiornamento del carrello.\"}"
            );
        }
    }
}
