package webserver;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class HttpRequestTest {
    private String testDirectory = "./src/test/resources/";

    @Test
    public void request_GET() throws Exception{
        //InputStream in = new FileInputStream(new File(testDirectory+"Http_POST.http"));
        InputStream in = new FileInputStream(new File(testDirectory+"Http_GET.http"));
        HttpRequest httpRequest = new HttpRequest(in);

        //Assert.assertEquals("GET", httpRequest.getMethod());
        Assert.assertEquals("GET", httpRequest.getMethod());
        Assert.assertEquals("/user/create", httpRequest.getPath());
        Assert.assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        Assert.assertEquals("swlee", httpRequest.getParameter("userId"));

    }

}
