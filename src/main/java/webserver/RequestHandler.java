package webserver;

import controller.Controller;
import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            Controller controller = RequestMapping.getController(request.getPath());
            if(controller == null){
                String path = getDefaultPath(request.getPath());
                response.forward(path);
            }else{
                controller.service(request,response);
            }

            /*switch (path) {
                case "/user/create" :
                    User user = new User(
                        request.getParameter("userId"),
                        request.getParameter("password"),
                        request.getParameter("name"),
                        request.getParameter("email")
                    );
                    break;
                *//*case "/user/create" :
                case "/user/create" :*//*
            }
            if("/user/create".equals(path)){
                User user = new User(
                        request.getParameter("userId");
                request.getParameter("password");
                request.getParameter("name");
                request.getParameter("email");
                );
            }*/


            /*if (line != null) {
                int contentLength = 0;
                String[] requsetInfos = line.split(" "); //url을 얻기 위해서

                int questionIndex = requsetInfos[1].indexOf("?");
                String url = requsetInfos[1].substring(questionIndex +1);
                Map<String, String> userInfoMap = null;

                boolean logineFlg = false;

                *//*while (!"".equals(line)) {
                    log.debug("header : {}", line);
                    line = br.readLine();

                    if (line.contains("Content-Length")) {
                        String[] httpBody = line.split(": ");
                        contentLength = Integer.parseInt(httpBody[1]);
                    }
                    if(line.contains("Cookie")) logineFlg = isLogin(line);
                }*//*

                log.debug("body : {}", line);
                //url 안들어오면 초기 페이지로 진입
                if(url.equals("") || url.equals("/") ){ url = "/index.html";}

                if(url.equals("/user/create")){
                    String userInfo = IOUtils.readData(br, contentLength);

                    userInfoMap = HttpRequestUtils.parseQueryString(userInfo);
                    User user = new User(userInfoMap.get("userId"), userInfoMap.get("password"), userInfoMap.get("name"), userInfoMap.get("email"));
                    DataBase.addUser(user);
                    url = "/index.html";

                    byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());//파일을 바이트로 읽는다.
                    DataOutputStream dos = new DataOutputStream(out);
                    response302Header(dos, "/index.html");

                }else if(url.equals("/user/login")){
                    String loginUserInfo = IOUtils.readData(br, contentLength);

                    userInfoMap = HttpRequestUtils.parseQueryString(loginUserInfo);
                    User user = DataBase.findUserById(userInfoMap.get("userId"));

                    if(user == null){ // 유저가 없을 경우
                        url = "/user/login_failed.html";
                        responseResource(out, url);
                        return;
                    }

                    if(user.getPassword().equals(userInfoMap.get("password"))){ //비밀번호를 맞춘 경우
                        url = "/index.html";
                        DataOutputStream dos = new DataOutputStream(out);
                        response302HeaderSetCookie(dos,url);
                    }else{ //비밀번호를 틀린 경우
                        url = "/user/login_failed.html";
                        responseResource(out, url);
                        return;
                    }
                }else if(url.equals("/user/list")){
                    if(!logineFlg){ //로그인 되지 않았을 때
                        responseResource(out, "/user/login.html");
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
                    DataOutputStream dos = new DataOutputStream(out);
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                }else if(url.endsWith("css")){
                    DataOutputStream dos = new DataOutputStream(out);
                    byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());//파일을 바이트로 읽는다.

                    response200CssHeader(dos, body.length);
                    responseBody(dos, body);
                }else{
                    responseResource(out, url);
                }
            }*/

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultPath(String path){
        if(path.equals("/")) return "/index.html";
        else return path;
    }

    /*

    private boolean isLogin(String line){
        String[] headerTokens = line.split(":");
        Map<String,String> cookies = HttpRequestUtils.parseCookies(headerTokens[1].trim());
        String value = cookies.get("logined");
        if(value ==null){
            return false;
        }
        return Boolean.parseBoolean(value);
    }

    private void responseResource(OutputStream out , String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());//파일을 바이트로 읽는다.

        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html ;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200CssHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 redirect \r\n");
            dos.writeBytes("Location: "+ url+"\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302HeaderSetCookie(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 redirect \r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");
            dos.writeBytes("Location: "+ url+"\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }*/
}
