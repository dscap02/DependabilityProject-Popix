package com.popx.integration.presentazione;

import com.popx.persistenza.ProdottoDAO;
import com.popx.presentazione.DeleteProductServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DeleteProductServletTest {

    private ProdottoDAO prodottoDAO;
    private DeleteProductServlet servlet;

    private HttpServletRequest request;
    private HttpServletResponse response;

    private StringWriter responseBody;
    private PrintWriter writer;

    @BeforeEach
    void setup() throws Exception {
        prodottoDAO = mock(ProdottoDAO.class);
        servlet = new DeleteProductServlet(prodottoDAO);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        when(request.getParameter("id")).thenReturn("P001");

        responseBody = new StringWriter();
        writer = new PrintWriter(responseBody);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void deleteProduct_success() throws Exception {
        servlet.doPost(request, response);

        verify(prodottoDAO).deleteProductById("P001");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        assertTrue(responseBody.toString().contains("\"success\": true"));
    }

    @Test
    void deleteProduct_exception_returns500() throws Exception {
        doThrow(new RuntimeException("DB error"))
                .when(prodottoDAO)
                .deleteProductById("P001");

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(responseBody.toString().contains("\"success\": false"));
    }
}
