package com.popx.integration.persistenza;

import com.popx.modello.ProdottoBean;
import com.popx.persistenza.DataSourceSingleton;
import com.popx.persistenza.ProdottoDAOImpl;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProdottoDAOImplTest {

    private ProdottoDAOImpl prodottoDAO;

    @BeforeEach
    void setupDatabase() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");

        DataSourceSingleton.setInstanceForTest(ds);

        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Prodotto (
                    id VARCHAR(5) PRIMARY KEY,
                    name VARCHAR(100),
                    description TEXT,
                    cost FLOAT NOT NULL,
                    pieces_in_stock INT,
                    img BLOB,
                    brand VARCHAR(100),
                    figure VARCHAR(100)
                )
            """);

            // isolamento tra test
            stmt.execute("DELETE FROM Prodotto");
        }

        prodottoDAO = new ProdottoDAOImpl();
    }

    // --------------------------------------------------
    // TESTS
    // --------------------------------------------------

    @Test
    void saveProdotto_and_getProdottoById_returnsProduct() throws Exception {
        ProdottoBean prodotto = new ProdottoBean(
                "P001",
                "Test Product",
                "Description",
                19.99,
                10,
                "BrandX",
                null,
                "FigureA"
        );

        assertTrue(prodottoDAO.saveProdotto(prodotto));

        ProdottoBean loaded = prodottoDAO.getProdottoById("P001");

        assertNotNull(loaded);
        assertEquals("P001", loaded.getId());
        assertEquals("Test Product", loaded.getName());
        assertEquals(10, loaded.getPiecesInStock());
    }

    @Test
    void getProdottoById_notExisting_returnsNull() throws Exception {
        ProdottoBean prodotto = prodottoDAO.getProdottoById("UNKNOWN");
        assertNull(prodotto);
    }

    @Test
    void getAllProdotti_returnsAllInsertedProducts() throws Exception {
        ProdottoBean p1 = new ProdottoBean(
                "P001", "Prod1", "Desc1", 10.0, 5, "Brand1", null, "Fig1"
        );
        ProdottoBean p2 = new ProdottoBean(
                "P002", "Prod2", "Desc2", 20.0, 3, "Brand2", null, "Fig2"
        );

        assertTrue(prodottoDAO.saveProdotto(p1));
        assertTrue(prodottoDAO.saveProdotto(p2));

        List<ProdottoBean> prodotti = prodottoDAO.getAllProducts();

        assertNotNull(prodotti);
        assertEquals(2, prodotti.size());
    }

    @Test
    void updateProductStock_updatesCorrectly() throws Exception {
        ProdottoBean prodotto = new ProdottoBean(
                "P001",
                "Stock Product",
                "Desc",
                9.99,
                10,
                "Brand",
                null,
                "Fig"
        );

        assertTrue(prodottoDAO.saveProdotto(prodotto));

        prodottoDAO.updateStock("P001", 5);

        ProdottoBean updated = prodottoDAO.getProdottoById("P001");

        assertNotNull(updated);
        assertEquals(5, updated.getPiecesInStock());
    }
}
