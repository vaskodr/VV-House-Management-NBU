package org.manager.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.manager.configuration.SessionFactoryUtill;
import org.manager.dto.EmployeeDTO;
import org.manager.entity.Building;
import org.manager.entity.Company;
import org.manager.entity.Employee;

import javax.persistence.criteria.*;
import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeDAO {
    public static void create(Employee employee) {
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(employee);
            transaction.commit();
        }
    }

    public static Employee getEmployeeById(long id) {
        Employee employee;
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            employee = session.get(Employee.class, id);
            transaction.commit();
        }
        return employee;
    }

    public static Set<Employee> getEmployees() {
        Set<Employee> employees;
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            employees = new HashSet<>(session.createQuery("SELECT e FROM Employee e ", Employee.class).list());
            transaction.commit();
        }
        return employees;
    }

    public static void update(Employee employee) {
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(employee);
            transaction.commit();
        }
    }

    public static void delete(Employee employee) {
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(employee);
            transaction.commit();
        }
    }

    public static void deleteById(long id) {
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Employee employeeToDelete = session.get(Employee.class, id);
            if (employeeToDelete != null) {
                session.delete(employeeToDelete);
            }
            transaction.commit();
        }
    }

    public static void hireEmployee(Employee employee, Company company) {
        if(!company.getEmployees().contains(employee)){
            employee.setCompany(company);
            update(employee);
            System.out.println("Employee hired to company successfully!");
        } else{
            System.out.println("Employee is already hired to that company!");
        }

    }

    public static void retireEmployee(Employee employee, Company company) {
        if(company.getEmployees().contains(employee)){
            employee.setCompany(null);
            company.getEmployees().remove(employee);
            update(employee);
            CompanyDAO.update(company);
            System.out.println("Employee retired from company successfully!");
        } else{
            System.out.println("Employee doesn't exists in that company!");
        }
    }

    public static List<EmployeeDTO> getCompanyEmployeesById(long id) {
        List<EmployeeDTO> employees;
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            employees = session.createQuery(
                            "select new org.manager.dto.EmployeeDTO(e.id, e.name) from Employee e" +
                                    " join e.company c " +
                                    "where c.id = :id",
                            EmployeeDTO.class)
                    .setParameter("id", id)
                    .getResultList();
            transaction.commit();
        }
        return employees;
    }

    public static List<Employee> getSortedEmployeesByNameAndBuildings(){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Employee> cr = cb.createQuery(Employee.class);
            Root<Employee> root = cr.from(Employee.class);

            cr.orderBy(
                    cb.asc(root.get("name")),
                    cb.desc(cb.size(root.get("buildings")))
            );

            return session.createQuery(cr).getResultList();
        }
    }

    public Long getTotalServicedBuildingsByEmployee(Company company) {
        Long totalServicedBuildings = null;
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            totalServicedBuildings = session.createQuery(
                            "SELECT COUNT(DISTINCT b.id) " +
                                    "FROM Employee e " +
                                    "LEFT JOIN e.buildings b " +
                                    "WHERE e.company = :company", Long.class)
                    .setParameter("company", company)
                    .getSingleResult();

            transaction.commit();
        }
        return totalServicedBuildings;
    }

    public static List<Building> getServicedBuildingsByEmployee(Company company) {
        List<Building> detailedServicedBuildings = null;

        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            detailedServicedBuildings = session.createQuery(
                            "SELECT DISTINCT b " +
                                    "FROM Employee e " +
                                    "LEFT JOIN FETCH e.buildings b " +
                                    "WHERE e.company = :company", Building.class)
                    .setParameter("company", company)
                    .getResultList();
            transaction.commit();
        }
        return detailedServicedBuildings;
    }


    public static Employee findEmployeeWithLeastAssignedBuildings(Company company) {
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Employee> cr = cb.createQuery(Employee.class);
            Root<Employee> employeeRoot = cr.from(Employee.class);
            Join<Employee, Building> buildingJoin = employeeRoot.join("buildings", JoinType.LEFT);
            cr.select(employeeRoot).distinct(true)
                    .where(
                            cb.and(
                                    cb.equal(employeeRoot.get("company"), company),
                                    cb.isNull(buildingJoin.get("id"))
                            )
                    )
                    .orderBy(
                            cb.asc(cb.size(employeeRoot.get("buildings"))),
                            cb.asc(employeeRoot.get("id"))
                    );
            Query<Employee> query = session.createQuery(cr);
            query.setMaxResults(1);
            List<Employee> result = query.getResultList();
            if (result.isEmpty()) {
                System.out.println("No available employee from the same company.");
                return null;
            }
            return result.get(0);
        }
    }

}
