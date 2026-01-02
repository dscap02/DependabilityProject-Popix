package com.popx.persistenza;

import com.popx.modello.GestoreOrdineBean;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;


public class GestoreOrdineDAOImpl implements UserDAO<GestoreOrdineBean> {

    private static final DataSource ds;

    static {
        try {
            Context ctx = new InitialContext();
            Context env = (Context) ctx.lookup("java:comp/env");
            ds = (DataSource) env.lookup("jdbc/Popix");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public GestoreOrdineBean getUserByEmail(String email) throws SQLException {
        String query = "SELECT * FROM UtenteRegistrato u " +
                "JOIN GestoreOrdine g ON u.email = g.utente_registrato_email " +
                "WHERE u.email = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new GestoreOrdineBean(
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        }
        return null;
    }


    @Override
    public boolean saveUser(GestoreOrdineBean user) throws SQLException {
        String userQuery = "INSERT INTO UtenteRegistrato (username, email, password, role) VALUES (?, ?, ?, ?)";
        String gestoreQuery = "INSERT INTO GestoreOrdine (utente_registrato_email) VALUES (?)";

        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement userStmt = conn.prepareStatement(userQuery);
                 PreparedStatement gestoreStmt = conn.prepareStatement(gestoreQuery)) {

                // Inserimento in UtenteRegistrato
                userStmt.setString(1, user.getUsername());
                userStmt.setString(2, user.getEmail());
                userStmt.setString(3, com.popx.servizio.SecurityService.hashPassword(user.getPassword()));
                userStmt.setString(4, "Gestore");
                userStmt.executeUpdate();

                // Inserimento in GestoreOrdine
                gestoreStmt.setString(1, user.getEmail());
                gestoreStmt.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
