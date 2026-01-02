package com.popx.persistenza;

import com.popx.modello.CarrelloBean;
import com.popx.modello.ProdottoCarrelloBean;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CarrelloDAOImpl implements CarrelloDAO {
    private DataSource ds;
    private static final Logger LOGGER = Logger.getLogger(CarrelloDAOImpl.class.getName());


    public CarrelloDAOImpl() {
        this.ds = DataSourceSingleton.getInstance();
    }


    @Override
    public void salvaCarrello(CarrelloBean carrello) {
        String upsertCart =
                "INSERT INTO Carrello (cliente_email) VALUES (?) " +
                        "ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id)";

        String insertProdottoCarrello =
                "INSERT INTO ProdottoCarrello (carrello_id, prodotto_id, quantity, unitary_cost) " +
                        "VALUES (?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE quantity = VALUES(quantity), unitary_cost = VALUES(unitary_cost)";

        try (Connection connection = ds.getConnection()) {
            connection.setAutoCommit(false);

            int carrelloId;

            // 1) Inserisci (o recupera) il carrello e ottieni l'id
            try (PreparedStatement psCart = connection.prepareStatement(upsertCart, Statement.RETURN_GENERATED_KEYS)) {
                psCart.setString(1, carrello.getClienteEmail());
                psCart.executeUpdate();

                try (ResultSet keys = psCart.getGeneratedKeys()) {
                    if (keys.next()) {
                        carrelloId = keys.getInt(1);
                    } else {
                        throw new SQLException("Impossibile ottenere l'ID del carrello.");
                    }
                }
            }

            // 2) Inserisci/aggiorna prodotti del carrello (batch)
            try (PreparedStatement psProd = connection.prepareStatement(insertProdottoCarrello)) {
                for (ProdottoCarrelloBean prodotto : carrello.getProdottiCarrello()) {
                    psProd.setInt(1, carrelloId);
                    psProd.setString(2, prodotto.getProdottoId());
                    psProd.setInt(3, prodotto.getQuantity());
                    psProd.setFloat(4, prodotto.getUnitaryCost());
                    psProd.addBatch();
                }
                psProd.executeBatch();
            }

            connection.commit();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error in salvaCarrello", e);
        }
    }


    @Override
    public CarrelloBean ottieniCarrelloPerEmail(String email) {
        String queryCarrello = "SELECT * FROM Carrello WHERE cliente_email = ?";
        String queryProdotti = "SELECT * FROM ProdottoCarrello WHERE carrello_id = ?";

        CarrelloBean carrello = null;
        List<ProdottoCarrelloBean> prodottiCarrello = new ArrayList<>();

        try (Connection connection = ds.getConnection()) {
            // Recupera il carrello
            try (PreparedStatement psCarrello = connection.prepareStatement(queryCarrello)) {
                psCarrello.setString(1, email);
                ResultSet rsCarrello = psCarrello.executeQuery();

                if (rsCarrello.next()) {
                    // Recupera l'ID del carrello
                    int carrelloId = rsCarrello.getInt("id");

                    // Recupera i prodotti associati al carrello
                    try (PreparedStatement psProdotti = connection.prepareStatement(queryProdotti)) {
                        psProdotti.setInt(1, carrelloId);
                        ResultSet rsProdotti = psProdotti.executeQuery();

                        while (rsProdotti.next()) {
                            ProdottoCarrelloBean prodotto = new ProdottoCarrelloBean(
                                    email,
                                    rsProdotti.getString("prodotto_id"),
                                    rsProdotti.getInt("quantity"),
                                    rsProdotti.getFloat("unitary_cost")
                            );
                            prodottiCarrello.add(prodotto);
                        }
                    }

                    // Crea il carrello
                    carrello = new CarrelloBean(email, prodottiCarrello);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error in ottieniCarrelloPerEmail", e);
        }

        return carrello != null ? carrello : new CarrelloBean(email, prodottiCarrello);
    }

    @Override
    public void clearCartByUserEmail(String email) {
        String queryProdottoCarrello =
                "DELETE FROM ProdottoCarrello WHERE carrello_id = (SELECT id FROM Carrello WHERE cliente_email = ?)";
        String queryCarrello =
                "DELETE FROM Carrello WHERE cliente_email = ?";

        try (Connection connection = ds.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement psProdottoCarrello = connection.prepareStatement(queryProdottoCarrello);
                 PreparedStatement psCarrello = connection.prepareStatement(queryCarrello)) {

                psProdottoCarrello.setString(1, email);
                psProdottoCarrello.executeUpdate();

                psCarrello.setString(1, email);
                psCarrello.executeUpdate();

                connection.commit();
            } catch (SQLException ex) {
                // se qualcosa va male durante i DELETE, rollback della transazione
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    LOGGER.log(Level.SEVERE, "Error during rollback in clearCartByUserEmail", rollbackEx);
                }
                throw ex; // rilancia per finire nel catch esterno
            } finally {
                // best-effort: ripristina autocommit prima della chiusura
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ignore) {
                    // ignore / log se preferisci
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error in clearCartByUserEmail", e);
        }
    }
}
