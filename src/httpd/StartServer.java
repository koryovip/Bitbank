package httpd;

import com.jfinal.core.JFinal;

public class StartServer {

    public static void main(String[] args) {
        JFinal.start("WebRoot", 88, "/", 5);
    }

}
