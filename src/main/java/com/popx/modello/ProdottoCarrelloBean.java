package com.popx.modello;

public class ProdottoCarrelloBean {

    //@ invariant clienteEmail == null || !clienteEmail.isEmpty();
    //@ invariant prodottoId == null || !prodottoId.isEmpty();
    //@ invariant quantity >= 0;
    //@ invariant unitaryCost >= 0;

    private String clienteEmail;
    private String prodottoId;
    private int quantity;
    private float unitaryCost;

    /*@
      @ ensures clienteEmail == null;
      @ ensures prodottoId == null;
      @ ensures quantity == 0;
      @ ensures unitaryCost == 0.0f;
      @*/
    public ProdottoCarrelloBean() {}

    /*@
      @ requires clienteEmail != null && !clienteEmail.isEmpty();
      @ requires prodottoId != null && !prodottoId.isEmpty();
      @ requires quantity >= 0;
      @ requires unitaryCost >= 0;
      @ ensures this.clienteEmail == clienteEmail;
      @ ensures this.prodottoId == prodottoId;
      @ ensures this.quantity == quantity;
      @ ensures this.unitaryCost == unitaryCost;
      @*/
    public ProdottoCarrelloBean(String clienteEmail,
                                String prodottoId,
                                int quantity,
                                float unitaryCost) {
        this.clienteEmail = clienteEmail;
        this.prodottoId = prodottoId;
        this.quantity = quantity;
        this.unitaryCost = unitaryCost;
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

    /*@ ensures \result == prodottoId; @*/
    public String getProdottoId() {
        return prodottoId;
    }

    /*@
      @ requires prodottoId != null && !prodottoId.isEmpty();
      @ ensures this.prodottoId == prodottoId;
      @*/
    public void setProdottoId(String prodottoId) {
        this.prodottoId = prodottoId;
    }

    /*@ ensures \result == quantity; @*/
    public int getQuantity() {
        return quantity;
    }

    /*@
      @ requires quantity >= 0;
      @ ensures this.quantity == quantity;
      @*/
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /*@ ensures \result == unitaryCost; @*/
    public float getUnitaryCost() {
        return unitaryCost;
    }

    /*@
      @ requires unitaryCost >= 0;
      @ ensures this.unitaryCost == unitaryCost;
      @*/
    public void setUnitaryCost(float unitaryCost) {
        this.unitaryCost = unitaryCost;
    }
}
