package com.popx.integration.presentazione;

import com.popx.modello.ProdottoBean;
import com.popx.persistenza.ProdottoDAO;
import com.popx.presentazione.LogoutServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class LogoutServletTest {

    private ProdottoDAO prodottoDAO;
    private LogoutServlet servlet;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    private StringWriter responseBody;
    private PrintWriter writer;

    @BeforeEach
    void setup() throws Exception {
        // DAO mock
        prodottoDAO = mock(ProdottoDAO.class);

        // servlet con dependency injection
        servlet = new LogoutServlet(prodottoDAO);

        // servlet API mocks
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/popx");

        // writer reale per catturare l'output
        responseBody = new StringWriter();
        writer = new PrintWriter(responseBody);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void logout_withCartAndUser_savesCartAndRedirects() throws Exception {
        List<ProdottoBean> cart = List.of(new ProdottoBean());

        when(session.getAttribute("cart")).thenReturn(cart);
        when(session.getAttribute("userEmail")).thenReturn("test@mail.com");

        servlet.doGet(request, response);

        verify(prodottoDAO).saveCartToDatabase("test@mail.com", cart);
        verify(session).removeAttribute("cart");
        verify(session).invalidate();
        verify(response).sendRedirect("/popx/jsp/HomePage.jsp");
    }

    @Test
    void logout_withoutCart_redirectsOnly() throws Exception {
        when(session.getAttribute("cart")).thenReturn(null);

        servlet.doGet(request, response);

        verify(prodottoDAO, never()).saveCartToDatabase(any(), any());
        verify(session).invalidate();
        verify(response).sendRedirect("/popx/jsp/HomePage.jsp");
    }

    @Test
    void logout_withCartButNoUserEmail_redirectsOnly() throws Exception {
        List<ProdottoBean> cart = List.of(new ProdottoBean());

        when(session.getAttribute("cart")).thenReturn(cart);
        when(session.getAttribute("userEmail")).thenReturn(null);

        servlet.doGet(request, response);

        verify(prodottoDAO, never()).saveCartToDatabase(any(), any());
        verify(session).invalidate();
        verify(response).sendRedirect("/popx/jsp/HomePage.jsp");
    }

    @Test
    void logout_sqlException_returns500AndWritesMessage() throws Exception {
        List<ProdottoBean> cart = List.of(new ProdottoBean());

        when(session.getAttribute("cart")).thenReturn(cart);
        when(session.getAttribute("userEmail")).thenReturn("test@mail.com");

        doThrow(new SQLException())
                .when(prodottoDAO)
                .saveCartToDatabase(anyString(), anyList());

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        // verifica sul contenuto scritto (NON sul writer)
        assertTrue(
                responseBody.toString()
                        .contains("Errore durante il salvataggio del carrello")
        );
    }
}
