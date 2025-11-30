package com.popx.persistenza;

import com.popx.modello.ClienteBean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*@ public invariant ds != null; @*/
public class ClienteDAOImpl implements UserDAO<ClienteBean> {
    private DataSource ds;

    /*@
      @ ensures this.ds != null;
      @*/
    public ClienteDAOImpl() {
        this.ds = DataSourceSingleton.getInstance();
    }

    /*@
      @ also
      @ public normal_behavior
      @ requires email != null && !email.isEmpty();
      @ ensures \result == null
      @      || \result.getEmail().equals(email);
      @ signals (SQLException) true;
      @*/
    @Override
    public ClienteBean getUserByEmail(String email) throws SQLException {
        String query = "SELECT * FROM UtenteRegistrato u " +
                "JOIN Cliente c ON u.email = c.utente_registrato_email " +
                "WHERE u.email = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ClienteBean(
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        }
        return null;
    }

    /*@
      @ also
      @ public normal_behavior
      @ requires user != null;
      @ requires user.getEmail() != null && !user.getEmail().isEmpty();
      @ ensures \result == true || \result == false;
      @ signals (SQLException) true;
      @*/
    @Override
    public boolean saveUser(ClienteBean user) throws SQLException {
        String userQuery = "INSERT INTO UtenteRegistrato (username, email, password, role) VALUES (?, ?, ?, ?)";
        String clienteQuery = "INSERT INTO Cliente (utente_registrato_email) VALUES (?)";

        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement userStmt = conn.prepareStatement(userQuery);
                 PreparedStatement clienteStmt = conn.prepareStatement(clienteQuery)) {

                // Inserimento in UtenteRegistrato
                userStmt.setString(1, user.getUsername());
                userStmt.setString(2, user.getEmail());
                userStmt.setString(3, com.popx.servizio.SecurityService.hashPassword(user.getPassword()));
                userStmt.setString(4, "Cliente");
                userStmt.executeUpdate();

                // Inserimento in Cliente
                clienteStmt.setString(1, user.getEmail());
                clienteStmt.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
