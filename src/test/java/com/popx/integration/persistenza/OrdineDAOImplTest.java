package com.popx.integration.persistenza;

import com.popx.modello.OrdineBean;
import com.popx.persistenza.DataSourceSingleton;
import com.popx.persistenza.OrdineDAOImpl;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrdineDAOImplTest {

    private OrdineDAOImpl ordineDAO;

    @BeforeEach
    void setupDatabase() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb_ordini;MODE=MySQL;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");

        // Inietta H2 nel singleton (come per gli altri integration test)
        DataSourceSingleton.setInstanceForTest(ds);

        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Ordine (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    subtotal FLOAT NOT NULL,
                    customer_email VARCHAR(100),
                    status VARCHAR(20),
                    data_ordine DATE
                )
            """);

            // isolamento: pulisci prima di ogni test
            stmt.execute("DELETE FROM Ordine");
        }

        ordineDAO = new OrdineDAOImpl();
    }

    // ------------------------------------------------------------
    // TESTS (CORE DAO)
    // ------------------------------------------------------------

    @Test
    void insertOrdine_persistsAndGeneratesId() {
        OrdineBean ordine = new OrdineBean();
        ordine.setSubtotal(99.99f);
        ordine.setCustomerEmail("cliente@example.com");
        ordine.setStatus("IN_CORSO");
        ordine.setDataOrdine(new Date(System.currentTimeMillis()));

        boolean inserted = ordineDAO.insertOrdine(ordine);

        assertTrue(inserted, "Inserimento ordine dovrebbe riuscire");
        assertTrue(ordine.getId() > 0, "L'ID dovrebbe essere auto-generato e > 0");
    }

    @Test
    void getOrdineById_existingOrder_returnsOrder() {
        OrdineBean ordine = new OrdineBean();
        ordine.setSubtotal(50.0f);
        ordine.setCustomerEmail("cliente@example.com");
        ordine.setStatus("IN_CORSO");
        ordine.setDataOrdine(new Date(System.currentTimeMillis()));

        assertTrue(ordineDAO.insertOrdine(ordine));
        int id = ordine.getId();

        OrdineBean loaded = ordineDAO.getOrdineById(id);

        assertNotNull(loaded);
        assertEquals(id, loaded.getId());
        assertEquals("cliente@example.com", loaded.getCustomerEmail());
        assertEquals("IN_CORSO", loaded.getStatus());
        assertEquals(50.0f, loaded.getSubtotal(), 0.0001f);
        assertNotNull(loaded.getDataOrdine());
    }

    @Test
    void getOrdineById_notExisting_returnsNull() {
        OrdineBean loaded = ordineDAO.getOrdineById(99999);
        assertNull(loaded);
    }

    @Test
    void getOrdiniByCliente_returnsOnlyThatClientOrders() {
        Date today = new Date(System.currentTimeMillis());

        // 2 ordini per lo stesso cliente
        OrdineBean o1 = new OrdineBean();
        o1.setSubtotal(10.0f);
        o1.setCustomerEmail("a@example.com");
        o1.setStatus("IN_CORSO");
        o1.setDataOrdine(today);

        OrdineBean o2 = new OrdineBean();
        o2.setSubtotal(20.0f);
        o2.setCustomerEmail("a@example.com");
        o2.setStatus("COMPLETATO");
        o2.setDataOrdine(today);

        // 1 ordine di altro cliente
        OrdineBean o3 = new OrdineBean();
        o3.setSubtotal(30.0f);
        o3.setCustomerEmail("b@example.com");
        o3.setStatus("IN_CORSO");
        o3.setDataOrdine(today);

        assertTrue(ordineDAO.insertOrdine(o1));
        assertTrue(ordineDAO.insertOrdine(o2));
        assertTrue(ordineDAO.insertOrdine(o3));

        List<OrdineBean> ordiniA = ordineDAO.getOrdiniByCliente("a@example.com");

        assertNotNull(ordiniA);
        assertEquals(2, ordiniA.size());
        assertTrue(ordiniA.stream().allMatch(o -> "a@example.com".equals(o.getCustomerEmail())));
    }

    @Test
    void getAllOrdini_returnsAllInsertedOrders() {
        Date today = new Date(System.currentTimeMillis());

        OrdineBean o1 = new OrdineBean();
        o1.setSubtotal(10.0f);
        o1.setCustomerEmail("a@example.com");
        o1.setStatus("IN_CORSO");
        o1.setDataOrdine(today);

        OrdineBean o2 = new OrdineBean();
        o2.setSubtotal(20.0f);
        o2.setCustomerEmail("b@example.com");
        o2.setStatus("COMPLETATO");
        o2.setDataOrdine(today);

        assertTrue(ordineDAO.insertOrdine(o1));
        assertTrue(ordineDAO.insertOrdine(o2));

        List<OrdineBean> all = ordineDAO.getAllOrdini();

        assertNotNull(all);
        assertEquals(2, all.size());

        // verifica contenuto reale
        OrdineBean first = all.get(0);

        assertTrue(first.getId() > 0);
        assertNotNull(first.getCustomerEmail());
        assertNotNull(first.getStatus());
        assertNotNull(first.getDataOrdine());
        assertTrue(first.getSubtotal() > 0);
    }

    @Test
    void updateStatus_updatesCorrectly() {
        OrdineBean ordine = new OrdineBean();
        ordine.setSubtotal(42.0f);
        ordine.setCustomerEmail("cliente@example.com");
        ordine.setStatus("IN_CORSO");
        ordine.setDataOrdine(new Date(System.currentTimeMillis()));

        assertTrue(ordineDAO.insertOrdine(ordine));
        int id = ordine.getId();

        // cambia stato
        OrdineBean update = new OrdineBean();
        update.setId(id);
        update.setStatus("COMPLETATO");

        boolean updated = ordineDAO.updateStatus(update);
        assertTrue(updated);

        OrdineBean loaded = ordineDAO.getOrdineById(id);
        assertNotNull(loaded);
        assertEquals("COMPLETATO", loaded.getStatus());
    }
}
