package com.itsoul.lab.app;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import com.it.soul.lab.connect.JDBConnectionPool;
import com.it.soul.lab.sql.query.models.DataType;
import com.itsoul.lab.domains.ExistanceConsume;
import com.itsoul.lab.domains.ExistanceProduce;
import com.itsoul.lab.domains.FetchQuery;
import com.itsoul.lab.domains.Passenger;
import com.itsoul.lab.domains.PassengerList;
import com.itsoul.lab.interactors.Interactor;
import com.itsoul.lab.interactors.PassengerInteractor;
import com.itsoul.lab.interactors.PassengerInteractor.InteractorType;
import com.itsoul.lab.interactors.UserExistInteractor;
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
	private Interactor<PassengerList, FetchQuery> fetcher;
	private Interactor<ExistanceProduce, ExistanceConsume> isExist;

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
            
            fetcher = new PassengerInteractor(InteractorType.JPA);
            
            fetchMore.addClickListener(event -> {
            	FetchQuery query = new FetchQuery();
    			query.setTable("Passenger");
    			query.setOrderBy("id");
    			query.setLocation(0);
    			query.setSize(20);
    			query.addCriteria("name","tanvir",DataType.STRING);
            	PassengerList list = fetcher.fetch(query);
            	
            	//How to Use Lambda & Stream API of Java8
            	List<Passenger> items = list.getPassengerList();
            	items.stream()
            			.filter(p -> p.getAge() > 25 && p.getAge() <= 33)
            			.map(p -> p.getId() + ":" + p.getName())
            			.forEach(print -> System.out.println(print));
            	
            	//
            	List<String> names = items.stream()
            			.filter(p -> p.getAge() > 25)
            			.map(p -> p.getName())
            			.collect(Collectors.toList());
            	names.forEach(nm -> System.out.println(nm));
            	
            });
            
            layout.addComponent(fetchMore);
        });
        
        layout.addComponents(name, button);
        
        Button b2 = new Button("IsExist");
        b2.addClickListener(event -> {
        	ExistanceConsume consume = new ExistanceConsume();
        	consume.setTenantID("73da6b0a-affb-4ffb-9ab0-c6fd74b4eedd");
        	consume.setEmail("m.towhid@gmail.com");
        	isExist = new UserExistInteractor();
        	ExistanceProduce prod = isExist.fetch(consume);
        	System.out.println(prod.getIsExist());
        });
        
        layout.addComponent(b2);
        
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
