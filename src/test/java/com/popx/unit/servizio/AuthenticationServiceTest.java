package com.popx.unit.servizio;

import com.popx.modello.UserBean;
import com.popx.persistenza.UserDAO;
import com.popx.servizio.AuthenticationService;
import com.popx.servizio.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private AuthenticationService authService;
    private UserDAO userDAOMock;

    @BeforeEach
    void setup() {
        userDAOMock = mock(UserDAO.class);
        authService = new AuthenticationService(userDAOMock);
    }

    // ---------- LOGIN ----------

    @Test
    void login_successful() throws Exception {
        UserBean user = new UserBean("Mario", "test@example.com", "hashed", "User");
        when(userDAOMock.getUserByEmail("test@example.com")).thenReturn(user);

        try (MockedStatic<SecurityService> securityMock =
                     mockStatic(SecurityService.class)) {

            securityMock
                    .when(() -> SecurityService.checkPassword("password", "hashed"))
                    .thenReturn(true);

            UserBean result = authService.login("test@example.com", "password");

            assertNotNull(result);
            assertEquals("test@example.com", result.getEmail());
        }
    }

    @Test
    void login_wrongPassword_throwsException() throws Exception {
        UserBean user = new UserBean("Mario", "test@example.com", "hashed", "User");
        when(userDAOMock.getUserByEmail("test@example.com")).thenReturn(user);

        try (MockedStatic<SecurityService> securityMock =
                     mockStatic(SecurityService.class)) {

            securityMock
                    .when(() -> SecurityService.checkPassword("wrong", "hashed"))
                    .thenReturn(false);

            Exception ex = assertThrows(
                    Exception.class,
                    () -> authService.login("test@example.com", "wrong")
            );

            assertEquals("Invalid credentials", ex.getMessage());
        }
    }

    @Test
    void login_userNotFound_throwsException() throws Exception {
        when(userDAOMock.getUserByEmail("missing@example.com")).thenReturn(null);

        Exception ex = assertThrows(
                Exception.class,
                () -> authService.login("missing@example.com", "password")
        );

        assertEquals("Invalid credentials", ex.getMessage());
    }

    // ---------- REGISTER ----------

    @Test
    void registerUser_successful() throws Exception {
        UserBean user = new UserBean("Mario", "new@example.com", "pwd", "User");

        when(userDAOMock.getUserByEmail("new@example.com")).thenReturn(null);
        when(userDAOMock.saveUser(user)).thenReturn(true);

        boolean result = authService.registerUser(user);

        assertTrue(result);
        verify(userDAOMock).saveUser(user);
    }

    @Test
    void registerUser_userAlreadyExists_throwsException() throws Exception {
        UserBean user = new UserBean("Mario", "test@example.com", "pwd", "User");

        when(userDAOMock.getUserByEmail("test@example.com")).thenReturn(user);

        Exception ex = assertThrows(
                Exception.class,
                () -> authService.registerUser(user)
        );

        assertEquals("User already exists", ex.getMessage());
        verify(userDAOMock, never()).saveUser(any());
    }
    @Test
    void isEmailRegistered_userExists_returnsTrue() throws Exception {
        UserBean user = new UserBean("Mario", "test@example.com", "pwd", "User");
        when(userDAOMock.getUserByEmail("test@example.com")).thenReturn(user);

        boolean result = authService.isEmailRegistered("test@example.com");

        assertTrue(result);
    }

    @Test
    void isEmailRegistered_userDoesNotExist_returnsFalse() throws Exception {
        when(userDAOMock.getUserByEmail("missing@example.com")).thenReturn(null);

        boolean result = authService.isEmailRegistered("missing@example.com");

        assertFalse(result);
    }

    @Test
    void retrieveRole_userExists_returnsRole() throws Exception {
        UserBean user = new UserBean("Mario", "test@example.com", "pwd", "Admin");
        when(userDAOMock.getUserByEmail("test@example.com")).thenReturn(user);

        String role = authService.retrieveRole("test@example.com");

        assertEquals("Admin", role);
    }

}
