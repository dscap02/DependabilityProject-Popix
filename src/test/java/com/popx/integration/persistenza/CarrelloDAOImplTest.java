package com.popx.integration.persistenza;

import com.popx.modello.CarrelloBean;
import com.popx.modello.ProdottoCarrelloBean;
import com.popx.persistenza.CarrelloDAOImpl;
import com.popx.persistenza.DataSourceSingleton;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarrelloDAOImplTest {

    private static DataSource ds;
    private CarrelloDAOImpl carrelloDAO;

    @BeforeAll
    static void setupDatabase() throws Exception {
        JdbcDataSource h2 = new JdbcDataSource();
        h2.setURL("jdbc:h2:mem:carrello_db;MODE=MySQL;DB_CLOSE_DELAY=-1");
        h2.setUser("sa");
        h2.setPassword("");
        ds = h2;

        DataSourceSingleton.setInstanceForTest(ds);

        try (Connection con = ds.getConnection();
             Statement st = con.createStatement()) {

            // ✅ pulizia: evita conflitti se DB resta in memoria nella JVM
            st.execute("DROP TABLE IF EXISTS ProdottoCarrello");
            st.execute("DROP TABLE IF EXISTS Carrello");
            st.execute("DROP TABLE IF EXISTS Cliente");
            st.execute("DROP TABLE IF EXISTS UtenteRegistrato");

            // (se ti serve davvero UtenteRegistrato per FK o seed)
            st.execute("""
            CREATE TABLE UtenteRegistrato (
                username VARCHAR(50),
                password VARCHAR(255),
                email VARCHAR(100) PRIMARY KEY,
                role VARCHAR(20)
            );
        """);

            st.execute("""
            CREATE TABLE Cliente (
                utente_registrato_email VARCHAR(100) PRIMARY KEY,
                FOREIGN KEY (utente_registrato_email) REFERENCES UtenteRegistrato(email)
            );
        """);

            st.execute("""
            CREATE TABLE Carrello (
                id INT AUTO_INCREMENT PRIMARY KEY,
                cliente_email VARCHAR(100) UNIQUE,
                FOREIGN KEY (cliente_email) REFERENCES Cliente(utente_registrato_email)
            );
        """);

            st.execute("""
            CREATE TABLE ProdottoCarrello (
                carrello_id INT,
                prodotto_id VARCHAR(5),
                quantity INT NOT NULL,
                unitary_cost FLOAT NOT NULL,
                PRIMARY KEY (carrello_id, prodotto_id),
                FOREIGN KEY (carrello_id) REFERENCES Carrello(id)
            );
        """);

            // seed minimo coerente con FK
            st.execute("""
            INSERT INTO UtenteRegistrato (username, password, email, role)
            VALUES ('Mario', 'pwd', 'mario@test.it', 'User');
        """);
            st.execute("""
            INSERT INTO Cliente (utente_registrato_email) VALUES ('mario@test.it');
        """);
        }
    }


    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        carrelloDAO = new CarrelloDAOImpl() {
            {
                // override del DataSource singleton
                java.lang.reflect.Field f =
                        CarrelloDAOImpl.class.getDeclaredField("ds");
                f.setAccessible(true);
                f.set(this, ds);
            }
        };
    }

    @Test
    void salvaCarrello_e_ottieniCarrelloPerEmail_funzionano() {
        ProdottoCarrelloBean prodotto = new ProdottoCarrelloBean(
                "mario@test.it",
                "P001",
                2,
                9.99f
        );

        CarrelloBean carrello = new CarrelloBean(
                "mario@test.it",
                List.of(prodotto)
        );

        // salva
        carrelloDAO.salvaCarrello(carrello);

        // recupera
        CarrelloBean risultato = carrelloDAO.ottieniCarrelloPerEmail("mario@test.it");

        assertNotNull(risultato);
        assertEquals("mario@test.it", risultato.getClienteEmail());
        assertEquals(1, risultato.getProdottiCarrello().size());

        ProdottoCarrelloBean p = risultato.getProdottiCarrello().get(0);
        assertEquals("P001", p.getProdottoId());
        assertEquals(2, p.getQuantity());
        assertEquals(9.99f, p.getUnitaryCost());
    }

    @Test
    void clearCartByUserEmail_rimuove_carrello_e_prodotti() {
        // precondizione
        salvaCarrello_e_ottieniCarrelloPerEmail_funzionano();

        // clear
        carrelloDAO.clearCartByUserEmail("mario@test.it");

        CarrelloBean carrello = carrelloDAO.ottieniCarrelloPerEmail("mario@test.it");

        assertNotNull(carrello);
        assertTrue(carrello.getProdottiCarrello().isEmpty());
    }
}
