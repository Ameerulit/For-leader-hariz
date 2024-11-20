
import javax.swing.*;
import java.awt.*;
import java.util.Stack;

public class CompletedTransactionWin extends JFrame {
    private JTable completedTransactionsTable;
    private Stack<CustomerInfo> completedTransactions;

    public CompletedTransactionWin() {
        setTitle("Completed Transactions");
        setSize(600, 400);
        setLocationRelativeTo(null); // Center the frame
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        completedTransactions = MainDashboard.getCompletedTransactions(); // Assuming you have a method to get the completed stack
        
        String[] columns = {"Customer Name", "Vehicle Plate No", "Service Details", "Total Cost"};
        
        // Set up the data model for the table
        Object[][] data = getCompletedTransactionData();
        
        completedTransactionsTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(completedTransactionsTable);
        
        // Add the table to the frame
        add(scrollPane, BorderLayout.CENTER);
    }

    // Method to extract data from completed transactions for the table
    private Object[][] getCompletedTransactionData() {
        Object[][] data = new Object[completedTransactions.size()][4];
        int index = 0;
        for (CustomerInfo customer : completedTransactions) {
            String services = getServiceDetails(customer);
            double totalCost = calculateTotalCost(customer);
            data[index++] = new Object[]{
                customer.getCustomerName(),
                customer.getVehiclePlateNumber(),
                services,
                totalCost
            };
        }
        return data;
    }

    // Helper method to get service details for each customer
    private String getServiceDetails(CustomerInfo customer) {
        StringBuilder serviceDetails = new StringBuilder();
        for (ServiceInfo service : customer.getServices()) {
            serviceDetails.append(service.getServiceType()).append(" (").append(service.getServiceCost()).append("), ");
        }
        return serviceDetails.toString();
    }

    // Helper method to calculate total cost for a customer
    private double calculateTotalCost(CustomerInfo customer) {
        double totalCost = 0;
        for (ServiceInfo service : customer.getServices()) {
            totalCost += service.getServiceCost();
        }
        return totalCost;
    }
}
