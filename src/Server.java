import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

public class Server {

    public static void main(String[] args) throws Exception {
        Class<ConnectionHandler> obj = ConnectionHandler.class;
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        for (Method method: obj.getDeclaredMethods()) {
            if (method.isAnnotationPresent(WebRoute.class)) {
                Annotation annotation = method.getAnnotation(WebRoute.class);
                WebRoute webRoute = (WebRoute) annotation;
                server.createContext(webRoute.path(), new ConnectionHandler(webRoute.path()));
            }
        }
        server.setExecutor(Executors.newFixedThreadPool(6));
        server.start();
    }

}