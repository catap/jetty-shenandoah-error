import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

public class DemoServer {
    public static void main(String[] args) throws Exception {
        QueuedThreadPool threadPool = new QueuedThreadPool(2048, 256, 600000,
                new ArrayBlockingQueue<>(6000));
        Server server = new Server(threadPool);
        server.manage(threadPool);
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);

        Slf4jRequestLog requestLog = new Slf4jRequestLog();
        requestLog.setExtended(false);
        server.setRequestLog(requestLog);

        server.addBean(Log.getLog());

        HttpConfiguration config = new HttpConfiguration();
        config.setPersistentConnectionsEnabled(true);
        config.setOutputBufferSize(32768);
        config.setSendServerVersion(false);
        config.setSendXPoweredBy(false);

        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(config);

        ServerConnector serverConnectorHttp = new ServerConnector(server, httpConnectionFactory);
        serverConnectorHttp.setIdleTimeout(3600000);
        serverConnectorHttp.setPort(80);
        serverConnectorHttp.setAcceptQueueSize(100);

        server.setConnectors(new Connector[] { serverConnectorHttp });

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        context.setMaxFormContentSize(24576);

        context.addServlet(new ServletHolder("test", new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.addHeader("Connection", "keep-alive");
                resp.setStatus(200);
            }
        }), "/test/");

        server.start();
        server.join();
    }
}
