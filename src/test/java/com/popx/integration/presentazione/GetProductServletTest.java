package com.popx.integration.presentazione;

import com.popx.modello.ProdottoBean;
import com.popx.persistenza.ProdottoDAO;
import com.popx.presentazione.GetProductServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

class GetProductServletTest {

    private ProdottoDAO prodottoDAO;
    private GetProductServlet servlet;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setup() {
        prodottoDAO = mock(ProdottoDAO.class);
        servlet = new GetProductServlet(prodottoDAO);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);

        when(request.getRequestDispatcher("/jsp/product.jsp"))
                .thenReturn(dispatcher);
    }

    @Test
    void missingId_returns400() throws Exception {
        when(request.getParameter("id")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "ID prodotto non fornito."
        );
        verifyNoInteractions(prodottoDAO);
    }

    @Test
    void productNotFound_returns404() throws Exception {
        when(request.getParameter("id")).thenReturn("P001");
        when(prodottoDAO.getProdottoById("P001")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_NOT_FOUND,
                "Prodotto non trovato."
        );
    }

    @Test
    void productFound_forwardsToJsp() throws Exception {
        ProdottoBean prodotto = new ProdottoBean();
        when(request.getParameter("id")).thenReturn("P001");
        when(prodottoDAO.getProdottoById("P001")).thenReturn(prodotto);

        servlet.doGet(request, response);

        verify(request).setAttribute("prod", prodotto);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void daoThrowsException_returns500() throws Exception {
        when(request.getParameter("id")).thenReturn("P001");
        when(prodottoDAO.getProdottoById("P001"))
                .thenThrow(new RuntimeException("DB error"));

        servlet.doGet(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Errore durante il recupero del prodotto."
        );
    }
}
