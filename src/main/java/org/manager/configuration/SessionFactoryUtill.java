package org.manager.configuration;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.manager.entity.*;

public class SessionFactoryUtill {
    private static SessionFactory sessionFactory;
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(Company.class);
            configuration.addAnnotatedClass(Employee.class);
            configuration.addAnnotatedClass(Apartment.class);
            configuration.addAnnotatedClass(Building.class);
            configuration.addAnnotatedClass(Resident.class);
            configuration.addAnnotatedClass(Owner.class);
            configuration.addAnnotatedClass(MonthTax.class);
            configuration.addAnnotatedClass(Contract.class);
            ServiceRegistry serviceRegistry
                    = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }
        return sessionFactory;
    }
}
