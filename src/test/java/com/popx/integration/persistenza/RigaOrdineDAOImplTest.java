package com.popx.integration.persistenza;

import com.popx.modello.RigaOrdineBean;
import com.popx.persistenza.DataSourceSingleton;
import com.popx.persistenza.RigaOrdineDAOImpl;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RigaOrdineDAOImplTest {

    private static DataSource ds;
    private RigaOrdineDAOImpl dao;

    @BeforeAll
    static void setupDatabase() throws Exception {
        JdbcDataSource h2 = new JdbcDataSource();
        h2.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        h2.setUser("sa");
        h2.setPassword("");
        ds = h2;

        DataSourceSingleton.setInstanceForTest(ds);

        try (Connection con = ds.getConnection();
             Statement st = con.createStatement()) {

            st.execute("""
                CREATE TABLE RigaOrdine (
                    ordine_id INT,
                    prodotto_id VARCHAR(5),
                    quantity INT NOT NULL,
                    unitary_cost FLOAT NOT NULL,
                    PRIMARY KEY (ordine_id, prodotto_id)
                )
            """);
        }
    }

    @BeforeEach
    void init() {
        dao = new RigaOrdineDAOImpl();
    }

    @Test
    void addAndGetRigaOrdine_success() {
        RigaOrdineBean riga = new RigaOrdineBean();
        riga.setOrdineId(1);
        riga.setProdottoId("P001");
        riga.setQuantity(2);
        riga.setUnitaryCost(10.0f);

        dao.addRigaOrdine(riga);

        List<RigaOrdineBean> result = dao.getRigheOrdineByOrdineId(1);

        assertEquals(1, result.size());
        assertEquals("P001", result.get(0).getProdottoId());
        assertEquals(2, result.get(0).getQuantity());
        assertEquals(10.0f, result.get(0).getUnitaryCost());
    }

    @Test
    void updateRigaOrdine_updatesValues() {
        RigaOrdineBean riga = new RigaOrdineBean();
        riga.setOrdineId(2);
        riga.setProdottoId("P002");
        riga.setQuantity(1);
        riga.setUnitaryCost(5.0f);
        dao.addRigaOrdine(riga);

        riga.setQuantity(4);
        riga.setUnitaryCost(7.5f);
        dao.updateRigaOrdine(riga);

        List<RigaOrdineBean> result = dao.getRigheOrdineByOrdineId(2);
        assertEquals(1, result.size());
        assertEquals(4, result.get(0).getQuantity());
        assertEquals(7.5f, result.get(0).getUnitaryCost());
    }

    @Test
    void deleteRigaOrdine_removesRow() {
        RigaOrdineBean riga = new RigaOrdineBean();
        riga.setOrdineId(3);
        riga.setProdottoId("P003");
        riga.setQuantity(1);
        riga.setUnitaryCost(3.0f);
        dao.addRigaOrdine(riga);

        dao.deleteRigaOrdine(3, "P003");

        List<RigaOrdineBean> result = dao.getRigheOrdineByOrdineId(3);
        assertTrue(result.isEmpty());
    }
}
