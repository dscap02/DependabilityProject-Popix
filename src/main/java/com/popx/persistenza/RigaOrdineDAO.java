package com.popx.persistenza;

import com.popx.modello.RigaOrdineBean;

import java.util.List;

public interface RigaOrdineDAO {

    void addRigaOrdine(RigaOrdineBean rigaOrdine);


    List<RigaOrdineBean> getRigheOrdineByOrdineId(int ordineId);


    void updateRigaOrdine(RigaOrdineBean rigaOrdine);

    void deleteRigaOrdine(int ordineId, String prodottoId);
}
