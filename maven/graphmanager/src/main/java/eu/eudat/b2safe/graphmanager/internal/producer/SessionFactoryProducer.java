package eu.eudat.b2safe.graphmanager.internal.producer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

@ApplicationScoped
public class SessionFactoryProducer {
	@Produces
	public SessionFactory createSessionFactory() {
		return new SessionFactory("eu.eudat.b2safe.graphmanager.model");
	}
	
	public void disposeSessionFactory(@Disposes SessionFactory factory) {
		factory.close();
	}
	
	@Produces
	public Session createSession(SessionFactory factory) {
		return factory.openSession();
	}
}
