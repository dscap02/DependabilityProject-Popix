package com.popx.integration.presentazione;

import com.popx.modello.ProdottoBean;
import com.popx.persistenza.*;
import com.popx.presentazione.CheckoutServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CheckoutServletTest {

    private CarrelloDAO carrelloDAO;
    private OrdineDAO ordineDAO;
    private RigaOrdineDAO rigaOrdineDAO;
    private ProdottoDAO prodottoDAO;

    private CheckoutServlet servlet;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    private StringWriter responseBody;

    @BeforeEach
    void setup() throws Exception {
        carrelloDAO = mock(CarrelloDAO.class);
        ordineDAO = mock(OrdineDAO.class);
        rigaOrdineDAO = mock(RigaOrdineDAO.class);
        prodottoDAO = mock(ProdottoDAO.class);

        servlet = new CheckoutServlet(
                carrelloDAO, ordineDAO, rigaOrdineDAO, prodottoDAO
        );

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);

        responseBody = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));
    }

    @Test
    void checkout_successfulOrder() throws Exception {
        ProdottoBean prodotto = mock(ProdottoBean.class);
        when(prodotto.getId()).thenReturn("P01");
        when(prodotto.getQty()).thenReturn(2);
        when(prodotto.getCost()).thenReturn(10.0);
        when(prodotto.getPiecesInStock()).thenReturn(10);

        when(session.getAttribute("userEmail")).thenReturn("user@mail.com");
        when(session.getAttribute("cart")).thenReturn(List.of(prodotto));

        servlet.doPost(request, response);

        verify(ordineDAO).insertOrdine(any());
        verify(rigaOrdineDAO).addRigaOrdine(any());
        verify(prodottoDAO).updateStock("P01", 8);
        verify(carrelloDAO).clearCartByUserEmail("user@mail.com");
        verify(session).setAttribute("cart", null);

        assertTrue(responseBody.toString().contains("Ordine completato"));
    }

    @Test
    void checkout_notAuthenticated_returns401() throws Exception {
        when(session.getAttribute("userEmail")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void checkout_emptyCart_returns400() throws Exception {
        when(session.getAttribute("userEmail")).thenReturn("user@mail.com");
        when(session.getAttribute("cart")).thenReturn(List.of());

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void checkout_exception_returns500() throws Exception {
        when(session.getAttribute("userEmail")).thenReturn("user@mail.com");
        when(session.getAttribute("cart")).thenReturn(List.of(mock(ProdottoBean.class)));

        doThrow(new RuntimeException())
                .when(ordineDAO).insertOrdine(any());

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
