package com.popx.presentazione;

import com.popx.persistenza.ProdottoDAO;
import com.popx.persistenza.ProdottoDAOImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet(name = "GetPictureServlet", urlPatterns = {"/getPictureServlet"})
public class GetPictureServlet extends HttpServlet {

    private final ProdottoDAO prodottoDAO;

    // 🔹 costruttore production
    public GetPictureServlet() {
        this.prodottoDAO = new ProdottoDAOImpl();
    }

    // 🔹 costruttore test
    public GetPictureServlet(ProdottoDAO prodottoDAO) {
        this.prodottoDAO = prodottoDAO;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String id = request.getParameter("id");

        if (id == null || id.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID prodotto mancante.");
            return;
        }

        try {
            byte[] imageData = prodottoDAO.getProductImageById(id);

            if (imageData != null) {
                response.setContentType("image/jpeg");
                response.getOutputStream().write(imageData);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Immagine non trovata.");
            }

        } catch (Exception e) {
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Errore durante il recupero dell'immagine."
            );
        }
    }
}
