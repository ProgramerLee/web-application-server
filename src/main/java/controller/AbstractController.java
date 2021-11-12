package controller;

import webserver.HttpMethod;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public abstract class AbstractController implements Controller{
    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        HttpMethod method = request.getMethod();

        if(method.isPost())doPost(request, response);
        else doGet(request, response);
    }
    protected void doPost(HttpRequest request, HttpResponse response){}
    protected void doGet(HttpRequest request, HttpResponse response){}
}
