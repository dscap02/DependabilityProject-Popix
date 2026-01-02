package com.popx.persistenza;

import com.popx.modello.AdminBean;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class AdminDAOImpl implements UserDAO<AdminBean> {

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
    public AdminBean getUserByEmail(String email) throws SQLException {
        String query = "SELECT * FROM UtenteRegistrato u " +
                "JOIN Admin a ON u.email = a.utente_registrato_email " +
                "WHERE u.email = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new AdminBean(
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
    public boolean saveUser(AdminBean user) throws SQLException {
        String userQuery = "INSERT INTO UtenteRegistrato (username, email, password, role) VALUES (?, ?, ?, ?)";
        String adminQuery = "INSERT INTO Admin (utente_registrato_email) VALUES (?)";

        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement userStmt = conn.prepareStatement(userQuery);
                 PreparedStatement adminStmt = conn.prepareStatement(adminQuery)) {

                userStmt.setString(1, user.getUsername());
                userStmt.setString(2, user.getEmail());
                userStmt.setString(3, com.popx.servizio.SecurityService.hashPassword(user.getPassword()));
                userStmt.setString(4, "Admin");
                userStmt.executeUpdate();

                adminStmt.setString(1, user.getEmail());
                adminStmt.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
