package com.cloudmanager.services.dropbox;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Receives the authentication codes from the web browser.
 * <p>
 * Once the user authorizes the application, the web browser
 * calls this server with the authorization tokens as parameters.
 */
final class CodeReceiverServer {
    private static final String HOST = "localhost";
    private static final int PORT = 41325;
    private static final String PATH = "/auth";

    private Server server;

    private Map<String, String[]> responseMap;

    private final Lock lock = new ReentrantLock();
    private final Condition gotAuthorizationResponse = lock.newCondition();

    /**
     * Starts the web server
     *
     * @throws IOException If the server couldn't be started
     */
    void start() throws IOException {
        if (server != null)
            return;

        server = new Server(PORT);
        server.setHandler(new CallbackHandler());

        try {
            server.start();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Returns the URL to redirect to. This is where this server expects a response.
     *
     * @return The redirect URL
     */
    String getRedirectUri() {
        return "http://" + HOST + ":" + PORT + PATH;
    }

    /**
     * Locks the thread until an authentication mao is aquired.
     *
     * @return The authentication Map
     * @throws IOException If there is any problem
     */
    Map<String, String[]> waitForCode() throws IOException {
        lock.lock();
        try {
            while (responseMap == null) {
                gotAuthorizationResponse.awaitUninterruptibly();
            }

            return responseMap;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Stops the server
     *
     * @throws IOException If the server coudn't be stopped
     */
    void stop() throws IOException {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                throw new IOException(e);
            }
            server = null;
        }
    }

    /**
     * Handles the requests to the server
     */
    private class CallbackHandler extends AbstractHandler {

        @Override
        public void handle(String target, Request request, HttpServletRequest servletRequest, HttpServletResponse response)
                throws IOException, ServletException {
            if (!PATH.equals(target)) { // If it's not where we expect it, ignore it
                return;
            }

            // Write a basic page
            writeLandingHtml(response);
            response.flushBuffer();
            request.setHandled(true);
            lock.lock();
            try {
                // Save the response
                responseMap = new HashMap<>();
                responseMap.putAll(request.getParameterMap());

                // Signal the function to return;
                gotAuthorizationResponse.signal();
            } finally {
                lock.unlock();
            }
        }

        private void writeLandingHtml(HttpServletResponse res) throws IOException {
            res.setStatus(HttpServletResponse.SC_OK);
            res.setContentType("text/html");

            PrintWriter doc = res.getWriter();
            doc.println("<html>");
            doc.println("<head><title>OAuth 2.0 Authentication Token Recieved</title></head>");
            doc.println("<body>");
            doc.println("Received verification code. You may now close this window...");
            doc.println("<script>window.close()</script>");
            doc.println("</body>");
            doc.println("</HTML>");
            doc.flush();
        }
    }
}
