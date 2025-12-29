package com.popx.integration.presentazione;

import com.popx.modello.ProdottoBean;
import com.popx.persistenza.ProdottoDAO;
import com.popx.presentazione.RemoveFromCartServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RemoveFromCartServletTest {

    private ProdottoDAO prodottoDAO;
    private RemoveFromCartServlet servlet;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    private StringWriter responseBody;
    private PrintWriter writer;

    @BeforeEach
    void setup() throws Exception {
        prodottoDAO = mock(ProdottoDAO.class);
        servlet = new RemoveFromCartServlet(prodottoDAO);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);

        responseBody = new StringWriter();
        writer = new PrintWriter(responseBody);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void removeFromCart_loggedUser_removesFromSessionAndDatabase() throws Exception {
        ProdottoBean prodotto = new ProdottoBean();
        prodotto.setId("P01");

        List<ProdottoBean> cart = new ArrayList<>();
        cart.add(prodotto);

        when(request.getParameter("productId")).thenReturn("P01");
        when(session.getAttribute("cart")).thenReturn(cart);
        when(session.getAttribute("userEmail")).thenReturn("user@mail.com");

        servlet.doPost(request, response);

        verify(prodottoDAO).removeProductFromCart("user@mail.com", "P01");
        assertTrue(cart.isEmpty());
        assertTrue(responseBody.toString().contains("\"success\": true"));
    }

    @Test
    void removeFromCart_guestUser_removesOnlyFromSession() throws Exception {
        ProdottoBean prodotto = new ProdottoBean();
        prodotto.setId("P01");

        List<ProdottoBean> cart = new ArrayList<>();
        cart.add(prodotto);

        when(request.getParameter("productId")).thenReturn("P01");
        when(session.getAttribute("cart")).thenReturn(cart);
        when(session.getAttribute("userEmail")).thenReturn(null);

        servlet.doPost(request, response);

        verify(prodottoDAO, never()).removeProductFromCart(any(), any());
        assertTrue(cart.isEmpty());
        assertTrue(responseBody.toString().contains("\"success\": true"));
    }

    @Test
    void removeFromCart_invalidProductId_returnsError() throws Exception {
        when(request.getParameter("productId")).thenReturn("");
        when(session.getAttribute("cart")).thenReturn(new ArrayList<>());

        servlet.doPost(request, response);

        assertTrue(responseBody.toString().contains("Invalid product ID"));
    }

    @Test
    void removeFromCart_sqlException_returns500() throws Exception {
        ProdottoBean prodotto = new ProdottoBean();
        prodotto.setId("P01");

        List<ProdottoBean> cart = new ArrayList<>();
        cart.add(prodotto);

        when(request.getParameter("productId")).thenReturn("P01");
        when(session.getAttribute("cart")).thenReturn(cart);
        when(session.getAttribute("userEmail")).thenReturn("user@mail.com");

        doThrow(new SQLException())
                .when(prodottoDAO)
                .removeProductFromCart("user@mail.com", "P01");

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(responseBody.toString().contains("Errore nella rimozione"));
    }
}
