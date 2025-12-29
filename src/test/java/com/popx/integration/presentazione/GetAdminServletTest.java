package com.popx.integration.presentazione;

import com.popx.modello.ProdottoBean;
import com.popx.persistenza.ProdottoDAO;
import com.popx.presentazione.GetAdminServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GetAdminServletTest {

    private ProdottoDAO prodottoDAO;
    private GetAdminServlet servlet;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setup() {
        prodottoDAO = mock(ProdottoDAO.class);
        servlet = new GetAdminServlet(prodottoDAO);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);

        when(request.getRequestDispatcher("/jsp/DashboardAdmin.jsp"))
                .thenReturn(dispatcher);
    }

    @Test
    void getAdmin_success_firstPage() throws Exception {
        List<ProdottoBean> prodotti = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            prodotti.add(new ProdottoBean());
        }

        when(prodottoDAO.getAllProducts()).thenReturn(prodotti);
        when(request.getParameter("page")).thenReturn("1");

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("products"), anyList());
        verify(request).setAttribute("currentPage", 1);
        verify(request).setAttribute("totalPages", 2);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void getAdmin_invalidPage_defaultsToOne() throws Exception {
        when(prodottoDAO.getAllProducts()).thenReturn(List.of(new ProdottoBean()));
        when(request.getParameter("page")).thenReturn("abc");

        servlet.doGet(request, response);

        verify(request).setAttribute("currentPage", 1);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void getAdmin_daoReturnsNull_returns500() throws Exception {
        when(prodottoDAO.getAllProducts()).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Errore durante il recupero dei prodotti."
        );
    }

    @Test
    void getAdmin_exception_returns500() throws Exception {
        when(prodottoDAO.getAllProducts())
                .thenThrow(new RuntimeException("DB error"));

        servlet.doGet(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Errore durante il recupero dei prodotti."
        );
    }
}
