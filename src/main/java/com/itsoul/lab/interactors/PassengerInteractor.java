package com.itsoul.lab.interactors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import javax.persistence.Query;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.it.soul.lab.connect.JDBConnectionPool;
import com.it.soul.lab.jpql.query.JPQLQuery;
import com.it.soul.lab.jpql.query.JPQLSelectQuery;
import com.it.soul.lab.service.ORMService;
import com.it.soul.lab.sql.SQLExecutor;
import com.it.soul.lab.sql.query.SQLQuery;
import com.it.soul.lab.sql.query.SQLQuery.QueryType;
import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.it.soul.lab.sql.query.models.AndExpression;
import com.it.soul.lab.sql.query.models.Expression;
import com.it.soul.lab.sql.query.models.ExpressionInterpreter;
import com.it.soul.lab.sql.query.models.Operator;
import com.it.soul.lab.sql.query.models.Property;
import com.it.soul.lab.sql.query.models.Row;
import com.it.soul.lab.sql.query.models.Table;
import com.itsoul.lab.app.JPAResourceLoader;
import com.itsoul.lab.domains.Criteria;
import com.itsoul.lab.domains.FetchQuery;
import com.itsoul.lab.domains.Passenger;
import com.itsoul.lab.domains.PassengerList;

public class PassengerInteractor implements Interactor<PassengerList, FetchQuery>{
	
	public enum InteractorType{
		RESTClient,
		JPA,
		JDBC
	}
	
	private WebTarget target;
	private InteractorType type;

	public PassengerInteractor(InteractorType type) {
		super();
		Client cl = ClientBuilder.newClient();
		target = cl.target(getJerseyExample()+"passenger/JPA");
		this.type = type;
	}
	
	public PassengerList fetch(FetchQuery query) {
		if (type == InteractorType.RESTClient) {
			return fetchPasserger(query, MediaType.APPLICATION_JSON_TYPE);
		} 
		else if(type == InteractorType.JPA) {
			ORMService<Passenger> service = new ORMService<>(JPAResourceLoader.entityManager(), Passenger.class);
			try {
				PassengerList list = new PassengerList();
				ExpressionInterpreter and = createExpression(query);
				JPQLSelectQuery jpql  = null;
				if(and == null) {
					jpql = new JPQLQuery.Builder(QueryType.SELECT)
							.columns()
							.from(service.getEntity())
							.orderBy("id")
							.build();
				}else {
					jpql = new JPQLQuery.Builder(QueryType.SELECT)
							.columns()
							.from(service.getEntity())
							.where(and)
							.orderBy("id")
							.build();
				}
				Query iQuery = service.getEntityManager().createQuery(jpql.toString());
				if(and != null) {
					for (Expression item : and.resolveExpressions()) {
						iQuery.setParameter(item.getProperty(), item.getValueProperty().getValue());
					}
				}
				iQuery.setFirstResult(query.getOffset());
				iQuery.setMaxResults(query.getLimit());
				@SuppressWarnings("unchecked")
				List<Passenger> passengers = iQuery.getResultList();
				list.setPassengerList(passengers);
				return list;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		else {
			try {
				Connection conn = JDBConnectionPool.connection("testDB");
				SQLExecutor exe = new SQLExecutor(conn);
				ExpressionInterpreter and = createExpression(query);
				SQLSelectQuery queryx = null;
				if(and == null) {
					queryx = new SQLQuery.Builder(QueryType.SELECT)
							.columns()
							.from(query.getTable())
							.orderBy(query.getOrderBy())
							.addLimit(query.getLimit(), query.getOffset())
							.build();
				}else {
					queryx = new SQLQuery.Builder(QueryType.SELECT)
							.columns()
							.from(query.getTable())
							.where(and)
							.orderBy(query.getOrderBy())
							.addLimit(query.getLimit(), query.getOffset())
							.build();
				}
				ResultSet set = exe.executeSelect(queryx);
				Table items = exe.collection(set);
				exe = null;//exe.close(); //automatically called when executor object is garbage collected.
				
				PassengerList list = new PassengerList();
				for (Row item : items.getRows()) {
					list.add(item.inflate(Passenger.class));
				}
				return list;
			}catch(Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private ExpressionInterpreter createExpression(FetchQuery query) {
		ExpressionInterpreter and = null;
		ExpressionInterpreter lhr = null;
		for (Criteria element : query.getCriterias()) {
			Property prop = element.getProperty();
			if(lhr == null) {
				lhr = new Expression(prop, Operator.EQUAL);
				and = lhr;
			}else {
				ExpressionInterpreter rhr = new Expression(prop, Operator.EQUAL);
				and = new AndExpression(lhr, rhr);
				lhr = and;
			}
		}
		return and;
	}
	
	public PassengerList fetchPasserger(FetchQuery query, MediaType mediaType) {
		Response response = target.request(mediaType).post(Entity.entity(query, mediaType));
		//System.out.print(response.toString());
		PassengerList list = response.readEntity(PassengerList.class);
		return list;
	}
	
}
