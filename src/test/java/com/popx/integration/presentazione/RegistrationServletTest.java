package com.popx.integration.presentazione;

import com.popx.modello.UserBean;
import com.popx.presentazione.RegistrationServlet;
import com.popx.servizio.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationServletTest {

    private AuthenticationService authService;
    private RegistrationServlet servlet;

    private HttpServletRequest request;
    private HttpServletResponse response;

    private StringWriter responseWriter;

    @BeforeEach
    void setup() throws Exception {
        authService = mock(AuthenticationService.class);
        servlet = new RegistrationServlet(authService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        when(request.getContextPath()).thenReturn("/popx");
    }

    @Test
    void register_successful() throws Exception {
        when(request.getParameter("username")).thenReturn("mario");
        when(request.getParameter("email")).thenReturn("mario@test.it");
        when(request.getParameter("password")).thenReturn("password");

        when(authService.isEmailRegistered("mario@test.it")).thenReturn(false);
        when(authService.registerUser(any(UserBean.class))).thenReturn(true);

        servlet.doPost(request, response);

        String output = responseWriter.toString();
        assertTrue(output.contains("\"status\":\"success\""));
        assertTrue(output.contains("Registrazione avvenuta con successo"));

        ArgumentCaptor<UserBean> captor = ArgumentCaptor.forClass(UserBean.class);
        verify(authService).registerUser(captor.capture());

        UserBean savedUser = captor.getValue();
        assertEquals("mario", savedUser.getUsername());
        assertEquals("mario@test.it", savedUser.getEmail());
        assertEquals("User", savedUser.getRole());
    }

    @Test
    void register_emailAlreadyExists() throws Exception {
        when(request.getParameter("email")).thenReturn("existing@test.it");
        when(authService.isEmailRegistered("existing@test.it")).thenReturn(true);

        servlet.doPost(request, response);

        String output = responseWriter.toString();
        assertTrue(output.contains("\"status\":\"error\""));
        assertTrue(output.contains("Email già registrata"));

        verify(authService, never()).registerUser(any());
    }

    @Test
    void register_unknownFailure() throws Exception {
        when(request.getParameter("username")).thenReturn("luigi");
        when(request.getParameter("email")).thenReturn("luigi@test.it");
        when(request.getParameter("password")).thenReturn("pwd");

        when(authService.isEmailRegistered("luigi@test.it")).thenReturn(false);
        when(authService.registerUser(any())).thenReturn(false);

        servlet.doPost(request, response);

        String output = responseWriter.toString();
        assertTrue(output.contains("\"status\":\"error\""));
        assertTrue(output.contains("Errore sconosciuto"));
    }

    @Test
    void register_exceptionThrown() throws Exception {
        when(request.getParameter("email")).thenReturn("boom@test.it");
        when(authService.isEmailRegistered("boom@test.it"))
                .thenThrow(new RuntimeException("DB error"));

        servlet.doPost(request, response);

        String output = responseWriter.toString();
        assertTrue(output.contains("\"status\":\"error\""));
        assertTrue(output.contains("DB error"));
    }
}
