package com.popx.modello;

import java.util.List;

public class CarrelloBean {

    //@ invariant clienteEmail == null || !clienteEmail.isEmpty();

    //@ invariant prodottiCarrello == null
    //@        || (\forall int i;
    //@               0 <= i && i < prodottiCarrello.size();
    //@               prodottiCarrello.get(i) != null);

    private String clienteEmail;
    private List<ProdottoCarrelloBean> prodottiCarrello;

    /*@
      @ ensures clienteEmail == null;
      @ ensures prodottiCarrello == null;
      @*/
    public CarrelloBean() {}

    /*@
      @ requires clienteEmail != null && !clienteEmail.isEmpty();
      @ requires prodottiCarrello != null;
      @ requires (\forall int i;
      @              0 <= i && i < prodottiCarrello.size();
      @              prodottiCarrello.get(i) != null);
      @
      @ ensures this.clienteEmail == clienteEmail;
      @ ensures this.prodottiCarrello == prodottiCarrello;
      @*/
    public CarrelloBean(String clienteEmail,
                        List<ProdottoCarrelloBean> prodottiCarrello) {
        this.clienteEmail = clienteEmail;
        this.prodottiCarrello = prodottiCarrello;
    }

    /*@ ensures \result == clienteEmail; @*/
    public String getClienteEmail() {
        return clienteEmail;
    }

    /*@
      @ requires clienteEmail != null && !clienteEmail.isEmpty();
      @ ensures this.clienteEmail == clienteEmail;
      @*/
    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
    }

    /*@ ensures \result == prodottiCarrello; @*/
    public List<ProdottoCarrelloBean> getProdottiCarrello() {
        return prodottiCarrello;
    }

    /*@
      @ requires prodottiCarrello != null;
      @ requires (\forall int i;
      @              0 <= i && i < prodottiCarrello.size();
      @              prodottiCarrello.get(i) != null);
      @ ensures this.prodottiCarrello == prodottiCarrello;
      @*/
    public void setProdottiCarrello(
            List<ProdottoCarrelloBean> prodottiCarrello) {
        this.prodottiCarrello = prodottiCarrello;
    }
}
