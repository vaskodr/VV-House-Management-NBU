package org.manager.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.manager.configuration.SessionFactoryUtill;
import org.manager.entity.Apartment;
import org.manager.entity.Owner;

import java.util.HashSet;
import java.util.Set;

public class OwnerDAO {
    public static void create(Owner apartmentOwner){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.save(apartmentOwner);
            transaction.commit();
        }
    }
    public static Owner getOwnerById(long id){
        Owner apartmentOwner;
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            apartmentOwner = session.get(Owner.class, id);
            transaction.commit();
        }
        return apartmentOwner;
    }
    public static Set<Owner> getOwners(){
        Set<Owner> apartmentOwners;
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            apartmentOwners = new HashSet<>(session.createQuery("SELECT o FROM Owner o ", Owner.class).list());
            transaction.commit();
        }
        return apartmentOwners;
    }
    public static void update(Owner apartmentOwner){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.merge(apartmentOwner);
            transaction.commit();
        }
    }
    public static void delete(Owner apartmentOwner){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.delete(apartmentOwner);
            transaction.commit();
        }
    }
    public static void deleteById(long id){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            Owner ownerToDelete = session.get(Owner.class, id);
            if(ownerToDelete != null){
                session.delete(ownerToDelete);
            }
            transaction.commit();
        }
    }
    public static void addOwnerToApartment(Owner apartmentOwner, Apartment apartment){
        apartmentOwner.getOwnedApartments().add(apartment);
        apartmentOwner.setOwnedApartments(apartmentOwner.getOwnedApartments());
        apartment.getApartmentOwners().add(apartmentOwner);
        apartment.setApartmentOwners(apartment.getApartmentOwners());
        update(apartmentOwner);
        ApartmentDAO.update(apartment);
    }
}
