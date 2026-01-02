package com.popx.modello;

import java.io.Serializable;

public class UserBean implements Serializable {

    //@ private invariant username == null || !username.isEmpty();

    //@ private invariant email == null
    //@     || (!email.isEmpty()
    //@         && email.contains("@")
    //@         && email.indexOf('@') > 0
    //@         && email.substring(email.indexOf('@') + 1).contains(".")
    //@         && email.indexOf('.') > email.indexOf('@') + 1
    //@         && email.lastIndexOf('.') < email.length() - 1);

    //@ private invariant password == null
    //@     || (password.length() >= 8
    //@         && password.length() <= 16
    //@         && (\exists int i; 0 <= i && i < password.length();
    //@               Character.isLowerCase(password.charAt(i)))
    //@         && (\exists int i; 0 <= i && i < password.length();
    //@               Character.isUpperCase(password.charAt(i)))
    //@         && (\exists int i; 0 <= i && i < password.length();
    //@               Character.isDigit(password.charAt(i))));

    //@ private invariant role == null
    //@     || role.equals("Gestore")
    //@     || role.equals("User")
    //@     || role.equals("Admin");

    private String username;
    private String email;
    private String password;
    private String role;

    /*@
      @ ensures getUsername() == null
      @      && getEmail() == null
      @      && getPassword() == null
      @      && getRole() == null;
      @*/
    public UserBean() {}

    /*@
      @ requires username != null && !username.isEmpty();
      @ requires email != null && !email.isEmpty();
      @ requires email.contains("@");
      @ requires email.indexOf('@') > 0;
      @ requires email.substring(email.indexOf('@') + 1).contains(".");
      @ requires email.indexOf('.') > email.indexOf('@') + 1;
      @ requires email.lastIndexOf('.') < email.length() - 1;

      @ requires password != null;
      @ requires password.length() >= 8 && password.length() <= 16;
      @ requires (\exists int i; 0 <= i && i < password.length();
      @               Character.isLowerCase(password.charAt(i)));
      @ requires (\exists int i; 0 <= i && i < password.length();
      @               Character.isUpperCase(password.charAt(i)));
      @ requires (\exists int i; 0 <= i && i < password.length();
      @               Character.isDigit(password.charAt(i)));

      @ requires role != null;
      @ requires role.equals("Gestore")
      @       || role.equals("User")
      @       || role.equals("Admin");

      @ ensures getUsername().equals(username)
      @      && getEmail().equals(email)
      @      && getPassword().equals(password)
      @      && getRole().equals(role);
      @*/
    public UserBean(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /*@ ensures \result == username; @*/
    public String getUsername() { return username; }

    /*@ ensures \result == email; @*/
    public String getEmail() { return email; }

    /*@ ensures \result == password; @*/
    public String getPassword() { return password; }

    /*@ ensures \result == role; @*/
    public String getRole() { return role; }

    /*@
      @ requires username != null && !username.isEmpty();
      @ ensures getUsername().equals(username);
      @*/
    public void setUsername(String username) {
        this.username = username;
    }

    /*@
      @ requires email != null && !email.isEmpty();
      @ ensures getEmail().equals(email);
      @*/
    public void setEmail(String email) {
        this.email = email;
    }

    /*@
      @ requires password != null;
      @ ensures getPassword().equals(password);
      @*/
    public void setPassword(String password) {
        this.password = password;
    }

    /*@
      @ requires role != null;
      @ ensures getRole().equals(role);
      @*/
    public void setRole(String role) {
        this.role = role;
    }
}