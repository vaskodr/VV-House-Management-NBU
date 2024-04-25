package org.manager.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.manager.configuration.SessionFactoryUtill;
import org.manager.dto.*;
import org.manager.entity.*;

import javax.persistence.criteria.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class MonthTaxDAO {

    public static void create(MonthTax tax){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.save(tax);
            transaction.commit();
        }
    }
    public static MonthTax getTaxById(long id){
        MonthTax tax;
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            tax = session.get(MonthTax.class, id);
            transaction.commit();
        }
        return tax;
    }
    public static Set<MonthTax> getTaxes(){
        Set<MonthTax> taxes;
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            taxes = new HashSet<>(session.createQuery("SELECT t FROM MonthTax t ", MonthTax.class).list());
            transaction.commit();
        }
        return taxes;
    }
    public static void update(MonthTax tax){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.merge(tax);
            transaction.commit();
        }
    }
    public static void delete(MonthTax tax){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.delete(tax);
            transaction.commit();
        }
    }
    public static void deleteById(long id){
        try(Session session = SessionFactoryUtill.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            MonthTax taxToDelete = session.get(MonthTax.class, id);
            if(taxToDelete != null){
                session.delete(taxToDelete);
            }
            transaction.commit();
        }
    }

    public static TotalAmountToPayDTO getTotalAmountToPayForCompany(Company company) {
        TotalAmountToPayDTO totalAmountToPayDTO = null;
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<BigDecimal> cr = cb.createQuery(BigDecimal.class);
            Root<Company> companyRoot = cr.from(Company.class);
            Root<Building> buildingRoot = cr.from(Building.class);
            Root<MonthTax> monthTaxRoot = cr.from(MonthTax.class);
            cr.select(cb.sum(monthTaxRoot.get("totalAmountToPay")))
                    .where(
                            cb.equal(companyRoot, company),
                            cb.equal(companyRoot.get("id"), buildingRoot.get("company")),
                            cb.equal(buildingRoot.get("id"), monthTaxRoot.get("building")),
                            cb.not(monthTaxRoot.get("paid"))
                    );
            Query<BigDecimal> query = session.createQuery(cr);
            BigDecimal result = query.uniqueResult();
            if (result != null) {
                totalAmountToPayDTO = new TotalAmountToPayDTO(result);
            }
        }
        return totalAmountToPayDTO;
    }

    public static List<MonthTaxDTO> getTaxesToPayForCompany(Company company) {
        List<MonthTaxDTO> unpaidTaxesList = new ArrayList<>();
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<MonthTaxDTO> cr = cb.createQuery(MonthTaxDTO.class);
            Root<MonthTax> root = cr.from(MonthTax.class);
            cr.select(cb.construct(
                    MonthTaxDTO.class,
                    root.get("id"),
                    root.get("totalAmountToPay"),
                    root.get("paid")
            ));
            Join<MonthTax, Building> buildingJoin = root.join("building");
            Join<Building, Company> companyJoin = buildingJoin.join("company");
            Predicate companyPredicate = cb.equal(companyJoin, company);
            Predicate unpaidPredicate = cb.notEqual(root.get("paid"), true);
            cr.where(companyPredicate, unpaidPredicate);
            List<MonthTaxDTO> unpaidTaxesDTO = session.createQuery(cr).getResultList();
            unpaidTaxesList.addAll(unpaidTaxesDTO);
        }
        return unpaidTaxesList;
    }

    public static BuildingTotalAmountDTO getTotalAmountToBePaidForBuilding(Building building) {
        BuildingTotalAmountDTO buildingTotalAmountDTO = null;
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<BuildingTotalAmountDTO> cr = cb.createQuery(BuildingTotalAmountDTO.class);
            Root<Building> root = cr.from(Building.class);
            Join<Building, MonthTax> monthTaxJoin = root.join("monthTaxes");
            cr.select(cb.construct(
                    BuildingTotalAmountDTO.class,
                    cb.sum(monthTaxJoin.get("totalAmountToPay"))
            ));
            Predicate buildingPredicate = cb.equal(root, building);
            Predicate unpaidPredicate = cb.not(monthTaxJoin.get("paid"));
            cr.where(buildingPredicate, unpaidPredicate);
            buildingTotalAmountDTO = session.createQuery(cr).uniqueResult();
        }
        return buildingTotalAmountDTO;
    }

//    public Map<Building, List<MonthTax>> getTaxesToPayForBuildings(List<Building> buildings) {
//        Map<Building, List<MonthTax>> unpaidTaxesMap = new HashMap<>();
//        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
//            Transaction transaction = session.beginTransaction();
//            for (Building building : buildings) {
//                List<MonthTax> unpaidTaxes = session.createQuery(
//                                "SELECT t " +
//                                        "FROM Building b " +
//                                        "JOIN b.monthTaxes t " +
//                                        "WHERE b = :building AND NOT t.paid", MonthTax.class)
//                        .setParameter("building", building)
//                        .getResultList();
//                unpaidTaxesMap.put(building, unpaidTaxes);
//            }
//            transaction.commit();
//        }
//        return unpaidTaxesMap;
//    }

    public static List<MonthTaxDTO> getTaxesToPayForBuilding(Building building) {
        List<MonthTaxDTO> unpaidTaxesDTO;
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<MonthTaxDTO> cr = cb.createQuery(MonthTaxDTO.class);
            Root<MonthTax> root = cr.from(MonthTax.class);
            cr.select(cb.construct(
                    MonthTaxDTO.class,
                    root.get("id"),
                    root.get("totalAmountToPay"),
                    root.get("paid")
            ));
            Join<MonthTax, Building> buildingJoin = root.join("building");
            Predicate buildingPredicate = cb.equal(buildingJoin, building);
            Predicate unpaidPredicate = cb.notEqual(root.get("paid"), true);
            cr.where(buildingPredicate, unpaidPredicate);
            unpaidTaxesDTO = session.createQuery(cr).getResultList();
        }
        return unpaidTaxesDTO;
    }

    public static TotalAmountToPayDTO getTotalAmountToBePaidForEmployee(Employee employee) {
        TotalAmountToPayDTO totalAmountToPayDTO = null;
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<TotalAmountToPayDTO> cr = cb.createQuery(TotalAmountToPayDTO.class);
            Root<Employee> root = cr.from(Employee.class);
            Join<Employee, Building> buildingJoin = root.join("buildings", JoinType.LEFT);
            Join<Building, MonthTax> monthTaxJoin = buildingJoin.join("monthTaxes", JoinType.LEFT);
            Join<Building, Contract> contractJoin = buildingJoin.join("contract", JoinType.LEFT);
            // Calculation
            Expression<BigDecimal> totalAmountExpression = cb.sum(
                    cb.prod(
                            monthTaxJoin.get("totalAmountToPay"),
                            contractJoin.get("percentageForEmployee")
                    )
            );
            cr.select(cb.construct(
                    TotalAmountToPayDTO.class,
                    totalAmountExpression
            ));
            Predicate employeePredicate = cb.equal(root, employee);
            Predicate unpaidPredicate = cb.not(monthTaxJoin.get("paid"));
            cr.where(employeePredicate, unpaidPredicate);
            totalAmountToPayDTO = session.createQuery(cr).uniqueResult();
        }
        return totalAmountToPayDTO;
    }

    public static List<MonthTaxDTO> getTaxesToPayForEmployee(Employee employee) {
        List<MonthTaxDTO> unpaidTaxesList = null;
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<MonthTaxDTO> cr = cb.createQuery(MonthTaxDTO.class);
            Root<Employee> root = cr.from(Employee.class);
            Join<Employee, Building> buildingJoin = root.join("buildings");
            Join<Building, MonthTax> monthTaxJoin = buildingJoin.join("monthTaxes");
            Join<Building, Contract> contractJoin = buildingJoin.join("contract");
            // Calculation
            Expression<BigDecimal> totalAmountExpression = cb.prod(
                    monthTaxJoin.get("totalAmountToPay"),
                    contractJoin.get("percentageForEmployee")
            );
            cr.select(cb.construct(
                    MonthTaxDTO.class,
                    monthTaxJoin.get("id"),
                    totalAmountExpression,
                    monthTaxJoin.get("paid")
            ));
            Predicate employeePredicate = cb.equal(root, employee);
            Predicate unpaidPredicate = cb.notEqual(monthTaxJoin.get("paid"), true);
            cr.where(employeePredicate, unpaidPredicate);
            unpaidTaxesList = session.createQuery(cr).getResultList();
        }
        return unpaidTaxesList;
    }

    public static BigDecimal getAmountPaidForCompany(Company company) {
        BigDecimal totalAmountPaid = null;
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<BigDecimal> cr = cb.createQuery(BigDecimal.class);
            Root<Company> root = cr.from(Company.class);
            Join<Company, Building> buildingJoin = root.join("buildings");
            Join<Building, MonthTax> monthTaxJoin = buildingJoin.join("monthTaxes");
            cr.select(cb.coalesce(
                    cb.sum(monthTaxJoin.get("paidAmount")),
                    BigDecimal.ZERO
            ));
            Predicate companyPredicate = cb.equal(root, company);
            Predicate paidPredicate = cb.isTrue(monthTaxJoin.get("paid"));
            cr.where(companyPredicate, paidPredicate);
            totalAmountPaid = session.createQuery(cr).uniqueResult();
        }
        return totalAmountPaid;
    }

    public static List<MonthTax> getPaidMonthTaxesForCompany(Company company) {
        List<MonthTax> paidTaxes = null;
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<MonthTax> cr = cb.createQuery(MonthTax.class);
            Root<Company> root = cr.from(Company.class);
            Join<Company, Building> buildingJoin = root.join("buildings");
            Join<Building, MonthTax> monthTaxJoin = buildingJoin.join("monthTaxes");
            cr.select(monthTaxJoin);
            Predicate companyPredicate = cb.equal(root, company);
            Predicate paidPredicate = cb.isTrue(monthTaxJoin.get("paid"));
            cr.where(companyPredicate, paidPredicate);
            paidTaxes = session.createQuery(cr).getResultList();
        }
        return paidTaxes;
    }

    public static BigDecimal getAmountPaidForBuilding(Building building) {
        BigDecimal totalAmountPaid = null;
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<BigDecimal> cr = cb.createQuery(BigDecimal.class);
            Root<Building> root = cr.from(Building.class);
            Join<Building, MonthTax> monthTaxJoin = root.join("monthTaxes");
            cr.select(cb.coalesce(
                    cb.sum(monthTaxJoin.get("paidAmount")), BigDecimal.ZERO));
            Predicate buildingPredicate = cb.equal(root, building);
            Predicate paidPredicate = cb.isTrue(monthTaxJoin.get("paid"));
            cr.where(buildingPredicate, paidPredicate);
            totalAmountPaid = session.createQuery(cr).uniqueResult();
        }
        return totalAmountPaid;
    }

    public static List<MonthTax> getPaidMonthTaxesForBuilding(Building building) {
        List<MonthTax> paidTaxes = null;
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<MonthTax> cr = cb.createQuery(MonthTax.class);
            Root<Building> root = cr.from(Building.class);
            Join<Building, MonthTax> monthTaxJoin = root.join("monthTaxes");
            cr.select(monthTaxJoin);
            Predicate buildingPredicate = cb.equal(root, building);
            Predicate paidPredicate = cb.isTrue(monthTaxJoin.get("paid"));
            cr.where(buildingPredicate, paidPredicate);
            paidTaxes = session.createQuery(cr).getResultList();
        }
        return paidTaxes;
    }


    public static BigDecimal getAmountPaidForEmployee(Employee employee) {
        BigDecimal totalAmountPaid = null;
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<BigDecimal> cr = cb.createQuery(BigDecimal.class);
            Root<Employee> root = cr.from(Employee.class);
            Join<Employee, Building> buildingJoin = root.join("buildings", JoinType.LEFT);
            Join<Building, MonthTax> monthTaxJoin = buildingJoin.join("monthTaxes", JoinType.LEFT);
            cr.select(cb.coalesce(
                    cb.sum(monthTaxJoin.get("paidAmount")),
                    BigDecimal.ZERO
            ));
            Predicate employeePredicate = cb.equal(root, employee);
            Predicate paidPredicate = cb.isTrue(monthTaxJoin.get("paid"));
            cr.where(employeePredicate, paidPredicate);
            totalAmountPaid = session.createQuery(cr).uniqueResult();
        }
        return totalAmountPaid;
    }

    public static List<MonthTax> getPaidMonthTaxesForEmployee(Employee employee) {
        List<MonthTax> paidTaxes = null;
        try (Session session = SessionFactoryUtill.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<MonthTax> cr = cb.createQuery(MonthTax.class);
            Root<Employee> root = cr.from(Employee.class);
            Join<Employee, Building> buildingJoin = root.join("buildings", JoinType.LEFT);
            Join<Building, MonthTax> monthTaxJoin = buildingJoin.join("monthTaxes", JoinType.LEFT);
            cr.select(monthTaxJoin);
            Predicate employeePredicate = cb.equal(root, employee);
            cr.where(employeePredicate, cb.isTrue(monthTaxJoin.get("paid")));
            paidTaxes = session.createQuery(cr).getResultList();
        }
        return paidTaxes;
    }

    public static void payTax(Building building) {
        BigDecimal incomeForCompany = BigDecimal.ZERO;
        BigDecimal incomeForEmployee = BigDecimal.ZERO;
        MonthTax tax = building.getMonthTaxes().stream()
                .min(Comparator.comparingInt(t ->
                        Math.toIntExact(Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), t.getDateOfTaxIssue())))))
                .orElse(null);
        if (tax != null) {
            System.out.println(tax.getId());
            if (tax.isPaid()) {
                System.out.println("Taxes for the building at address "
                        + building.getAddress()
                        + " have already been paid. No further actions needed.");
            } else {
                tax.setPaidAmount(tax.getTotalAmountToPay());
                tax.setPaid(true);
                tax.setPaidDateAndTime(LocalDateTime.now());
                update(tax);
                building.getMonthTaxes().add(tax);
            }
            if(building.getCompany().getIncomeFromTaxes() == null){
                building.getCompany().setIncomeFromTaxes(incomeForCompany);
            }

            incomeForCompany = building.getCompany().getIncomeFromTaxes()
                    .add(tax.getPaidAmount()
                            .multiply(building.getContract().getPercentageForCompany()));
            building.getCompany().setIncomeFromTaxes(incomeForCompany);
            CompanyDAO.update(building.getCompany());

            if(building.getEmployee().getSalary() == null){
                building.getEmployee().setSalary(incomeForEmployee);
            }

            incomeForEmployee = building.getEmployee().getSalary()
                    .add(tax.getPaidAmount()
                            .multiply(building.getContract().getPercentageForEmployee()));
            building.getEmployee().setSalary(incomeForEmployee);
            EmployeeDAO.update(building.getEmployee());
            recordTaxPaymentToFile(building);
            System.out.println(building.getCompany().getIncomeFromTaxes());
            System.out.println(building.getEmployee().getSalary());
        } else{
            System.out.println("Couldn't find a tax!");
        }
    }


    public static void introduceTaxForBuilding(Building building){
        BigDecimal totalTaxForBuilding = calculateTaxForBuilding(building);
        BigDecimal paidAmount = BigDecimal.ZERO;
        boolean status = false;
        MonthTax tax = new MonthTax();
        tax.setDateOfTaxIssue(LocalDate.now());
        tax.setTotalAmountToPay(totalTaxForBuilding);
        tax.setPaidAmount(paidAmount);
        tax.setPaid(status);
        tax.setBuilding(building);
        building.getMonthTaxes().add(tax);
        BuildingDAO.update(building);
        create(tax);
    }

    public static BigDecimal calculateTaxForBuilding(Building building) {
        BigDecimal taxForBuilding = BigDecimal.ZERO;
        for (Apartment apartment : building.getApartments()) {
            BigDecimal taxForApartment = calculateTaxForApartment(apartment);
            taxForBuilding = taxForBuilding.add(taxForApartment);
        }
        System.out.print("\nTax for Building on address " + building.getAddress() + " is: ");
        return taxForBuilding;
    }

    public static BigDecimal calculateTaxForApartment(Apartment apartment) {
        BigDecimal baseTax = calculateBaseTax(apartment);
        BigDecimal taxForResidents = calculateTaxPerResident(apartment);
        BigDecimal taxPerPets = calculateTaxPerPets(apartment);

        return baseTax
                .add(taxForResidents)
                .add(taxPerPets);
    }

    public static BigDecimal calculateBaseTax(Apartment apartment) {
        return BigDecimal.valueOf(apartment.getArea())
                .multiply(apartment.getBuilding().getContract().getBaseTax());

    }

    public static BigDecimal calculateTaxPerResident(Apartment apartment) {
        long residentsOver7Years = 0;
        if (apartment.getResidents() != null) {
            residentsOver7Years = apartment.getResidents().stream()
                    .filter(resident -> ResidentDAO.calculateAge(resident.getDateOfBirth()) > 7 && resident.isUseElevator())
                    .count();
        }
        return BigDecimal.valueOf(residentsOver7Years)
                .multiply(apartment.getBuilding().getContract().getTaxPerResident());
    }


    public static BigDecimal calculateTaxPerPets(Apartment apartment) {
        return BigDecimal.valueOf(apartment.getPets())
                .multiply(apartment.getBuilding().getContract().getTaxPerPets());
    }

    private static void recordTaxPaymentToFile(Building building) {
        String fileName = "tax_payment_record.txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            writer.println("Tax Payment Record:");
            writer.println("Date and Time: " + LocalDateTime.now());
            writer.println("Building Address: " + building.getAddress());
            writer.println("Company Income from Taxes: " + building.getCompany().getIncomeFromTaxes());
            writer.println("Employee Salary: " + building.getEmployee().getSalary());
            writer.println("Total Amount: " + building.getCompany().getIncomeFromTaxes().add(building.getEmployee().getSalary()));
            writer.println("-------------------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
