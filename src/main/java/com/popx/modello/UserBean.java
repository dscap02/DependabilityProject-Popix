package com.popx.modello;

import java.io.Serializable;

/*@
  @ invariant username == null || !username.isEmpty();

  @ invariant email == null
      || (!email.isEmpty()
          && email.contains("@")
          && email.indexOf('@') > 0
          && email.substring(email.indexOf('@') + 1).contains(".")
          && email.indexOf('.') > email.indexOf('@') + 1
          && email.lastIndexOf('.') < email.length() - 1);

  @ invariant password == null
      || (password.length() >= 8
          && password.length() <= 16
          && (\exists int i; 0 <= i && i < password.length();
                Character.isLowerCase(password.charAt(i)))
          && (\exists int i; 0 <= i && i < password.length();
                Character.isUpperCase(password.charAt(i)))
          && (\exists int i; 0 <= i && i < password.length();
                Character.isDigit(password.charAt(i))));

  @ invariant role == null
      || role.equals("Gestore")
      || role.equals("User")
      || role.equals("Admin");
@*/
public class UserBean implements Serializable {

    private String username;
    private String email;
    private String password;
    private String role;

    /*@
      @ ensures this.username == null
      @      && this.email == null
      @      && this.password == null
      @      && this.role == null;
    @*/
    public UserBean() {}

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
      @ requires role.equals("Gestore")
             || role.equals("User")
             || role.equals("Admin");

      @ ensures this.username.equals(username)
      @      && this.email.equals(email)
      @      && this.password.equals(password)
      @      && this.role.equals(role);
    @*/
    public UserBean(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /*@ ensures \result == username; @*/
    public String getUsername() { return username; }

    /*@
      @ requires username != null && !username.isEmpty();
      @ ensures this.username.equals(username);
    @*/
    public void setUsername(String username) { this.username = username; }

    /*@ ensures \result == email; @*/
    public String getEmail() { return email; }

    /*@
      @ requires email != null;
      @ requires !email.isEmpty();
      @ requires email.contains("@");
      @ requires email.indexOf('@') > 0;
      @ requires email.substring(email.indexOf('@') + 1).contains(".");
      @ requires email.indexOf('.') > email.indexOf('@') + 1;
      @ requires email.lastIndexOf('.') < email.length() - 1;
      @ ensures this.email.equals(email);
    @*/
    public void setEmail(String email) { this.email = email; }

    /*@ ensures \result == password; @*/
    public String getPassword() { return password; }

    /*@
      @ requires password != null;
      @ requires password.length() >= 8 && password.length() <= 16;
      @ requires (\exists int i; 0 <= i && i < password.length();
                    Character.isLowerCase(password.charAt(i)));
      @ requires (\exists int i; 0 <= i && i < password.length();
                    Character.isUpperCase(password.charAt(i)));
      @ requires (\exists int i; 0 <= i && i < password.length();
                    Character.isDigit(password.charAt(i)));
      @ ensures this.password.equals(password);
    @*/
    public void setPassword(String password) { this.password = password; }

    /*@ ensures \result == role; @*/
    public String getRole() { return role; }

    /*@
      @ requires role != null;
      @ requires role.equals("Gestore")
             || role.equals("User")
             || role.equals("Admin");
      @ ensures this.role.equals(role);
    @*/
    public void setRole(String role) { this.role = role; }
}
