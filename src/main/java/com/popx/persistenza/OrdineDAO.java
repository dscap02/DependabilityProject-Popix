package com.popx.persistenza;

import com.popx.modello.OrdineBean;

import java.util.List;

public interface OrdineDAO {


    boolean insertOrdine(OrdineBean ordine);


    OrdineBean getOrdineById(int id);


    List<OrdineBean> getAllOrdini();

    List<OrdineBean> getOrdiniByCliente(String clienteEmail);


    int countOrdiniByCliente(String email);


    List<OrdineBean> getOrdiniByClientePaginati(String email, int currentPage, int itemsPerPage);


    List<OrdineBean> getOrdiniPaginati(int currentPage, int recordsPerPage);


    int countTuttiOrdini();


    boolean updateStatus(OrdineBean ordineBean);
}
