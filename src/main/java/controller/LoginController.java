package controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public class LoginController extends AbstractController{
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        User user = DataBase.findUserById(request.getParameter("userId"));
        if(user != null){
            if(user.getPassword().equals(request.getParameter("password"))){
                response.addHeader("set-Cookie", "logined=true");
                response.sendRedirect("/index.html");
            }else{
                response.sendRedirect("/user/login_failed.html");
            }
        }else{
            response.sendRedirect("/user/login_failed.html");
        }
    }


}
