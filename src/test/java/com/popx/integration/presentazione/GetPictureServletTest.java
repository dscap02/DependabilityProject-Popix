package com.popx.integration.presentazione;

import com.popx.persistenza.ProdottoDAO;
import com.popx.presentazione.GetPictureServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GetPictureServletTest {

    private ProdottoDAO prodottoDAO;
    private GetPictureServlet servlet;

    private HttpServletRequest request;
    private HttpServletResponse response;

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setup() throws Exception {
        prodottoDAO = mock(ProdottoDAO.class);
        servlet = new GetPictureServlet(prodottoDAO);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        outputStream = new ByteArrayOutputStream();

        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                outputStream.write(b);
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(javax.servlet.WriteListener writeListener) {
                // no-op per test
            }
        };


        when(response.getOutputStream()).thenReturn(servletOutputStream);
    }

    @Test
    void getPicture_success() throws Exception {
        byte[] image = new byte[]{1, 2, 3, 4};

        when(request.getParameter("id")).thenReturn("P001");
        when(prodottoDAO.getProductImageById("P001")).thenReturn(image);

        servlet.doGet(request, response);

        verify(response).setContentType("image/jpeg");
        assertArrayEquals(image, outputStream.toByteArray());
    }

    @Test
    void getPicture_missingId_returns400() throws Exception {
        when(request.getParameter("id")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "ID prodotto mancante."
        );
    }

    @Test
    void getPicture_notFound_returns404() throws Exception {
        when(request.getParameter("id")).thenReturn("P404");
        when(prodottoDAO.getProductImageById("P404")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_NOT_FOUND,
                "Immagine non trovata."
        );
    }

    @Test
    void getPicture_exception_returns500() throws Exception {
        when(request.getParameter("id")).thenReturn("PERR");
        when(prodottoDAO.getProductImageById("PERR"))
                .thenThrow(new RuntimeException("DB error"));

        servlet.doGet(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Errore durante il recupero dell'immagine."
        );
    }
}
