package com.popx.persistenza;

import com.popx.modello.UserBean;

import javax.sql.DataSource;
import java.sql.*;

public class UserDAOImpl implements UserDAO<UserBean> {

    private DataSource ds;

    /*@
      @ ensures this.ds != null;
      @*/
    public UserDAOImpl() {
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
    public UserBean getUserByEmail(String email) throws SQLException {
        String query = "SELECT * FROM UtenteRegistrato WHERE email = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new UserBean(
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
    public boolean saveUser(UserBean user) throws SQLException {
        String userQuery = "INSERT INTO UtenteRegistrato (username, email, password, role) VALUES (?, ?, ?, ?)";
        String clienteQuery = "INSERT INTO Cliente (utente_registrato_email) VALUES (?)";

        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement userStmt = conn.prepareStatement(userQuery);
                 PreparedStatement clienteStmt = conn.prepareStatement(clienteQuery)) {

                userStmt.setString(1, user.getUsername());
                userStmt.setString(2, user.getEmail());
                userStmt.setString(3, com.popx.servizio.SecurityService.hashPassword(user.getPassword()));
                userStmt.setString(4, "User");
                userStmt.executeUpdate();

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