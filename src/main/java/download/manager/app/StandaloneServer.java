package download.manager.app;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.sun.jersey.spi.container.servlet.ServletContainer;

public class StandaloneServer {

    public static void main(String[] args) throws Exception {
	ServletHolder sh = new ServletHolder(ServletContainer.class);
	sh.setInitParameter("com.sun.jersey.config.property.resourceConfigClass",
		"com.sun.jersey.api.core.PackagesResourceConfig");
	sh.setInitParameter("com.sun.jersey.config.property.packages", "download.manager.controller");
	sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
	Server server = new Server(8080);
	ServletContextHandler sch = new ServletContextHandler(server, "/");
	sch.addServlet(sh, "/*");
	server.start();
	server.join();
    }

}
