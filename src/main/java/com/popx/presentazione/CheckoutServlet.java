package com.popx.presentazione;

import com.popx.modello.*;
import com.popx.persistenza.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet(name = "CheckoutServlet", value = "/CheckoutServlet")
public class CheckoutServlet extends HttpServlet {

    private final CarrelloDAO carrelloDAO;
    private final OrdineDAO ordineDAO;
    private final RigaOrdineDAO rigaOrdineDAO;
    private final ProdottoDAO prodottoDAO;

    // 👉 costruttore production
    public CheckoutServlet() {
        this.carrelloDAO = new CarrelloDAOImpl();
        this.ordineDAO = new OrdineDAOImpl();
        this.rigaOrdineDAO = new RigaOrdineDAOImpl();
        this.prodottoDAO = new ProdottoDAOImpl();
    }

    // 👉 costruttore test
    public CheckoutServlet(
            CarrelloDAO carrelloDAO,
            OrdineDAO ordineDAO,
            RigaOrdineDAO rigaOrdineDAO,
            ProdottoDAO prodottoDAO
    ) {
        this.carrelloDAO = carrelloDAO;
        this.ordineDAO = ordineDAO;
        this.rigaOrdineDAO = rigaOrdineDAO;
        this.prodottoDAO = prodottoDAO;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");

        if (userEmail == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("<script>alert('Utente non autenticato');</script>");
            return;
        }

        List<ProdottoBean> cart =
                (List<ProdottoBean>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("<script>alert('Il carrello è vuoto.');</script>");
            return;
        }

        double subtotal = 0;
        for (ProdottoBean p : cart) {
            subtotal += p.getQty() * p.getCost();
        }

        OrdineBean ordine = new OrdineBean(
                (float) subtotal,
                userEmail,
                new Date(System.currentTimeMillis())
        );

        try {
            ordineDAO.insertOrdine(ordine);
            int ordineId = ordine.getId();

            for (ProdottoBean p : cart) {
                rigaOrdineDAO.addRigaOrdine(
                        new RigaOrdineBean(
                                ordineId,
                                p.getId(),
                                p.getQty(),
                                (float) p.getCost()
                        )
                );

                prodottoDAO.updateStock(
                        p.getId(),
                        p.getPiecesInStock() - p.getQty()
                );
            }

            carrelloDAO.clearCartByUserEmail(userEmail);
            session.setAttribute("cart", null);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(
                    "<script>alert('Ordine completato con successo!');</script>"
            );

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                    "<script>alert('Errore interno nel completamento dell'ordine.');</script>"
            );
        }
    }
}
