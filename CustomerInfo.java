import java.util.LinkedList;

 public class CustomerInfo {
     private String customerId;
     private String customerName;
     private String vehiclePlateNumber;
     private LinkedList<ServiceInfo> services; // customer services
 
     public CustomerInfo(String customerId, String customerName, String vehiclePlateNumber) {
         this.customerId = customerId;
         this.customerName = customerName;
         this.vehiclePlateNumber = vehiclePlateNumber;
         this.services = new LinkedList<>();
     }
 
     // getter for customerId
     public String getCustomerId() {
         return customerId;
     }
 
     // getter for customerName
     public String getCustomerName() {
         return customerName;
     }
 
     // getter for vehiclePlateNumber
     public String getVehiclePlateNumber() {
         return vehiclePlateNumber;
     }
 
     // getter for services
     public LinkedList<ServiceInfo> getServices() {
         return services;
     }
 
     // add a service to the customer
     public void addService(ServiceInfo service) {
         services.add(service);
     }
 
     // calculate total cost of services
     public double calculateTotalCost() {
         double totalCost = 0.0;
         for (ServiceInfo service : services) {
             totalCost += service.getServiceCost();
         }
         return totalCost;
     }
 
     // get count of services
     public int getCountOfServices() {
         return services.size();
     }
 }