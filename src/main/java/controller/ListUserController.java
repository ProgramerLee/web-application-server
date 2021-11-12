package controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.util.Collection;

public class ListUserController extends AbstractController{
    private static final Logger log = LoggerFactory.getLogger(ListUserController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        if(!request.logineFlg){ //로그인 되지 않았을 때
            response.sendRedirect("/user/login.html");
            return;
        }

        Collection<User> userList = DataBase.findAll();
        StringBuilder userHtml = new StringBuilder();
        userHtml.append("<table>");
        for (User user : userList){
            userHtml.append("<tr>");
            userHtml.append("<td>"+user.getUserId()+"</td>");
            userHtml.append("<td>"+user.getName()+"</td>");
            userHtml.append("<td>"+user.getEmail()+"</td>");
            userHtml.append("</tr>");
        }
        userHtml.append("</table>");
        byte[] body = userHtml.toString().getBytes();

        response.forward(body);
    }
}
