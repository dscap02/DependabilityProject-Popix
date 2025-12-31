package com.popx.presentazione;

import com.popx.modello.ProdottoBean;
import com.popx.persistenza.ProdottoDAO;
import com.popx.persistenza.ProdottoDAOImpl;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "GetProductServlet", urlPatterns = {"/getProduct"})
public class GetProductServlet extends HttpServlet {

    private final ProdottoDAO prodottoDAO;

    // 🔹 costruttore production
    public GetProductServlet() {
        this.prodottoDAO = new ProdottoDAOImpl();
    }

    // 🔹 costruttore test
    public GetProductServlet(ProdottoDAO prodottoDAO) {
        this.prodottoDAO = prodottoDAO;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String productId = request.getParameter("id");

        if (productId == null || productId.isEmpty()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "ID prodotto non fornito."
            );
            return;
        }

        try {
            ProdottoBean prodotto = prodottoDAO.getProdottoById(productId);

            if (prodotto == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Prodotto non trovato."
                );
                return;
            }

            request.setAttribute("prod", prodotto);
            request.getRequestDispatcher("/jsp/product.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Errore durante il recupero del prodotto."
            );
        }
    }
}
