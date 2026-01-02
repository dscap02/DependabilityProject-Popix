package com.popx.persistenza;

import com.popx.modello.ProdottoBean;

import java.sql.SQLException;
import java.util.List;
import javax.servlet.http.HttpSession;

public interface ProdottoDAO {


    boolean saveProdotto(ProdottoBean prodotto);


    ProdottoBean getProdottoById(String id);


    List<ProdottoBean> getAllProducts();


    List<ProdottoBean> getProdottiByBrand(String brand);


    List<ProdottoBean> getProdottiByBrandAndPrice(String brand, boolean ascending);


    List<ProdottoBean> getProdottiSortedByPrice(boolean ascending);


    byte[] getProductImageById(String id);


    List<ProdottoBean> getRandomProducts(int limit) throws SQLException;


    void updateProductQtyInCart(HttpSession session, String productId, int qty);


    int getProductQtyInCart(HttpSession session, String productId);


    void saveCartToDatabase(String userEmail, List<ProdottoBean> cart) throws SQLException;


    List<ProdottoBean> getCartByUserEmail(String userEmail) throws SQLException;


    void updateCartProductQuantityInDatabase(String userEmail, String productId, int qty) throws SQLException;


    void removeProductFromCart(String userEmail, String productId) throws SQLException;


    void deleteProductById(String id) throws SQLException;


    boolean updateProduct(ProdottoBean prodottoBean);


    void updateStock(String productId, int quantity) throws SQLException;
}

