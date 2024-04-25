package org.manager.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.manager.configuration.SessionFactoryUtill;
import org.manager.entity.Building;
import org.manager.entity.Contract;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class ContractDAO {

    public static void create(Contract contract){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.save(contract);
            transaction.commit();
        }
    }
    public static Contract getContractById(long id){
        Contract contract;
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            contract = session.get(Contract.class, id);
            transaction.commit();
        }
        return contract;
    }
    public static Set<Contract> getContracts(){
        Set<Contract> contracts;
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            contracts = new HashSet<>(session.createQuery("SELECT c FROM Contract c ", Contract.class).list());
            transaction.commit();
        }
        return contracts;
    }
    public static void update(Contract contract){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.merge(contract);
            transaction.commit();
        }
    }
    public static void delete(Contract contract){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.delete(contract);
            transaction.commit();
        }
    }
    public static void deleteById(long id){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            Contract contractToDelete = session.get(Contract.class, id);
            if(contractToDelete != null){
                session.delete(contractToDelete);
            }
            transaction.commit();
        }
    }

    public static Contract createContractForBuilding(
            Building building,
            BigDecimal baseTax,
            BigDecimal taxPerResident,
            BigDecimal taxPerPets,
            BigDecimal percentageForCompany,
            BigDecimal percentageForEmployee){
        Contract contract = new Contract();
        contract.setStartDate(LocalDate.now());
        contract.setEndDate(contract.getStartDate().plusYears(1));
        contract.setBaseTax(baseTax);
        contract.setTaxPerResident(taxPerResident);
        contract.setTaxPerPets(taxPerPets);
        contract.setPercentageForCompany(percentageForCompany);
        contract.setPercentageForEmployee(percentageForEmployee);
        //contract.setCompany(building.getCompany());
        contract.setBuilding(building);

        return contract;
    }

}
