package com.popx.persistenza;

import com.popx.modello.RigaOrdineBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RigaOrdineDAOImpl implements RigaOrdineDAO {

    private DataSource ds;
    private static final Logger LOGGER = Logger.getLogger(RigaOrdineDAOImpl.class.getName());



    public RigaOrdineDAOImpl() {
        this.ds = DataSourceSingleton.getInstance();
    }

    @Override
    public void addRigaOrdine(RigaOrdineBean rigaOrdine) {
        String query = "INSERT INTO RigaOrdine (ordine_id, prodotto_id, quantity, unitary_cost) VALUES (?, ?, ?, ?)";

        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, rigaOrdine.getOrdineId());
            ps.setString(2, rigaOrdine.getProdottoId());
            ps.setInt(3, rigaOrdine.getQuantity());
            ps.setFloat(4, rigaOrdine.getUnitaryCost());

            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error in addRigaOrdine", e);
        }
    }

    @Override

    public List<RigaOrdineBean> getRigheOrdineByOrdineId(int ordineId) {
        String query = "SELECT * FROM RigaOrdine WHERE ordine_id = ?";
        List<RigaOrdineBean> righeOrdine = new ArrayList<>();

        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, ordineId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                RigaOrdineBean rigaOrdine = new RigaOrdineBean();
                rigaOrdine.setOrdineId(rs.getInt("ordine_id"));
                rigaOrdine.setProdottoId(rs.getString("prodotto_id"));
                rigaOrdine.setQuantity(rs.getInt("quantity"));
                rigaOrdine.setUnitaryCost(rs.getFloat("unitary_cost"));
                righeOrdine.add(rigaOrdine);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error in getRigheOrdineByOrdineId", e);
        }

        return righeOrdine;
    }

    @Override
    public void updateRigaOrdine(RigaOrdineBean rigaOrdine) {
        String query = "UPDATE RigaOrdine SET quantity = ?, unitary_cost = ? WHERE ordine_id = ? AND prodotto_id = ?";

        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, rigaOrdine.getQuantity());
            ps.setFloat(2, rigaOrdine.getUnitaryCost());
            ps.setInt(3, rigaOrdine.getOrdineId());
            ps.setString(4, rigaOrdine.getProdottoId());

            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error in updateRigaOrdine", e);
        }
    }

    @Override
    public void deleteRigaOrdine(int ordineId, String prodottoId) {
        String query = "DELETE FROM RigaOrdine WHERE ordine_id = ? AND prodotto_id = ?";

        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, ordineId);
            ps.setString(2, prodottoId);

            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error in deleteRigaOrdine", e);
        }
    }
}
