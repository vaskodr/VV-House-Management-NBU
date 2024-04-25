package org.manager.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.manager.configuration.SessionFactoryUtill;
import org.manager.entity.Apartment;
import org.manager.entity.Building;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApartmentDAO {

    public static void create(Apartment apartment){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.save(apartment);
            transaction.commit();
        }
    }
    public static Apartment getApartmentById(long id){
        Apartment apartment;
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            apartment = session.get(Apartment.class, id);
            transaction.commit();
        }
        return apartment;
    }
    public static Set<Apartment> getApartments(){
        Set<Apartment> apartments;
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            apartments = new HashSet<>(session.createQuery("SELECT a FROM Apartment a ", Apartment.class).list());
            transaction.commit();
        }
        return apartments;
    }
    public static void update(Apartment apartment){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.merge(apartment);
            transaction.commit();
        }
    }
    public static void delete(Apartment apartment){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.delete(apartment);
            transaction.commit();
        }
    }
    public static void deleteById(long id){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            Apartment apartmentToDelete = session.get(Apartment.class, id);
            if(apartmentToDelete != null){
                session.delete(apartmentToDelete);
            }
            transaction.commit();
        }
    }

    public static Long getTotalApartmentsInBuilding(Building building){
        Long totalApartments = null;
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            totalApartments = session.createQuery(
                            "SELECT COUNT(a.id) " +
                                    "FROM Building b " +
                                    "JOIN b.apartments a " +
                                    "WHERE b = :building", Long.class)
                    .setParameter("building", building)
                    .getSingleResult();

            transaction.commit();
        }
        return totalApartments;
    }

    public static List<Apartment> getApartmentsInBuilding(Building building){
        List<Apartment> apartmentsInBuilding = null;
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            apartmentsInBuilding = session.createQuery(
                            "SELECT a " +
                                    "FROM Building b " +
                                    "JOIN b.apartments a " +
                                    "WHERE b = :building", Apartment.class)
                    .setParameter("building", building)
                    .getResultList();
            transaction.commit();
        }
        return apartmentsInBuilding;
    }

    public static void addApartmentToBuilding(Apartment apartment, Building building){
        if(!building.getApartments().contains(apartment)){
            building.getApartments().add(apartment);
            building.setApartments(building.getApartments());
            apartment.setBuilding(building);
            update(apartment);
            BuildingDAO.update(building);
        } else{
            System.out.println("Apartment already exists in that building!");
        }
    }

}
