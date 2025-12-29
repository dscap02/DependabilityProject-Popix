package com.popx.integration.presentazione;

import com.popx.modello.ProdottoBean;
import com.popx.modello.UserBean;
import com.popx.persistenza.ProdottoDAO;
import com.popx.persistenza.UserDAO;
import com.popx.presentazione.AddToCartServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddToCartServletTest {

    private ProdottoDAO prodottoDAO;
    private UserDAO<UserBean> userDAO;
    private AddToCartServlet servlet;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    private StringWriter responseBody;
    private PrintWriter writer;

    @BeforeEach
    void setup() throws Exception {
        prodottoDAO = mock(ProdottoDAO.class);
        userDAO = mock(UserDAO.class);
        servlet = new AddToCartServlet(prodottoDAO, userDAO);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);

        responseBody = new StringWriter();
        writer = new PrintWriter(responseBody);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void addToCart_guestUser_addsProductSuccessfully() throws Exception {
        ProdottoBean prodotto = new ProdottoBean();
        prodotto.setId("P01");
        prodotto.setPiecesInStock(10);

        when(request.getParameter("productId")).thenReturn("P01");
        when(request.getParameter("quantity")).thenReturn("2");
        when(session.getAttribute("userEmail")).thenReturn(null);
        when(session.getAttribute("cart")).thenReturn(null);
        when(prodottoDAO.getProdottoById("P01")).thenReturn(prodotto);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("cart"), any(List.class));
        verify(prodottoDAO).updateProductQtyInCart(eq(session), eq("P01"), eq(2));

        assertTrue(responseBody.toString().contains("\"success\": true"));
    }

    @Test
    void addToCart_loggedUserWithWrongRole_denied() throws Exception {
        UserBean admin = new UserBean();
        admin.setRole("Admin");

        when(request.getParameter("productId")).thenReturn("P01");
        when(request.getParameter("quantity")).thenReturn("1");
        when(session.getAttribute("userEmail")).thenReturn("admin@mail.com");
        when(userDAO.getUserByEmail("admin@mail.com")).thenReturn(admin);

        servlet.doPost(request, response);

        assertTrue(responseBody.toString().contains("Accesso negato"));
        verify(prodottoDAO, never()).getProdottoById(anyString());
    }

    @Test
    void addToCart_productAlreadyInCart_updatesQuantity() throws Exception {
        ProdottoBean prodotto = new ProdottoBean();
        prodotto.setId("P01");
        prodotto.setPiecesInStock(10);

        List<ProdottoBean> cart = new ArrayList<>();
        cart.add(prodotto);

        when(request.getParameter("productId")).thenReturn("P01");
        when(request.getParameter("quantity")).thenReturn("2");
        when(session.getAttribute("userEmail")).thenReturn(null);
        when(session.getAttribute("cart")).thenReturn(cart);
        when(prodottoDAO.getProdottoById("P01")).thenReturn(prodotto);
        when(prodottoDAO.getProductQtyInCart(session, "P01")).thenReturn(3);

        servlet.doPost(request, response);

        verify(prodottoDAO)
                .updateProductQtyInCart(session, "P01", 5);

        assertTrue(responseBody.toString().contains("\"success\": true"));
    }

    @Test
    void addToCart_quantityExceedsStock_returnsError() throws Exception {
        ProdottoBean prodotto = new ProdottoBean();
        prodotto.setId("P01");
        prodotto.setPiecesInStock(4);

        List<ProdottoBean> cart = new ArrayList<>();
        cart.add(prodotto);

        when(request.getParameter("productId")).thenReturn("P01");
        when(request.getParameter("quantity")).thenReturn("3");
        when(session.getAttribute("userEmail")).thenReturn(null);
        when(session.getAttribute("cart")).thenReturn(cart);
        when(prodottoDAO.getProdottoById("P01")).thenReturn(prodotto);
        when(prodottoDAO.getProductQtyInCart(session, "P01")).thenReturn(2);

        servlet.doPost(request, response);

        assertTrue(responseBody.toString().contains("Quantità non disponibile"));
    }

    @Test
    void addToCart_productNotFound_returnsError() throws Exception {
        when(request.getParameter("productId")).thenReturn("P99");
        when(request.getParameter("quantity")).thenReturn("1");
        when(session.getAttribute("userEmail")).thenReturn(null);
        when(prodottoDAO.getProdottoById("P99")).thenReturn(null);

        servlet.doPost(request, response);

        assertTrue(responseBody.toString().contains("Errore nell'aggiungere il prodotto"));
    }

    @Test
    void addToCart_sqlExceptionDuringRoleCheck_returnsError() throws Exception {
        when(request.getParameter("productId")).thenReturn("P01");
        when(request.getParameter("quantity")).thenReturn("1");
        when(session.getAttribute("userEmail")).thenReturn("user@mail.com");
        when(userDAO.getUserByEmail("user@mail.com")).thenThrow(new SQLException());

        servlet.doPost(request, response);

        assertTrue(responseBody.toString().contains("Errore interno"));
    }
}
