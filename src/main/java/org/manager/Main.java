package org.manager;

import org.manager.configuration.SessionFactoryUtill;
import org.manager.dao.*;
import org.manager.dto.BuildingTotalAmountDTO;
import org.manager.dto.MonthTaxDTO;
import org.manager.dto.TotalAmountToPayDTO;
import org.manager.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
       SessionFactoryUtill.getSessionFactory().openSession();
       System.out.println("\n\n\nHOUSE_MANAGER_APP\n=======================\n\n");

       Company c1 = new Company("Alpha");
       Company c2 = new Company("Vision");
       Company c3 = new Company("Housing");
       Company c4 = new Company("VirtualHousing");
       CompanyDAO.create(c1);
       CompanyDAO.create(c2);
       CompanyDAO.create(c3);
       CompanyDAO.create(c4);
       Employee e1 = new Employee("Ivan Ivanov", LocalDate.of(1999, 8, 10));
       Employee e2 = new Employee("Yordan Yordanov", LocalDate.of(1999, 8, 10));
       Employee e3 = new Employee("Dimitar Todorov", LocalDate.of(1999, 8, 10));
       Employee e4 = new Employee("Valentin Yordanov", LocalDate.of(1999, 8, 10));
       EmployeeDAO.create(e1);
       EmployeeDAO.create(e2);
       EmployeeDAO.create(e3);
       EmployeeDAO.create(e4);
       EmployeeDAO.hireEmployee(e1, c1);
       EmployeeDAO.hireEmployee(e2, c1);
       EmployeeDAO.hireEmployee(e3, c2);
       EmployeeDAO.hireEmployee(e4, c2);
       Building b1 = new Building("Vorino 15");
       Apartment a1b1 = new Apartment(1, 1, 75);
       Apartment a2b1 = new Apartment(1, 2, 56);
       Apartment a3b1 = new Apartment(2, 3, 120);
       Apartment a4b1 = new Apartment(2, 4, 40);
       Resident r1a1b1 = new Resident("Galya Georgieva", LocalDate.of(1977, 3, 15));
       r1a1b1.setUseElevator(true);
       Resident r2a1b1 = new Resident("Ivan Georgiev", LocalDate.of(1975, 12, 3));
       r2a1b1.setUseElevator(true);
       Resident r3a2b1 = new Resident("Galin Mitev", LocalDate.of(2000, 5, 18));
       Resident r4a3b1 = new Resident("Georgi Toshev", LocalDate.of(1985, 7, 26));
       Resident r5a3b1 = new Resident("Valentina Tosheva", LocalDate.of(2020, 7, 11));
       BuildingDAO.create(b1);


       BuildingDAO.addBuildingToCompany(
               b1, c1,
               BigDecimal.valueOf(0.5),
               BigDecimal.valueOf(1),
               BigDecimal.valueOf(0.5),
               BigDecimal.valueOf(0.7),
               BigDecimal.valueOf(0.3)
       );

       ApartmentDAO.create(a1b1);
       ApartmentDAO.create(a2b1);
       ApartmentDAO.create(a3b1);
       ApartmentDAO.create(a4b1);

       ApartmentDAO.addApartmentToBuilding(a1b1, b1);
       ApartmentDAO.addApartmentToBuilding(a2b1, b1);
       ApartmentDAO.addApartmentToBuilding(a3b1, b1);
       ApartmentDAO.addApartmentToBuilding(a4b1, b1);

       ResidentDAO.create(r1a1b1);
       ResidentDAO.create(r2a1b1);
       ResidentDAO.create(r3a2b1);
       ResidentDAO.create(r4a3b1);
       ResidentDAO.create(r5a3b1);

       ResidentDAO.addResidentToApartment(r1a1b1, a1b1);
       ResidentDAO.addResidentToApartment(r2a1b1, a1b1);
       ResidentDAO.addResidentToApartment(r3a2b1, a2b1);
       ResidentDAO.addResidentToApartment(r4a3b1, a3b1);
       ResidentDAO.addResidentToApartment(r5a3b1, a3b1);

       Owner a1Owner = new Owner("Konstantin Radev", LocalDate.of(2000, 2, 8));
       OwnerDAO.create(a1Owner);
       OwnerDAO.addOwnerToApartment(a1Owner, a1b1);
       Owner a2Owner = new Owner("Dimitar Toshev", LocalDate.of(2000, 2, 8));
       OwnerDAO.create(a2Owner);
       OwnerDAO.addOwnerToApartment(a2Owner, a2b1);
       Owner a3Owner = new Owner("Vasil Ivanov", LocalDate.of(2000, 2, 8));
       OwnerDAO.create(a3Owner);
       OwnerDAO.addOwnerToApartment(a3Owner, a3b1);
       Owner a4Owner = new Owner("Galina Kostova", LocalDate.of(2000, 2, 8));
       OwnerDAO.create(a4Owner);
       OwnerDAO.addOwnerToApartment(a4Owner, a4b1);

       BuildingDAO.assignEmployeeToBuilding(b1);

       Building b2 = new Building("Hristo Genchev 12");
       BuildingDAO.create(b2);
       BuildingDAO.addBuildingToCompany(
               b2, c1,
               BigDecimal.valueOf(0.5),
               BigDecimal.valueOf(1),
               BigDecimal.valueOf(0.5),
               BigDecimal.valueOf(0.7),
               BigDecimal.valueOf(0.3)
       );

       BuildingDAO.assignEmployeeToBuilding(b2);


       MonthTaxDAO.introduceTaxForBuilding(b1);
       MonthTaxDAO.payTax(b1);


//       TotalAmountToPayDTO totalAmountToPayDTO = MonthTaxDAO.getTotalAmountToPayForCompany(c1);
//       List<MonthTaxDTO> unpaidTaxesList = MonthTaxDAO.getTaxesToPayForCompany(c1);
//       for (MonthTaxDTO monthTaxDTO : unpaidTaxesList) {
//          System.out.println("Unpaid Tax ID: " + monthTaxDTO.getId());
//          System.out.println("Total Amount To Pay: " + monthTaxDTO.getTotalAmountToPay());
//          System.out.println("Is Paid: " + monthTaxDTO.isPaid());
//          System.out.println("------");
//       }
//       System.out.println(totalAmountToPayDTO);
//
//       System.out.println("\n\n\n\n");
//
//
//       BuildingTotalAmountDTO buildingTotalAmountDTO = MonthTaxDAO.getTotalAmountToBePaidForBuilding(b1);
//       System.out.println(buildingTotalAmountDTO);
//
//       System.out.println("\n\n\n");
//
//       List<MonthTaxDTO> unpaidTaxesDTO = MonthTaxDAO.getTaxesToPayForBuilding(b1);
//
//       // Print details for each unpaid tax DTO
//       for (MonthTaxDTO unpaidTaxDTO : unpaidTaxesDTO) {
//          System.out.println("Unpaid Tax:");
//          System.out.println("   ID: " + unpaidTaxDTO.getId());
//          System.out.println("   Total Amount To Pay: " + unpaidTaxDTO.getTotalAmountToPay());
//          System.out.println("   Is Paid: " + unpaidTaxDTO.isPaid());
//          System.out.println(); // Add a newline for better readability
//       }
//
//       TotalAmountToPayDTO total = MonthTaxDAO.getTotalAmountToBePaidForEmployee(e1);
//       System.out.println(total);
//
//
//       System.out.println("\n\n");
//
//       List<MonthTaxDTO> tax = MonthTaxDAO.getTaxesToPayForEmployee(e1);
//       for (MonthTaxDTO t : tax) {
//          System.out.println("Unpaid Tax:");
//          System.out.println("   ID: " + t.getId());
//          System.out.println("   Total Amount To Pay: " + t.getTotalAmountToPay());
//          System.out.println("   Is Paid: " + t.isPaid());
//          System.out.println(); // Add a newline for better readability
//       }
//
//       long totalresidents = ResidentDAO.getTotalResidentsInBuilding(b1);
//       System.out.println(totalresidents);
//
//       List<Resident> residentList = ResidentDAO.getResidentsInBuilding(b1);
//       for(Resident resident : residentList){
//          System.out.println("\nResident in Building Info:");
//          System.out.println("   ID: " + resident.getId());
//          System.out.println("   Name: " + resident.getName());
//       }







    }
}