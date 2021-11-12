package webserver;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    public boolean logineFlg;
    private String path = "";
    private String header = "";
    private String parameter = "";
    private User user;
    private Map<String, String> requestHeader = new HashMap<String, String>();

    private int questionIndex;
    String[] headerLine;

    private HttpMethod method;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line = br.readLine();

        questionIndex = 0;

        path = getURL(line);

        int contentLength = 0;

        line = br.readLine();
        //3. 헤더
        while (!"".equals(line)) {
            String[] requestBody = line.split(": ");
            requestHeader.put(requestBody[0], requestBody[1]); //header 정보를 map에 담는다

            System.out.println("key:" + requestBody[0]+ "value:" + requestBody[1]);
            if (line.contains("Content-Length")) {
                String[] httpBody = line.split(": ");
                contentLength = Integer.parseInt(httpBody[1]);
            }

            if(line.contains("Cookie")) logineFlg = isLogin(line);
            line = br.readLine();
        }

        if(method.isGet())getRequest(headerLine, questionIndex);
        if (method.isPost()){getPost(br, contentLength);}

    }

    private String getURL(String line){
        String url;
        headerLine = line.split(" ");

        //1. 메소드
        method = HttpMethod.valueOf(headerLine[0]);

        if(method.isPost()){
            url = headerLine[1];
        }else{
            questionIndex = headerLine[1].indexOf("?");
            if(questionIndex == -1){
                url = headerLine[1];
            }else{
                url = headerLine[1].substring(0, questionIndex);
            }
        }
        return url;
    }
    public HttpMethod getMethod(){
        return method;
    }
    public String getPath(){
        return path;
    }
    public String getHeader(String key){
        return requestHeader.get(key);
    }
    public String getParameter(String key){
        if(key.equals("userId")) return user.getUserId();
        if(key.equals("password")) return user.getPassword();
        if(key.equals("name")) return user.getName();
        if(key.equals("email")) return user.getEmail();
        else{return "";}
    }

    public void getRequest(String[] headerLine, int questionIndex){
        String userInfo = headerLine[1].substring(questionIndex +1, headerLine[1].length());

        Map<String, String> userInfoMap = HttpRequestUtils.parseQueryString(userInfo);
        user = new User(userInfoMap.get("userId"), userInfoMap.get("password"), userInfoMap.get("name"), userInfoMap.get("email"));
    }
    public void getPost(BufferedReader br, int contentLength) throws IOException {
        String userInfo = IOUtils.readData(br, contentLength);

        Map<String, String> userInfoMap = HttpRequestUtils.parseQueryString(userInfo);
        user = new User(userInfoMap.get("userId"), userInfoMap.get("password"), userInfoMap.get("name"), userInfoMap.get("email"));
        DataBase.addUser(user);
    }

    public boolean isLogin(String line){
        String[] headerTokens = line.split(":");
        Map<String,String> cookies = HttpRequestUtils.parseCookies(headerTokens[1].trim());
        String value = cookies.get("logined");
        if(value ==null){
            return false;
        }
        return Boolean.parseBoolean(value);
    }
}
