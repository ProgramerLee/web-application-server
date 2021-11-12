package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    DataOutputStream dos;

    Map<String, String> headerMap = new HashMap<String,String>();
    public HttpResponse(OutputStream outputStream){
        dos = new DataOutputStream(outputStream);

    }

    public void forward(String url) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());//파일을 바이트로 읽는다.

        if(url.endsWith(".css")){
            headerMap.put("Content-Type: ","text/css");
        }else if(url.endsWith(".js")){
            headerMap.put("Content-Type: ","application/javascript");
        }else{
            headerMap.put("Content-Type: ","text/html ;charset=utf-8");
        }
        headerMap.put("Content-Length: ", Integer.toString(body.length));
        response200Header(body.length);
        responseBody( body);
    }

    public void forward(byte[] html) throws IOException {

        headerMap.put("Content-Type: ","text/html ;charset=utf-8");
        headerMap.put("Content-Length: ", Integer.toString(html.length));
        response200Header(html.length);
        responseBody( html);
    }

    public void addHeader(String key, String value){
        headerMap.put(key +": ",value);
    }

    public void sendRedirect(String url) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp\\" + url).toPath());//파일을 바이트로 읽는다.

        headerMap.put("Location: ", url);
        response302Header(url);
        responseBody(body);
    }

    private void responseResource( String url) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp\\" + url).toPath());//파일을 바이트로 읽는다.

        response200Header(body.length);
        responseBody(body);
    }

    private void response200Header(int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            for (String key : headerMap.keySet()){
                dos.writeBytes(key+headerMap.get(key) +"\r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200CssHeader(int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 redirect \r\n");
            for (String key : headerMap.keySet()){
                dos.writeBytes(key+headerMap.get(key) +"\r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302HeaderSetCookie(String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 redirect \r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");
            dos.writeBytes("Location: "+ url+"\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
