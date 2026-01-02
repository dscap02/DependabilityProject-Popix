package com.popx.modello;

public class GestoreOrdineBean extends UserBean {

    /*@
      @ ensures getUsername() == null
      @      && getEmail() == null
      @      && getPassword() == null
      @      && getRole() == null;
      @*/
    public GestoreOrdineBean() {}

    /*@
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
      @ requires role.equals("Gestore");

      @ ensures getUsername().equals(username)
      @      && getEmail().equals(email)
      @      && getPassword().equals(password)
      @      && getRole().equals("Gestore");
      @*/
    public GestoreOrdineBean(String username,
                             String email,
                             String password,
                             String role) {
        super(username, email, password, role);
    }
}
