package com.popx.integration.presentazione;

import com.popx.modello.ProdottoBean;
import com.popx.persistenza.ProdottoDAOImpl;
import com.popx.presentazione.ProductServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductServletTest {

    private ProductServlet servlet;
    private ProdottoDAOImpl prodottoDAO;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private Part imgPart;

    private StringWriter responseBody;
    private PrintWriter writer;

    @BeforeEach
    void setup() throws Exception {
        prodottoDAO = mock(ProdottoDAOImpl.class);
        servlet = new ProductServlet(prodottoDAO);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        imgPart = mock(Part.class);

        responseBody = new StringWriter();
        writer = new PrintWriter(responseBody);

        when(response.getWriter()).thenReturn(writer);
        when(request.getPart("img_src")).thenReturn(imgPart);
        when(imgPart.getContentType()).thenReturn("image/png");
        when(imgPart.getInputStream()).thenReturn(
                new java.io.ByteArrayInputStream("img".getBytes())
        );
    }

    @Test
    void addProduct_success_returnsSuccessJson() throws Exception {
        when(request.getParameter("idProduct")).thenReturn("P01");
        when(request.getParameter("name")).thenReturn("Prodotto");
        when(request.getParameter("description")).thenReturn("Descrizione");
        when(request.getParameter("price")).thenReturn("10.5");
        when(request.getParameter("qty")).thenReturn("3");
        when(request.getParameter("brand")).thenReturn("Brand");
        when(request.getParameter("figure")).thenReturn("Figure");

        when(prodottoDAO.saveProdotto(any(ProdottoBean.class))).thenReturn(true);

        servlet.doPost(request, response);

        writer.flush();

        String json = responseBody.toString();
        assertTrue(json.contains("\"success\":true"));
        verify(prodottoDAO).saveProdotto(any(ProdottoBean.class));
    }

    @Test
    void addProduct_missingRequiredFields_returnsError() throws Exception {
        when(request.getParameter("idProduct")).thenReturn("");
        when(request.getParameter("name")).thenReturn("");

        servlet.doPost(request, response);

        writer.flush();

        String json = responseBody.toString();
        assertTrue(json.contains("Campi obbligatori mancanti"));
        verify(prodottoDAO, never()).saveProdotto(any());
    }

    @Test
    void addProduct_invalidImage_returnsError() throws Exception {
        when(request.getParameter("idProduct")).thenReturn("P01");
        when(request.getParameter("name")).thenReturn("Prodotto");
        when(request.getParameter("price")).thenReturn("10");
        when(request.getParameter("qty")).thenReturn("2");

        when(imgPart.getContentType()).thenReturn("text/plain");

        servlet.doPost(request, response);

        writer.flush();

        String json = responseBody.toString();
        assertTrue(json.contains("non è un'immagine valida"));
        verify(prodottoDAO, never()).saveProdotto(any());
    }

    @Test
    void addProduct_daoFails_returnsErrorJson() throws Exception {
        when(request.getParameter("idProduct")).thenReturn("P01");
        when(request.getParameter("name")).thenReturn("Prodotto");
        when(request.getParameter("price")).thenReturn("10");
        when(request.getParameter("qty")).thenReturn("2");

        when(prodottoDAO.saveProdotto(any())).thenReturn(false);

        servlet.doPost(request, response);

        writer.flush();

        String json = responseBody.toString();
        assertTrue(json.contains("Errore durante il salvataggio"));
    }
}
