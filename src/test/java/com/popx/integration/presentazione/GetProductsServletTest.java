package com.popx.integration.presentazione;

import com.popx.modello.ProdottoBean;
import com.popx.persistenza.ProdottoDAO;
import com.popx.presentazione.GetProductsServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GetProductsServletTest {

    private ProdottoDAO prodottoDAO;
    private GetProductsServlet servlet;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setup() {
        prodottoDAO = mock(ProdottoDAO.class);
        servlet = new GetProductsServlet(prodottoDAO);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);

        when(request.getRequestDispatcher("/jsp/Catalog.jsp"))
                .thenReturn(dispatcher);
    }

    @Test
    void noFilters_returnsAllProducts_firstPage() throws Exception {
        List<ProdottoBean> products = List.of(
                new ProdottoBean(), new ProdottoBean(), new ProdottoBean()
        );

        when(prodottoDAO.getAllProducts()).thenReturn(products);
        when(request.getParameter("page")).thenReturn("1");

        servlet.doGet(request, response);

        verify(prodottoDAO).getAllProducts();
        verify(request).setAttribute(eq("products"), eq(products));
        verify(request).setAttribute("currentPage", 1);
        verify(request).setAttribute("totalPages", 1);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void categoryFilter_applied() throws Exception {
        when(request.getParameter("category")).thenReturn("Marvel");
        when(request.getParameter("price")).thenReturn(null);

        List<ProdottoBean> products = List.of(new ProdottoBean());
        when(prodottoDAO.getProdottiByBrand("Marvel")).thenReturn(products);

        servlet.doGet(request, response);

        verify(prodottoDAO).getProdottiByBrand("Marvel");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void categoryAndPriceFilter_appliedAscending() throws Exception {
        when(request.getParameter("category")).thenReturn("Marvel");
        when(request.getParameter("price")).thenReturn("low");

        when(prodottoDAO.getProdottiByBrandAndPrice("Marvel", true))
                .thenReturn(List.of(new ProdottoBean()));

        servlet.doGet(request, response);

        verify(prodottoDAO)
                .getProdottiByBrandAndPrice("Marvel", true);
    }

    @Test
    void invalidPage_defaultsToFirst() throws Exception {
        when(request.getParameter("page")).thenReturn("abc");
        when(prodottoDAO.getAllProducts()).thenReturn(List.of(new ProdottoBean()));

        servlet.doGet(request, response);

        verify(request).setAttribute("currentPage", 1);
    }

    @Test
    void daoThrowsException_returns500() throws Exception {
        when(prodottoDAO.getAllProducts())
                .thenThrow(new RuntimeException("DB error"));

        servlet.doGet(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Errore durante il recupero dei prodotti."
        );
    }
}
