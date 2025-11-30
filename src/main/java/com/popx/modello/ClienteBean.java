package com.popx.modello;

/*@
  @ invariant this.getRole() == null
  @        || this.getRole().equals("User");
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
      @ also
      @ requires username != null && !username.isEmpty();

      @ requires email != null;
      @ requires !email.isEmpty();
      @ requires email.contains("@");
      @ requires email.indexOf('@') > 0;
      @ requires email.substring(email.indexOf('@') + 1).contains(".");
      @ requires email.indexOf('.') > email.indexOf('@') + 1;
      @ requires email.lastIndexOf('.') < email.length() - 1;

      @ requires password != null;
      @ requires password.length() >= 8 && password.length() <= 16;
      @ requires (\exists int i; 0 <= i && i < password.length();
                    Character.isLowerCase(password.charAt(i)));
      @ requires (\exists int i; 0 <= i && i < password.length();
                    Character.isUpperCase(password.charAt(i)));
      @ requires (\exists int i; 0 <= i && i < password.length();
                    Character.isDigit(password.charAt(i)));

      @ requires role != null;
      @ requires role.equals("User");

      @ ensures this.getUsername().equals(username)
      @      && this.getEmail().equals(email)
      @      && this.getPassword().equals(password)
      @      && this.getRole().equals("User");
    @*/
    public ClienteBean(String username, String email, String password, String role) {
        super(username, email, password, role); // JML richiede che role sia "User"
    }
}
