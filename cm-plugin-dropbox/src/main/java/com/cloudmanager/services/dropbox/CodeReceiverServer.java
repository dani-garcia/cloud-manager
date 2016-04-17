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

final class CodeReceiverServer {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 41325;
    private static final String DEFAULT_PATH = "/auth";

    private Server server;

    private final String host;
    private int port;

    private Map<String, String[]> responseMap;

    private final Lock lock = new ReentrantLock();
    private final Condition gotAuthorizationResponse = lock.newCondition();

    CodeReceiverServer() throws IOException {
        this.host = DEFAULT_HOST;
        this.port = DEFAULT_PORT;

        start();
    }

    private void start() throws IOException {
        server = new Server(port);

        server.setHandler(new CallbackHandler());
        try {
            server.start();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    String getRedirectUri() throws IOException {
        return "http://" + host + ":" + port + DEFAULT_PATH;
    }

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

    private class CallbackHandler extends AbstractHandler {

        @Override
        public void handle(String target, Request request, HttpServletRequest servletRequest, HttpServletResponse response)
                throws IOException, ServletException {
            if (!DEFAULT_PATH.equals(target)) {
                return;
            }
            writeLandingHtml(response);
            response.flushBuffer();
            request.setHandled(true);
            lock.lock();
            try {
                responseMap = new HashMap<>();
                responseMap.putAll(request.getParameterMap());

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
