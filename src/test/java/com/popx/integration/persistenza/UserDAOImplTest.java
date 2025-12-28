package com.popx.integration.persistenza;

import com.popx.modello.UserBean;
import com.popx.persistenza.DataSourceSingleton;
import com.popx.persistenza.UserDAOImpl;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOImplTest {

    private UserDAOImpl userDAO;

    @BeforeEach
    void setupDatabase() throws Exception {
        // --- H2 in-memory DataSource (MySQL compatibility) ---
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");

        // Inietta il DataSource di test
        DataSourceSingleton.setInstanceForTest(ds);

        // --- Schema MINIMO necessario per UserDAOImpl ---
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS UtenteRegistrato (
                    username VARCHAR(50) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(100) PRIMARY KEY,
                    role VARCHAR(20) NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Cliente (
                    utente_registrato_email VARCHAR(100) PRIMARY KEY,
                    FOREIGN KEY (utente_registrato_email)
                        REFERENCES UtenteRegistrato(email)
                )
            """);

            // Pulizia dati per isolamento tra test
            stmt.execute("DELETE FROM Cliente");
            stmt.execute("DELETE FROM UtenteRegistrato");
        }

        userDAO = new UserDAOImpl();
    }

    // ------------------------------------------------------------------
    // TESTS
    // ------------------------------------------------------------------

    @Test
    void saveUser_success_and_getUserByEmail_returnsUser() throws Exception {
        UserBean user = new UserBean(
                "Mario",
                "mario@example.com",
                "password",
                "User"
        );

        boolean saved = userDAO.saveUser(user);
        assertTrue(saved);

        UserBean loaded = userDAO.getUserByEmail("mario@example.com");

        assertNotNull(loaded);
        assertEquals("Mario", loaded.getUsername());
        assertEquals("mario@example.com", loaded.getEmail());
        assertEquals("User", loaded.getRole());
        assertNotNull(loaded.getPassword());
    }

    @Test
    void getUserByEmail_userDoesNotExist_returnsNull() throws Exception {
        UserBean result = userDAO.getUserByEmail("missing@example.com");
        assertNull(result);
    }

    @Test
    void saveUser_duplicateEmail_throwsSQLException() throws Exception {
        UserBean user1 = new UserBean(
                "Mario",
                "dup@example.com",
                "password",
                "User"
        );

        UserBean user2 = new UserBean(
                "Luigi",
                "dup@example.com", // stessa email
                "password",
                "User"
        );

        assertTrue(userDAO.saveUser(user1));

        assertThrows(
                Exception.class,
                () -> userDAO.saveUser(user2)
        );
    }
}
