import DataAccess.Entities.TemperatureSensor;
import DataAccess.Entities.UserData;
import DataAccess.Services.Authenticate;
import DataAccess.Services.RandomString;
import DataAccess.Services.TemperatureSensorRepository;
import com.google.gson.Gson;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import spark.Spark.*;

import javax.servlet.http.Cookie;
import javax.swing.text.View;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;


public class WebService {



    public static void main(String args[]){
        //Change server port number
        int portNumber = 3373;
        port(portNumber);
        //Set maximum allowed threads
        int maxThreads = 1;
        threadPool(maxThreads);

        TemperatureSensorRepository temperatureSensorRepository = new TemperatureSensorRepository();
        Authenticate authenticate = new Authenticate();
        int maxAttempts = 2; //Maximum allowed login attempts

        staticFiles.location("/static");
        Sql2o sql2o = new Sql2o("jdbc:postgresql://localhost:5432/webservice", "postgres", "%#dbas2");

        before("/", (req, res) -> {
            boolean auth;
            //Check if authenticated
            //Get session code and user name from cookies
            String cookieCode = req.cookie("sessionCode");
            String cookieUserName = req.cookie("userName");

            if(cookieCode != null && cookieUserName != null && !cookieCode.isEmpty() && !cookieUserName.isEmpty()) {
                //Get session code from database
                List<UserData> userDataList = authenticate.getUserData(sql2o, cookieUserName);

                UserData userDataObject = userDataList.get(0);
                String dbUserName = userDataObject.getUserName();
                String dbCookieCode = userDataObject.getCookieCode();

                int dbAttempts = userDataObject.getAttempts();

                //Check how many login attempts that have been made. If too many, kick the user out
                if(dbAttempts>maxAttempts){
                    System.out.println("User '" + dbUserName + "' has tried to login " + dbAttempts + " times. User is locked out until database is reset.");
                    halt("Too many login attempts! Fuck off!");
                }

                if (cookieUserName.equals(dbUserName) && cookieCode.equals(dbCookieCode)) {
                    System.out.println("User '" + dbUserName + "' has logged in");
                } else {
                    System.out.println("User '" + dbUserName + "' has not been authenticated, passing to /Login");
                    res.redirect("/Login");
                }
            }else {
                System.out.println("User tried to connect to site but no cookies were found, passing to /Login");
                res.redirect("/Login");
            }
        });

        get("/", (req,res) ->{
            Map<String, Object> model = new HashMap<>();
            List<TemperatureSensor> temperatures = temperatureSensorRepository.GetReadings(sql2o);
            model.put("temperatures", temperatures);

            return new ThymeleafTemplateEngine().render(new ModelAndView(model,"Index"));
        });

        get("/Cameras", (req,res) ->{
            Map<String, Object> model = new HashMap<>();
            return new ThymeleafTemplateEngine().render(new ModelAndView(model,"Camera"));
        });

        get("/Login", (req,res) ->{
            Map<String, Object> model = new HashMap<>();
            return new ThymeleafTemplateEngine().render(new ModelAndView(model,"Login"));
        });

        post("/Login", (req,res) ->{
            String userName, pwd;
            boolean auth = false;
            userName = req.queryParams("email");
            pwd = req.queryParams("pwd");

            //Get user data from database
            List<UserData> userDataList = authenticate.getUserData(sql2o, userName);


            if(userDataList.size() > 0) {
                UserData userDataObject = userDataList.get(0);
                String dbUserName = userDataObject.getUserName();
                String dbPwd = userDataObject.getPwd();
                //String dbCookieCode = userDataObject.getCookieCode();
                int dbAttempts = userDataObject.getAttempts();

                //Check how many login attempts that have been made. If too many, kick the user out
                if(dbAttempts > maxAttempts){
                    System.out.println("User '" + dbUserName + "' has tried to login " + dbAttempts + " times. User is locked out until database is reset.");
                    halt("Too many login attempts! Fuck off!");
                }

                if (userName.equals(dbUserName) && pwd.equals(dbPwd)) {
                    //Generate random number that will be set in the cookie
                    String cookieCode = authenticate.createCookieCode(sql2o, userName);

                    //Send cookie
                    res.cookie("/", "sessionCode", cookieCode, 900, false, true);
                    res.cookie("/", "userName", userName, 900, false, true);
                    //auth = true;

                    //Reset login attempts in database
                    authenticate.resetLoginAttempts(sql2o,userName);

                    System.out.println("User '" + dbUserName + "' successfully logged in, redirecting to page");
                    res.redirect("/");
                } else {
                    System.out.println("User '" + dbUserName + "' entered the wrong password (attempt: " + dbAttempts+1 + "), passing back to /Login");

                    //Increment login attempts in database
                    authenticate.incrementLoginAttempts(sql2o, userName, dbAttempts);

                    Map<String, Object> model = new HashMap<>();
                    model.put("loginError", true);
                    model.put("wrongPassword", true);
                    return new ThymeleafTemplateEngine().render(new ModelAndView(model,"Login"));

                    //res.redirect("/Login");
                }
            }else {
                System.out.println("Username '" + userName + "' not found in database, passing back to /Login");

                List<TemperatureSensor> tempList = new ArrayList<>();

                Map<String, Object> model = new HashMap<>();
                model.put("loginError", true);
                model.put("wrongUsername", true);
                model.put("tempSensors", tempList);
                return new ThymeleafTemplateEngine().render(new ModelAndView(model,"Login"));



                //halt("Username not found");
            }
            return null;
/*
            Map<String, Object> model = new HashMap<>();
            if(auth) {
                return new ThymeleafTemplateEngine().render(new ModelAndView(model, "Index"));
            }else{
                return new ThymeleafTemplateEngine().render(new ModelAndView(model, "Login"));
            }*/
        });

        get("/temperature", (req, res) -> {
            String value = req.queryParams("value");
            temperatureSensorRepository.CreateReading(sql2o, value);

            return res;
        });

        get("/temperatures", (req, res) -> {

            return temperatureSensorRepository.GetReadings(sql2o);
        },new JsonTransformer());
    }

}
