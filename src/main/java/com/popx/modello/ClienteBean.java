package com.popx.modello;

/*@
  @ also
  @ invariant true;
@*/
public class ClienteBean extends UserBean {

    /*@
      @ ensures this.getUsername() == null
      @      && this.getEmail() == null
      @      && this.getPassword() == null
      @      && this.getRole() == null;
    @*/
    public ClienteBean() {}

    /*@
      @ requires username != null && email != null && password != null && role != null;
      @ ensures this.getUsername().equals(username)
      @      && this.getEmail().equals(email)
      @      && this.getPassword().equals(password)
      @      && this.getRole().equals(role);
    @*/
    public ClienteBean(String username, String email, String password, String role) {
        super(username, email, password, role);
    }
}