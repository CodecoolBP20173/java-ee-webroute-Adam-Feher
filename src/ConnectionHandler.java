import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ConnectionHandler implements HttpHandler {
    private String route;

    ConnectionHandler() {
    }

    ConnectionHandler(String route) {
        this.route = route;
    }

    @WebRoute(path = "/test", method = "post")
    private void onTest(HttpExchange httpExchange) throws IOException {
        String response = "This is the test page";
        httpRender(httpExchange, response);
    }

    @WebRoute(path = "/index")
    private void onIndex(HttpExchange httpExchange) throws IOException {
        String response = "This is the index page";
        httpRender(httpExchange, response);
        return;
    }

    private void httpRender(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Class<ConnectionHandler> obj = ConnectionHandler.class;
        for (Method method : obj.getDeclaredMethods()) {
            if (method.isAnnotationPresent(WebRoute.class)) {
                Annotation annotation = method.getAnnotation(WebRoute.class);
                WebRoute webRoute = (WebRoute) annotation;
                if (webRoute.path().equals(route)) {
                    try {
                        method.invoke(obj.newInstance(), httpExchange);
                    } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
