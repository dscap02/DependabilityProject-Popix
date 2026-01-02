package com.popx.persistenza;

import com.popx.modello.CarrelloBean;

public interface CarrelloDAO {


    void salvaCarrello(CarrelloBean carrello);


    CarrelloBean ottieniCarrelloPerEmail(String email);

    void clearCartByUserEmail(String email);
}
