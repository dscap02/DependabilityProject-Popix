package com.popx.integration.presentazione;

import com.popx.modello.ProdottoBean;
import com.popx.persistenza.ProdottoDAOImpl;
import com.popx.presentazione.UpdateProductServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

class UpdateProductServletTest {

    private ProdottoDAOImpl prodottoDAO;
    private UpdateProductServlet servlet;

    private HttpServletRequest request;
    private HttpServletResponse response;

    private StringWriter responseBody;
    private PrintWriter writer;

    @BeforeEach
    void setup() throws Exception {
        prodottoDAO = mock(ProdottoDAOImpl.class);
        servlet = new UpdateProductServlet(prodottoDAO);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        responseBody = new StringWriter();
        writer = new PrintWriter(responseBody);
        when(response.getWriter()).thenReturn(writer);

        when(request.getParameter("idProduct")).thenReturn("P001");
        when(request.getParameter("name")).thenReturn("Prodotto");
        when(request.getParameter("description")).thenReturn("Desc");
        when(request.getParameter("price")).thenReturn("10.0");
        when(request.getParameter("qty")).thenReturn("5");
        when(request.getParameter("brand")).thenReturn("Brand");
        when(request.getParameter("figure")).thenReturn("Figure");
        when(request.getParameter("current_img_src")).thenReturn("img.png");
    }

    @Test
    void updateProduct_success() throws Exception {
        when(prodottoDAO.getProdottoById("P001")).thenReturn(new ProdottoBean());
        when(prodottoDAO.updateProduct(any())).thenReturn(true);
        when(request.getPart("img_src")).thenReturn(null);

        servlet.doPost(request, response);

        verify(prodottoDAO).updateProduct(any());
        assert responseBody.toString().contains("\"success\":true");
    }

    @Test
    void invalidImage_returnsError() throws Exception {
        Part part = mock(Part.class);
        when(part.getSize()).thenReturn(10L);
        when(part.getContentType()).thenReturn("text/plain");
        when(request.getPart("img_src")).thenReturn(part);

        servlet.doPost(request, response);

        assert responseBody.toString().contains("non è un'immagine valida");
        verify(prodottoDAO, never()).updateProduct(any());
    }

    @Test
    void daoThrowsException_returnsError() throws Exception {
        when(request.getPart("img_src")).thenReturn(null);
        when(prodottoDAO.updateProduct(any()))
                .thenThrow(new RuntimeException("DB error"));

        servlet.doPost(request, response);

        assert responseBody.toString().contains("\"success\":false");
    }
}
