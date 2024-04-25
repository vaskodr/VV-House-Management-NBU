package org.manager.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.manager.configuration.SessionFactoryUtill;
import org.manager.entity.Company;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompanyDAO {
    public static void create(Company company){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.save(company);
            transaction.commit();
        }
    }
    public static Company getCompanyById(long id){
        Company company;
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            company = session.get(Company.class, id);
            transaction.commit();
        }
        return company;
    }
    public static Set<Company> getCompanies(){
        Set<Company> companies;
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            companies = new HashSet<>(session.createQuery("SELECT c FROM Company c ", Company.class).list());
            transaction.commit();
        }
        return companies;
    }
    public static void update(Company company){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.merge(company);
            transaction.commit();
        }
    }
    public static void delete(Company company){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.delete(company);
            transaction.commit();
        }
    }
    public static void deleteById(long id){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            Company companyToDelete = session.get(Company.class, id);
            if(companyToDelete != null){
                session.delete(companyToDelete);
            }
            transaction.commit();
        }
    }

    public List<Company> getSortedCompaniesByRevenue() {
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Company> cr = cb.createQuery(Company.class);
            Root<Company> root = cr.from(Company.class);

            cr.orderBy(cb.desc(root.get("incomeFromTaxes")));

            return session.createQuery(cr).getResultList();
        }
    }



}
