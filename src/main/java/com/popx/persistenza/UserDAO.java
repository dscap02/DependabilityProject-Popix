package com.popx.persistenza;

import com.popx.modello.UserBean;
import java.sql.SQLException;


public  interface UserDAO<T extends UserBean> {

    T getUserByEmail(String email) throws SQLException;


    boolean saveUser(T user) throws SQLException;
}
