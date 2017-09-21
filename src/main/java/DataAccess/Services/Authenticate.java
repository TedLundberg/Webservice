package DataAccess.Services;

import DataAccess.Entities.UserData;
import org.sql2o.*;
import org.sql2o.Connection;


import java.sql.*;
import java.util.List;

public class Authenticate {

    public String createCookieCode(Sql2o sql2o, String userName){

        String updateSql = "update login set cookieCode = :valParam where username = :userNameParam";

        //Create random alpha numeric string to use as sessing identifier
        RandomString session = new RandomString();
        String cookieCode = session.nextString();

        try (Connection con = sql2o.open()) {
            con.createQuery(updateSql)
                    .addParameter("valParam", cookieCode)
                    .addParameter("userNameParam", userName)
                    .executeUpdate();
        }

        return cookieCode;
    }

    public void incrementLoginAttempts(Sql2o sql2o, String userName, int attempts){
        String updateSql = "update login set attempts = :valParam where username = :userNameParam";

        //Increment attempts
        //int newAttempts = attempts++;

        try (Connection con = sql2o.open()) {
            con.createQuery(updateSql)
                    .addParameter("valParam", attempts + 1)
                    .addParameter("userNameParam", userName)
                    .executeUpdate();
        }

    }

    public void resetLoginAttempts(Sql2o sql2o, String userName){
        String updateSql = "update login set attempts = :valParam where username = :userNameParam";

        try (Connection con = sql2o.open()) {
            con.createQuery(updateSql)
                    .addParameter("valParam", 0)
                    .addParameter("userNameParam", userName)
                    .executeUpdate();
        }

    }

    public List<UserData> getUserData(Sql2o sql2o, String userName){

        try (Connection con = sql2o.open()) {
            final String query =
                    "SELECT username, pwd, cookieCode, attempts " +
                            "FROM login WHERE username = :userNameParam";

            return con.createQuery(query)
                    .addParameter("userNameParam", userName)
                    .executeAndFetch(UserData.class);
        }
    }

}
