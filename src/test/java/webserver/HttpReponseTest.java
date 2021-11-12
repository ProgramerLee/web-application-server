package webserver;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class HttpReponseTest {
    private String testDirectory = "./src/test/resources/";

    @Test
    public void responseForward() throws Exception{
        HttpResponse response = new HttpResponse(createOutputStream("Http_forward.txt"));
        response.forward("index.html");
    }

    @Test
    public void responseRedirect() throws Exception{
        HttpResponse response = new HttpResponse(createOutputStream("Http_Redirect.txt"));
        response.sendRedirect("index.html");
    }


    @Test
    public void responseCookies() throws Exception{
        HttpResponse response = new HttpResponse(createOutputStream("Http_Cookie.txt"));
        response.addHeader("Set-Cookie","logined=true");
        response.sendRedirect("index.html");
    }

    private OutputStream createOutputStream(String filename) throws FileNotFoundException {
        return new FileOutputStream(new File(testDirectory+filename));
    }
}
