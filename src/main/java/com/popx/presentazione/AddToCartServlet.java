package com.popx.presentazione;

import com.popx.modello.ProdottoBean;
import com.popx.modello.UserBean;
import com.popx.persistenza.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/addToCart")
public class AddToCartServlet extends HttpServlet {

    private final ProdottoDAO prodottoDAO;
    private final UserDAO<UserBean> userDAO;

    // 👉 costruttore production
    public AddToCartServlet() {
        this.prodottoDAO = new ProdottoDAOImpl();
        this.userDAO = new UserDAOImpl();
    }

    // 👉 costruttore test
    public AddToCartServlet(ProdottoDAO prodottoDAO, UserDAO<UserBean> userDAO) {
        this.prodottoDAO = prodottoDAO;
        this.userDAO = userDAO;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String productId = request.getParameter("productId");
        String qtyParam = request.getParameter("quantity");

        int quantity;
        try {
            quantity = Integer.parseInt(qtyParam);
            if (quantity <= 0) {
                writeJson(response, false, "Quantità non valida.");
                return;
            }
        } catch (NumberFormatException e) {
            writeJson(response, false, "Quantità non valida.");
            return;
        }

        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");

        // 🔐 controllo ruolo
        if (userEmail != null) {
            try {
                UserBean userBean = userDAO.getUserByEmail(userEmail);
                if (userBean != null && !"User".equals(userBean.getRole())) {
                    writeJson(response, false,
                            "Accesso negato: solo gli utenti con il ruolo 'User' possono aggiungere prodotti al carrello.");
                    return;
                }
            } catch (SQLException e) {
                writeJson(response, false, "Errore interno durante il controllo dei permessi.");
                return;
            }
        }

        List<ProdottoBean> cart = (List<ProdottoBean>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        ProdottoBean prodotto = prodottoDAO.getProdottoById(productId);
        if (prodotto == null) {
            writeJson(response, false, "Prodotto non trovato.");
            return;
        }

        for (ProdottoBean cartItem : cart) {
            if (cartItem.getId().equals(prodotto.getId())) {
                int currentQty = prodottoDAO.getProductQtyInCart(session, productId);
                if (currentQty + quantity <= prodotto.getPiecesInStock()) {
                    prodottoDAO.updateProductQtyInCart(session, productId, currentQty + quantity);
                    writeJson(response, true, "Prodotto aggiunto al carrello!");
                    return;
                } else {
                    writeJson(response, false, "Quantità non disponibile nel magazzino.");
                    return;
                }
            }
        }

        cart.add(prodotto);
        prodottoDAO.updateProductQtyInCart(session, productId, quantity);
        writeJson(response, true, "Prodotto aggiunto al carrello!");
    }


    private void writeJson(HttpServletResponse response, boolean success, String message) throws IOException {
        response.setContentType("application/json");
        response.getWriter()
                .write("{\"success\": " + success + ", \"message\": \"" + message + "\"}");
    }
}
