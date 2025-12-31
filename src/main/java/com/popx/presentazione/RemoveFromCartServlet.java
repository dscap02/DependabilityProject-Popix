package com.popx.presentazione;

import com.popx.modello.ProdottoBean;
import com.popx.persistenza.ProdottoDAO;
import com.popx.persistenza.ProdottoDAOImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/RemoveFromCartServlet")
public class RemoveFromCartServlet extends HttpServlet {

    private final ProdottoDAO prodottoDAO;

    // 👉 costruttore production
    public RemoveFromCartServlet() {
        this.prodottoDAO = new ProdottoDAOImpl();
    }

    // 👉 costruttore test
    public RemoveFromCartServlet(ProdottoDAO prodottoDAO) {
        this.prodottoDAO = prodottoDAO;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String productId = request.getParameter("productId");

        HttpSession session = request.getSession();
        List<ProdottoBean> cart = (List<ProdottoBean>) session.getAttribute("cart");
        String userEmail = (String) session.getAttribute("userEmail");

        try {
            if (productId != null && !productId.isEmpty() && cart != null) {

                cart.removeIf(product -> product.getId().equals(productId));
                session.setAttribute("cart", cart);

                if (userEmail != null) {
                    prodottoDAO.removeProductFromCart(userEmail, productId);
                }

                response.setContentType("application/json");
                response.getWriter().write("{\"success\": true}");

            } else {
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"success\": false, \"message\": \"Invalid product ID.\"}"
                );
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                    "{\"success\": false, \"message\": \"Errore nella rimozione del prodotto.\"}"
            );
        }
    }
}
