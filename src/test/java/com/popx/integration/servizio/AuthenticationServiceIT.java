package com.popx.integration.servizio;

import com.popx.modello.UserBean;
import com.popx.persistenza.DataSourceSingleton;
import com.popx.servizio.AuthenticationService;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceIT {

    private AuthenticationService authService;

    @BeforeEach
    void setupDatabase() throws Exception {

        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");

        DataSourceSingleton.setInstanceForTest(ds);

        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
            CREATE TABLE UtenteRegistrato (
                username VARCHAR(50),
                email VARCHAR(100) PRIMARY KEY,
                password VARCHAR(255),
                role VARCHAR(20)
            )
        """);

            stmt.execute("""
            CREATE TABLE Cliente (
                utente_registrato_email VARCHAR(100) PRIMARY KEY,
                FOREIGN KEY (utente_registrato_email)
                    REFERENCES UtenteRegistrato(email)
            )
        """);
        }

        authService = new AuthenticationService();
    }


    @AfterEach
    void cleanup() throws Exception {
        try (Connection conn = DataSourceSingleton.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE Cliente");
            stmt.execute("DROP TABLE UtenteRegistrato");
        }
    }


    // ---------- REGISTER + LOGIN FLOW ----------

    @Test
    void registerAndLogin_successful() throws Exception {

        UserBean user = new UserBean(
                "Mario",
                "mario@example.com",
                "password123",
                "User"
        );

        // Register
        boolean registered = authService.registerUser(user);
        assertTrue(registered);

        // Login
        UserBean logged = authService.login("mario@example.com", "password123");

        assertNotNull(logged);
        assertEquals("mario@example.com", logged.getEmail());
        assertEquals("User", logged.getRole());
    }

    @Test
    void login_wrongPassword_fails() throws Exception {

        UserBean user = new UserBean(
                "Luigi",
                "luigi@example.com",
                "correctPwd",
                "User"
        );

        authService.registerUser(user);

        Exception ex = assertThrows(
                Exception.class,
                () -> authService.login("luigi@example.com", "wrongPwd")
        );

        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void register_existingUser_fails() throws Exception {

        UserBean user = new UserBean(
                "Peach",
                "peach@example.com",
                "pwd",
                "User"
        );

        authService.registerUser(user);

        Exception ex = assertThrows(
                Exception.class,
                () -> authService.registerUser(user)
        );

        assertEquals("User already exists", ex.getMessage());
    }
}
