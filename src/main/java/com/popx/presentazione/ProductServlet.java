package com.popx.presentazione;

import com.google.gson.JsonObject;
import com.popx.modello.ProdottoBean;
import com.popx.persistenza.DataSourceSingleton;
import com.popx.persistenza.ProdottoDAOImpl;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;

@WebServlet("/addProductServlet")
@MultipartConfig(maxFileSize = 2 * 1024 * 1024)
public class ProductServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ProdottoDAOImpl prodottoDAO;

    // 👉 costruttore production (Tomcat)
    public ProductServlet() {
        this.prodottoDAO = new ProdottoDAOImpl(DataSourceSingleton.getInstance());
    }

    // 👉 costruttore test
    public ProductServlet(ProdottoDAOImpl prodottoDAO) {
        this.prodottoDAO = prodottoDAO;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        JsonObject jsonResponse = new JsonObject();

        try {
            String id = request.getParameter("idProduct");
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String costStr = request.getParameter("price");
            String piecesInStockStr = request.getParameter("qty");
            String brand = request.getParameter("brand");
            String figure = request.getParameter("figure");
            Part imgPart = request.getPart("img_src");

            if (id == null || id.isEmpty() ||
                    name == null || name.isEmpty() ||
                    costStr == null || costStr.isEmpty() ||
                    piecesInStockStr == null || piecesInStockStr.isEmpty()) {

                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Campi obbligatori mancanti.");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            double cost = Double.parseDouble(costStr);
            int piecesInStock = Integer.parseInt(piecesInStockStr);

            byte[] imgBytes = null;
            if (imgPart != null && imgPart.getContentType().startsWith("image/")) {
                try (InputStream imgInputStream = imgPart.getInputStream()) {
                    imgBytes = imgInputStream.readAllBytes();
                }
            } else if (imgPart != null) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Il file caricato non è un'immagine valida.");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            ProdottoBean prodotto = new ProdottoBean(
                    id, name, description, cost, piecesInStock, brand, imgBytes, figure
            );

            boolean isSaved = prodottoDAO.saveProdotto(prodotto);

            jsonResponse.addProperty("success", isSaved);
            jsonResponse.addProperty(
                    "message",
                    isSaved ? "Prodotto aggiunto con successo." : "Errore durante il salvataggio del prodotto."
            );

        } catch (Exception e) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Errore interno.");
        }

        response.getWriter().write(jsonResponse.toString());
    }
}
