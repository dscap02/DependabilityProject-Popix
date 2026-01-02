package com.popx.servizio;

import com.popx.modello.UserBean;
import com.popx.persistenza.UserDAO;
import com.popx.persistenza.UserDAOImpl;


public class AuthenticationService {

    private UserDAO userDAO;


    public AuthenticationService() {
        // Production / integration behavior (UNCHANGED)
        this.userDAO = new UserDAOImpl();
    }


    public AuthenticationService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }


    public UserBean login(String email, String password) throws Exception {
        UserBean user = userDAO.getUserByEmail(email);

        if (user != null && SecurityService.checkPassword(password, user.getPassword())) {
            return user;
        }
        throw new Exception("Invalid credentials");
    }


    public boolean registerUser(UserBean user) throws Exception {
        if (userDAO.getUserByEmail(user.getEmail()) == null) {
            return userDAO.saveUser(user);
        }
        throw new Exception("User already exists");
    }


    public boolean isEmailRegistered(String email) throws Exception {
        return userDAO.getUserByEmail(email) != null;
    }


    public String retrieveRole(String email) throws Exception {
        UserBean user = userDAO.getUserByEmail(email);
        return user.getRole();
    }
}
