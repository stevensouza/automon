package com.stevesouza.webapp;

import com.stevesouza.helloworld.HelloWorld;
import com.stevesouza.jdk.JdkHelloWorld;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/** Servlet used to test automon in a web application
 * 
 * @author stevesouza
 *
 */
public class MyServlet extends HttpServlet {


    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        HelloWorld helloWorld = new HelloWorld();
        PrintWriter out = response.getWriter();
        out.println("Hello World & "+helloWorld.getFirstName()+" "+helloWorld.getLastName());
        try {
            helloWorld.iMessedUp();
        } catch (Exception exception) {
            // hidden exception
        }

        try {
            jdkInteractions(out);
        } catch (Exception exception) {
            // hidden exception
        }

        out.flush();
        out.close();
    }

    private void jdkInteractions(PrintWriter out) throws Exception {
        JdkHelloWorld helloWorld = new JdkHelloWorld();
        String info;

        String url = "http://www.google.com";
        info = url+" line count="+helloWorld.readUrl(url);
        out.println(info);

        url =  "http://example.com";
        info = url+" status code="+helloWorld.urlWithConnection(url);
        out.println(info);

        out.println("jdbc interactions");
        helloWorld.jdbcInteractions();
    }



}
