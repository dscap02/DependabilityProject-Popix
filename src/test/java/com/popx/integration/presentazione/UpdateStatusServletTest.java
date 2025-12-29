package com.popx.integration.presentazione;

import com.popx.modello.OrdineBean;
import com.popx.persistenza.OrdineDAO;
import com.popx.presentazione.UpdateStatusServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UpdateStatusServletTest {

    private OrdineDAO ordineDAO;
    private UpdateStatusServlet servlet;

    private HttpServletRequest request;
    private HttpServletResponse response;

    private StringWriter responseBody;
    private PrintWriter writer;

    @BeforeEach
    void setup() throws Exception {
        ordineDAO = mock(OrdineDAO.class);
        servlet = new UpdateStatusServlet(ordineDAO);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        responseBody = new StringWriter();
        writer = new PrintWriter(responseBody);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void updateStatus_success() throws Exception {
        OrdineBean ordine = new OrdineBean();
        ordine.setId(1);

        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("status")).thenReturn("SHIPPED");
        when(ordineDAO.getOrdineById(1)).thenReturn(ordine);
        when(ordineDAO.updateStatus(ordine)).thenReturn(true);

        servlet.doGet(request, response);

        verify(ordineDAO).updateStatus(ordine);
        assertTrue(responseBody.toString().contains("\"success\": true"));
    }

    @Test
    void updateStatus_missingParams_returns400() throws Exception {
        when(request.getParameter("id")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void updateStatus_orderNotFound_returns404() throws Exception {
        when(request.getParameter("id")).thenReturn("99");
        when(request.getParameter("status")).thenReturn("SHIPPED");
        when(ordineDAO.getOrdineById(99)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void updateStatus_exception_returns500() throws Exception {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("status")).thenReturn("SHIPPED");
        when(ordineDAO.getOrdineById(1))
                .thenThrow(new RuntimeException("DB error"));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
