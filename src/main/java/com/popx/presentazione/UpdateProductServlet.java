package com.popx.presentazione;

import com.google.gson.JsonObject;
import com.popx.modello.ProdottoBean;
import com.popx.persistenza.ProdottoDAOImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;

@WebServlet("/updateProductServlet")
@MultipartConfig(maxFileSize = 2 * 1024 * 1024)
public class UpdateProductServlet extends HttpServlet {

    private final ProdottoDAOImpl prodottoDAO;

    // 🔹 costruttore production
    public UpdateProductServlet() {
        this.prodottoDAO = new ProdottoDAOImpl();
    }

    // 🔹 costruttore test
    public UpdateProductServlet(ProdottoDAOImpl prodottoDAO) {
        this.prodottoDAO = prodottoDAO;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        JsonObject jsonResponse = new JsonObject();

        try {
            String idProduct = request.getParameter("idProduct");
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            double price = Double.parseDouble(request.getParameter("price"));
            int qty = Integer.parseInt(request.getParameter("qty"));
            String brand = request.getParameter("brand");
            String figure = request.getParameter("figure");
            Part imgPart = request.getPart("img_src");
            String currentImgSrc = request.getParameter("current_img_src");

            ProdottoBean prodotto =
                    new ProdottoBean(idProduct, name, description, price, qty, brand, null, figure);

            // 🔹 gestione immagine
            if (imgPart != null && imgPart.getSize() > 0) {
                if (!imgPart.getContentType().startsWith("image/")) {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Il file caricato non è un'immagine valida.");
                    response.getWriter().write(jsonResponse.toString());
                    return;
                }

                try (InputStream is = imgPart.getInputStream()) {
                    prodotto.setImg(is.readAllBytes());
                }
            } else if (currentImgSrc != null && !currentImgSrc.isEmpty()) {
                ProdottoBean existing = prodottoDAO.getProdottoById(idProduct);
                if (existing != null) {
                    prodotto.setImg(existing.getImg());
                }
            }

            boolean updated = prodottoDAO.updateProduct(prodotto);

            jsonResponse.addProperty("success", updated);
            jsonResponse.addProperty(
                    "message",
                    updated ? "Prodotto aggiornato con successo."
                            : "Errore durante l'aggiornamento del prodotto."
            );

        } catch (Exception e) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Errore interno: " + e.getMessage());
        }

        response.getWriter().write(jsonResponse.toString());
    }
}
