package com.popx.integration.presentazione;

import com.popx.presentazione.LoginServlet;
import com.popx.servizio.AuthenticationService;
import com.popx.modello.UserBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServletTest {

    private AuthenticationService authServiceMock;
    private LoginServlet servlet;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    @BeforeEach
    void setup() {
        authServiceMock = mock(AuthenticationService.class);
        servlet = new LoginServlet(authServiceMock);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @Test
    void login_success_setsSessionAndReturns200() throws Exception {
        // Arrange
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(request.getParameter("password")).thenReturn("password");
        when(request.getSession(true)).thenReturn(session);

        UserBean user = new UserBean(
                "Mario",
                "test@example.com",
                "hashed",
                "User"
        );

        when(authServiceMock.login("test@example.com", "password"))
                .thenReturn(user);

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(session).setAttribute("role", "User");
        verify(session).setAttribute("userEmail", "test@example.com");
    }

    @Test
    void login_failure_returns401AndErrorMessage() throws Exception {
        // Arrange
        when(request.getParameter("email")).thenReturn("wrong@example.com");
        when(request.getParameter("password")).thenReturn("wrong");

        when(authServiceMock.login(anyString(), anyString()))
                .thenThrow(new Exception("Invalid credentials"));

        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(body.toString().contains("Sbagliato email o password"));
    }
}
