package org.manager.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.manager.configuration.SessionFactoryUtill;
import org.manager.entity.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class BuildingDAO {

    public static void create(Building building){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.save(building);
            transaction.commit();
        }
    }
    public static Building getBuildingById(long id){
        Building building;
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            building = session.get(Building.class, id);
            transaction.commit();
        }
        return building;
    }
    public static Set<Building> getBuildings(){
        Set<Building> buildings;
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            buildings = new HashSet<>(session.createQuery("SELECT b FROM Building b ", Building.class).list());
            transaction.commit();
        }
        return buildings;
    }
    public static void update(Building building){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.merge(building);
            transaction.commit();
        }
    }
    public static void delete(Building building){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.delete(building);
            transaction.commit();
        }
    }
    public static void deleteById(long id){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            Building buildingToDelete = session.get(Building.class, id);
            if(buildingToDelete != null){
                session.delete(buildingToDelete);
            }
            transaction.commit();
        }
    }

    public static Set<Apartment> getApartmentsFromBuilding(Building building) {
        if (building.getApartments() == null) {
            building.setApartments(new HashSet<>());
            update(building);
        }
        return building.getApartments();
    }

    public static void addBuildingToCompany(
            Building building,
            Company company,
            BigDecimal baseTax,
            BigDecimal taxPerResident,
            BigDecimal taxPerPets,
            BigDecimal percentageForCompany,
            BigDecimal percentageForEmployee){
        building.setCompany(company);
        Contract contract = ContractDAO.createContractForBuilding(
                building, baseTax, taxPerResident, taxPerPets, percentageForCompany, percentageForEmployee);
        building.setContract(contract);
        ContractDAO.create(contract);
        update(building);
        CompanyDAO.update(company);
    }

    public static void assignEmployeeToBuilding(Building building){
        Employee employeeToAssign = EmployeeDAO.findEmployeeWithLeastAssignedBuildings(building.getCompany());
        building.setEmployee(employeeToAssign);
        //building.getContract().setEmployee(employeeToAssign);
        employeeToAssign.getBuildings().add(building);
        update(building);
        ContractDAO.update(building.getContract());
        EmployeeDAO.update(employeeToAssign);
    }



}
