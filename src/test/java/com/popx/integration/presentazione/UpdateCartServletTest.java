package com.popx.integration.presentazione;

import com.popx.persistenza.ProdottoDAO;
import com.popx.presentazione.UpdateCartServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateCartServletTest {

    private ProdottoDAO prodottoDAO;
    private UpdateCartServlet servlet;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    private StringWriter responseBody;
    private PrintWriter writer;

    @BeforeEach
    void setup() throws Exception {
        prodottoDAO = mock(ProdottoDAO.class);
        servlet = new UpdateCartServlet(prodottoDAO);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);

        responseBody = new StringWriter();
        writer = new PrintWriter(responseBody);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void updateCart_loggedUser_updatesSessionAndDatabase() throws Exception {
        when(request.getParameter("productId")).thenReturn("P01");
        when(request.getParameter("qty")).thenReturn("3");
        when(session.getAttribute("userEmail")).thenReturn("user@mail.com");

        servlet.doPost(request, response);

        verify(prodottoDAO).updateProductQtyInCart(session, "P01", 3);
        verify(prodottoDAO).updateCartProductQuantityInDatabase("user@mail.com", "P01", 3);
        assertTrue(responseBody.toString().contains("\"success\": true"));
    }

    @Test
    void updateCart_guestUser_updatesSessionOnly() throws Exception {
        when(request.getParameter("productId")).thenReturn("P01");
        when(request.getParameter("qty")).thenReturn("2");
        when(session.getAttribute("userEmail")).thenReturn(null);

        servlet.doPost(request, response);

        verify(prodottoDAO).updateProductQtyInCart(session, "P01", 2);
        verify(prodottoDAO, never())
                .updateCartProductQuantityInDatabase(any(), any(), anyInt());
    }

    @Test
    void updateCart_invalidQty_defaultsToOne() throws Exception {
        when(request.getParameter("productId")).thenReturn("P01");
        when(request.getParameter("qty")).thenReturn("abc");

        servlet.doPost(request, response);

        verify(prodottoDAO).updateProductQtyInCart(session, "P01", 1);
    }

    @Test
    void updateCart_exception_returns500() throws Exception {
        when(request.getParameter("productId")).thenReturn("P01");
        when(request.getParameter("qty")).thenReturn("2");

        doThrow(new RuntimeException())
                .when(prodottoDAO)
                .updateProductQtyInCart(any(), any(), anyInt());

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(responseBody.toString().contains("Errore nell'aggiornamento"));
    }
}
