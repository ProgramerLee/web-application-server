package webserver;

import controller.Controller;
import controller.CreateUserController;
import controller.ListUserController;
import controller.LoginController;


import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private static Map<String, Controller> controller = new HashMap<String, Controller>();

    static {
        controller.put("/user/create", new CreateUserController());
        controller.put("/user/login", new LoginController());
        controller.put("/user/list", new ListUserController());
    }

    public static Controller getController(String requestUrl){
        return controller.get(requestUrl);
    }
}
