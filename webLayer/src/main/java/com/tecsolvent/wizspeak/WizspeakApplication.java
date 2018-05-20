package com.tecsolvent.wizspeak;

import com.tecsolvent.wizspeak.filter.ExceptionFilter;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.DispatcherType;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @author sunil.kata
 * @since 16/01/16
 */
public class WizspeakApplication {
	/**
	 * The path to the web app on the server. Relative to root (/)
	 */
	private static final String CONTEXT_PATH = "/backend";

	private static final String PROJECT_RELATIVE_PATH_TO_WEBAPP = "src/main/webapp";

	private static Server server;
	private static int port = 8080;

	//private static final String LOG_PATH = "/var/log/wizspeak/backend/access.log";
	private static final String LOG_PATH = "/home/ubuntu/logs/backend/access.log";

	public static void main(String[] args) throws Exception {
		start();
	}

	public static void start() throws Exception {
		server = new Server();

		server.setConnectors(new Connector[]{createConnector(server)});
		server.setHandler(createHandlers());
		server.setStopAtShutdown(true);

		server.start();
	}

	public void join() throws InterruptedException {
		server.join();
	}

	public void stop() throws Exception {
		server.stop();
	}


	private static ServerConnector createConnector(Server server) {
		ServerConnector _connector =
				new ServerConnector(server);
		_connector.setPort(port);
		return _connector;
	}

	private static HandlerCollection createHandlers() {
		WebAppContext _ctx = new WebAppContext();
		_ctx.setContextPath(CONTEXT_PATH);

		_ctx.setWar(PROJECT_RELATIVE_PATH_TO_WEBAPP);
		_ctx.setDescriptor("WEB-INF/web.xml");

		_ctx.addFilter(ExceptionFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

		List<Handler> _handlers = new ArrayList<Handler>();

		_handlers.add(_ctx);

		HandlerList _contexts = new HandlerList();
		_contexts.setHandlers(_handlers.toArray(new Handler[0]));

		RequestLogHandler requestLogHandler = new RequestLogHandler();
		requestLogHandler.setRequestLog(createRequestLogs());

		HandlerCollection _result = new HandlerCollection();
		_result.setHandlers(new Handler[]{_contexts, requestLogHandler});

		return _result;
	}

	private static NCSARequestLog createRequestLogs() {
		NCSARequestLog requestLog = new NCSARequestLog(LOG_PATH);
		requestLog.setRetainDays(90);
		requestLog.setAppend(true);
		requestLog.setExtended(false);
		requestLog.setLogTimeZone("GMT");
		return requestLog;
	}
}
