package com.popx.presentazione;

import com.popx.modello.ProdottoBean;
import com.popx.persistenza.ProdottoDAO;
import com.popx.persistenza.ProdottoDAOImpl;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "GetProductsServlet", urlPatterns = {"/getProductsServlet"})
public class GetProductsServlet extends HttpServlet {

    private static final int PRODUCTS_PER_PAGE = 6;
    private final ProdottoDAO prodottoDAO;

    // 🔹 costruttore production
    public GetProductsServlet() {
        this.prodottoDAO = new ProdottoDAOImpl();
    }

    // 🔹 costruttore test
    public GetProductsServlet(ProdottoDAO prodottoDAO) {
        this.prodottoDAO = prodottoDAO;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String category = request.getParameter("category");
        String price = request.getParameter("price");
        String pageParam = request.getParameter("page");

        int currentPage = 1;
        try {
            if (pageParam != null) {
                currentPage = Math.max(1, Integer.parseInt(pageParam));
            }
        } catch (NumberFormatException ignored) {
            currentPage = 1;
        }

        try {
            List<ProdottoBean> prodotti;

            if (category != null && !category.isEmpty()) {
                if (price != null && !price.isEmpty()) {
                    boolean ascending = "low".equalsIgnoreCase(price);
                    prodotti = prodottoDAO.getProdottiByBrandAndPrice(category, ascending);
                } else {
                    prodotti = prodottoDAO.getProdottiByBrand(category);
                }
            } else if (price != null && !price.isEmpty()) {
                boolean ascending = "low".equalsIgnoreCase(price);
                prodotti = prodottoDAO.getProdottiSortedByPrice(ascending);
            } else {
                prodotti = prodottoDAO.getAllProducts();
            }

            int totalProducts = prodotti.size();
            int totalPages = (int) Math.ceil((double) totalProducts / PRODUCTS_PER_PAGE);
            if (totalPages == 0) totalPages = 1;

            currentPage = Math.min(currentPage, totalPages);

            int start = (currentPage - 1) * PRODUCTS_PER_PAGE;
            int end = Math.min(start + PRODUCTS_PER_PAGE, totalProducts);

            List<ProdottoBean> productsForPage = prodotti.subList(start, end);

            request.setAttribute("products", productsForPage);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);

            request.getRequestDispatcher("/jsp/Catalog.jsp").forward(request, response);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Errore durante il recupero dei prodotti.");
        }
    }
}
