package com.itsoul.lab.app;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import com.it.soul.lab.connect.JDBConnectionPool;
import com.itsoul.lab.domains.Passenger;
import com.itsoul.lab.domains.PassengerList;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("AppTheme")
public class WebApp extends UI {

	private static final long serialVersionUID = 1L;
	private PassengerList fetcher;

	@Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        
        final TextField name = new TextField();
        name.setCaption("Type your name here:");

        Button button = new Button("Click Me");
        button.addClickListener(e -> {
            layout.addComponent(new Label("Thanks " + name.getValue() 
                    + ", it works!"));
            Button fetchMore = new Button("Fetch Passengers");
            fetcher = new PassengerList();
            fetchMore.addClickListener(fetcher);
            layout.addComponent(fetchMore);
        });
        
        layout.addComponents(name, button);
        
        setContent(layout);
    }
	
	@Override
	public void detach() {
		super.detach();
		fetcher = null;
	}

    @WebServlet(urlPatterns = "/*", name = "WebAppServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = WebApp.class, productionMode = false)
    public static class WebAppServlet extends VaadinServlet {

		private static final long serialVersionUID = 1L;
		
		@Override
		protected void servletInitialized() throws ServletException {
			super.servletInitialized();
			//Initialized Our Resource Here:
			JDBConnectionPool.configure("java:comp/env/jdbc/testDB");
			System.out.println("jdbc/testDB has been loaded.");
			//
			JPAResourceLoader.configure();
			System.out.println("EntiryManager for testDB is created.");
		}
    }
}
