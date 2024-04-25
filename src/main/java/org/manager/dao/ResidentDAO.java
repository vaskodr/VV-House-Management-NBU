package org.manager.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.manager.configuration.SessionFactoryUtill;
import org.manager.entity.Apartment;
import org.manager.entity.Building;
import org.manager.entity.Resident;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResidentDAO {

    public static void create(Resident resident){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.save(resident);
            transaction.commit();
        }
    }
    public static Resident getResidentById(long id){
        Resident resident;
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            resident = session.get(Resident.class, id);
            transaction.commit();
        }
        return resident;
    }
    public static Set<Resident> getResidents(){
        Set<Resident> residents;
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            residents = new HashSet<>(session.createQuery("SELECT r FROM Resident r ", Resident.class).list());
            transaction.commit();
        }
        return residents;
    }
    public static void update(Resident resident){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.merge(resident);
            transaction.commit();
        }
    }
    public static void delete(Resident resident){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.delete(resident);
            transaction.commit();
        }
    }
    public static void deleteById(long id){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            Resident residentToDelete = session.get(Resident.class, id);
            if(residentToDelete != null){
                session.delete(residentToDelete);
            }
            transaction.commit();
        }
    }

    public static List<Resident> getSortedResidentsByNameAndAge(){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Resident> cr = cb.createQuery(Resident.class);
            Root<Resident> root = cr.from(Resident.class);

            cr.orderBy(
                    cb.asc(root.get("name")),
                    cb.asc(root.get("dateOfBirth"))
            );

            return session.createQuery(cr).getResultList();
        }
    }

    public static Long getTotalResidentsInBuilding(Building building){
        Long totalResidents = null;
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            totalResidents = session.createQuery(
                            "SELECT COUNT(r.id) " +
                                    "FROM Building b " +
                                    "JOIN b.apartments a " +
                                    "JOIN a.residents r " +
                                    "WHERE b = :building", Long.class)
                    .setParameter("building", building)
                    .getSingleResult();
            transaction.commit();
        }
        return totalResidents;
    }

    public static List<Resident> getResidentsInBuilding(Building building){
        List<Resident> residentsInBuilding = null;
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            residentsInBuilding = session.createQuery(
                            "SELECT r " +
                                    "FROM Building b " +
                                    "JOIN b.apartments a " +
                                    "JOIN a.residents r " +
                                    "WHERE b = :building", Resident.class)
                    .setParameter("building", building)
                    .getResultList();
            transaction.commit();
        }
        return residentsInBuilding;
    }

    public static void addResidentToApartment(Resident resident, Apartment apartment) {
        if(!apartment.getResidents().contains(resident)){
            apartment.getResidents().add(resident);
            apartment.setResidents(apartment.getResidents());
            resident.setApartment(apartment);
            update(resident);
            ApartmentDAO.update(apartment);
            System.out.println("\nResident successfully added to Apartment with number " + apartment.getNumber() + " from building on address " + apartment.getBuilding().getAddress() + " !");
        } else{
            System.out.println("\nResindent already lives in Apartment with number " + apartment.getNumber() + " !");
        }
    }

    public static int calculateAge(LocalDate birthDate) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }

}
