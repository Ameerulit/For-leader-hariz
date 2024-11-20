import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionListener;
import java.util.Stack;

import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;
import javax.swing.table.DefaultTableModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MainDashboard extends JFrame{
    // variables for all components
    private JPanel mainPanel, bottomPanel;
    private JLabel lblCustomersLeft, lblServicesPerformed, lblTotalSales, lblDateTime;
    private JTable table1, table2, table3 ;
    private JScrollPane tableScrollPane1, tableScrollPane2, tableScrollPane3;
    private JLabel lblTab1CustomerInfo, lblTab2CustomerInfo, lblTab3CustomerInfo;
    private JButton btnTab1CreateReceipt, btnTab1CancelOrder;
    private JButton btnTab2CreateReceipt, btnTab2CancelOrder;
    private JButton btnTab3CreateReceipt, btnTab3CancelOrder;
    private ImageIcon icon;
    private JTable customerTable;
    private JTable serviceTable;

    private Processor processor; // processor instance
    private Queue<CustomerInfo> queue1, queue2, queue3; //three queues
    private LinkedList<CustomerInfo> customerList; 

    private JButton btnUploadCustomer;
    private JButton btnOpenQueue1;
    private JButton btnOpenQueue2;
    private JButton btnOpenQueue3;
    public MainDashboard() {
        // frame setup
        setTitle("SWC-Project 3344");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());
        setVisible(true);
        icon = new ImageIcon("gta bengkel.jpg");
        setIconImage(icon.getImage());
        this.processor = new Processor();

        // Initialize mainPanel
        mainPanel = new JPanel(new BorderLayout());

        btnOpenQueue1 = new JButton("Open Queue 1");
        btnOpenQueue2 = new JButton("Open Queue 2");
        btnOpenQueue3 = new JButton("Open Queue 3");

        btnOpenQueue1.addActionListener(e -> openQueueWindow("Queue 1", queue1));
        btnOpenQueue2.addActionListener(e -> openQueueWindow("Queue 2", queue2));
        btnOpenQueue3.addActionListener(e -> openQueueWindow("Queue 3", queue3));

        // Add these buttons to your layout, for example in the main panel:
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnOpenQueue1);
        buttonPanel.add(btnOpenQueue2);
        buttonPanel.add(btnOpenQueue3);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        // Add mainPanel to the frame
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
        // bottom panel (date and time)
        bottomPanel = new JPanel();
        lblDateTime = new JLabel();

        JButton btnViewCompletedTransaction = new JButton("Display Completed Transactions");
        btnViewCompletedTransaction.addActionListener(e -> openCompletedTransWin());

        btnUploadCustomer = new JButton("Upload Customer");

        bottomPanel.add(btnViewCompletedTransaction);
        bottomPanel.add(btnUploadCustomer);
        bottomPanel.add(lblDateTime);

        //add components to frame
        add(bottomPanel, BorderLayout.SOUTH);

        /////test after, put in the constructor
        processor = new Processor(); // initialize processor
        queue1 = new LinkedList<>(); // initialize queues
        queue2 = new LinkedList<>();
        queue3 = new LinkedList<>();
        customerList = new LinkedList<>(); // linked list for all customers

        table1 = new JTable();
        table2 = new JTable();
        table3 = new JTable();
        // Create scroll panes for tables
        tableScrollPane1 = new JScrollPane(table1);
        tableScrollPane2 = new JScrollPane(table2);
        tableScrollPane3 = new JScrollPane(table3);

        // Add scroll panes to the layout
        // For example:
        JPanel tablesPanel = new JPanel(new GridLayout(3, 1));
        tablesPanel.add(tableScrollPane1);
        tablesPanel.add(tableScrollPane2);
        tablesPanel.add(tableScrollPane3);
        mainPanel.add(tablesPanel, BorderLayout.CENTER);    
        //actionlistener
        btnUploadCustomer.addActionListener(e -> {
                    try {
                        processor.loadCustomerDataFromFile("CustomerList.txt");
                        customerList = processor.getCustomerList();
                        System.out.println("Loaded " + customerList.size() + " customers from file.");

                        updateQueues();
                        updateTables();

                        JOptionPane.showMessageDialog(this, "Customer data loaded successfully!");
                        System.out.println("Queue sizes after upload: Q1=" + queue1.size() + 
                            ", Q2=" + queue2.size() + ", Q3=" + queue3.size());
                    } catch (Exception ex) {
                        String errorMessage = "Error loading customer data: " + ex.getMessage() + 
                            "\n\nStack trace:\n" + getStackTraceAsString(ex);
                        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
            });

    }
    // Helper method to get stack trace as string
    private String getStackTraceAsString(Exception ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    private void openQueueWindow(String title, Queue<CustomerInfo> queue) {
        QueueWindow queueWindow = new QueueWindow(title, queue, this.processor);
        queueWindow.setVisible(true);
    }

    //Queue process
    private void updateQueues() {
        queue1.clear();
        queue2.clear();
        queue3.clear();

        QueueManager queueManager = new QueueManager();

        System.out.println("Updating queues. Total customers: " + customerList.size());

        for (CustomerInfo customer : customerList) {
            boolean enqueued = queueManager.enqueue(customer);
            System.out.println("Enqueuing customer " + customer.getCustomerId() + 
                " with " + customer.getServices().size() + " services. Enqueued: " + enqueued);
        }

        queue1 = queueManager.getQueue1();
        queue2 = queueManager.getQueue2();
        queue3 = queueManager.getQueue3();

        System.out.println("Queue sizes after update: Q1=" + queue1.size() + 
            ", Q2=" + queue2.size() + ", Q3=" + queue3.size());
    }

    private static Stack<CustomerInfo> completeStack = new Stack<>();

    //update jtable part1/2
    private void updateTables() {
        updateTable(table1, queue1);
        updateTable(table2, queue2);
        updateTable(table3, queue3);
    }

    // update jtable part2/2
    //method to update the JTable with customer data
    private void updateTable(JTable table, Queue<CustomerInfo> queue) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear the existing data

        // Only display the first 5 customers from the selected queue
        int count = 0;
        for (CustomerInfo customerInfo : queue) {
            if (count >= 5) {
                break; // Stop after 5 customers
            }
            model.addRow(new Object[]{
                    customerInfo.getCustomerId(),
                    customerInfo.getCustomerName(),
                    customerInfo.getVehiclePlateNumber(),
                    customerInfo.getCountOfServices(),
                    customerInfo.calculateTotalCost()
                });
            count++;
        }
    }

    // helper method to create a stat panel
    private JLabel createStatPanel(String text, Color bgColor, Color borderColor) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(bgColor); // set background color
        label.setForeground(Color.BLACK); // text color
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, borderColor)); // matte border with customizable weights and color
        return label;
    }

    private JLabel createStatPanel(String text, Color bgColor) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(bgColor); // set background color
        label.setForeground(Color.BLACK); // text color
        label.setFont(new Font("Arial", Font.BOLD, 16));
        return label;
    }

    // helper method to create a bottom panel for each tabs
    // Method to create action buttons for each tab
    private JPanel createTabActionPanel(JTable table, Queue<CustomerInfo> queue) {
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BorderLayout());

        // action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnCreateReceipt = new JButton("Receipt");
        JButton btnCancelOrder = new JButton("Cancel");
        buttonPanel.add(btnCreateReceipt);
        buttonPanel.add(btnCancelOrder);

        actionPanel.add(buttonPanel, BorderLayout.CENTER);

        // Action listeners for buttons
        btnCreateReceipt.addActionListener(e -> processReceipt(table1, queue1)); // dynamic, Update table after processing
        btnCancelOrder.addActionListener(e -> cancelOrder(table1, queue1)); // dynamic, Update table after cancellation

        return actionPanel;
    }

    // processReceipt implementation
    private void processReceipt(JTable table, Queue<CustomerInfo> queue) {
        System.out.println("Create Receipt button clicked");
        if (queue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No customers in this queue.");
            return;
        }

        CustomerInfo customer = queue.poll(); // remove the first customer from the queue
        completeStack.push(customer);
        try{
            processor.markCustomerAsComplete(customer); // mark as complete (this should be handled in your processor)
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error processing the receipt: " + ex.getMessage());
        }
        updateTables(); // update tables after processing the customer

        JOptionPane.showMessageDialog(this, "Receipt created for " + customer.getCustomerName());
    }

    // cancelOrder implementation
    private void cancelOrder(JTable table, Queue<CustomerInfo> queue) {
        System.out.println("Create Receipt button clicked");
        if (queue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No customers in this queue.");
            return;
        }
        // Prompt the user for confirmation before canceling
        int response = JOptionPane.showConfirmDialog(MainDashboard.this,
                "Are you sure you want to cancel this order?", "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {

            // Perform the cancellation logic (e.g., remove customer, etc.)
            updateTables();  // Update the tables if needed

            CustomerInfo customer = queue.poll(); // remove the first customer from the queue
            // Optionally, you could push this customer to a "canceled" list or handle as needed

            updateTables(); // update tables after canceling the order

            JOptionPane.showMessageDialog(this, "Order canceled for " + customer.getCustomerName());
        }
    }
    //end of implement Action buttons for actionpanel

    private void openCompletedTransWin() {
        CompletedTransactionWin completedTransWin = new CompletedTransactionWin();
        completedTransWin.setVisible(true);
    }

    public static Stack<CustomerInfo> getCompletedTransactions() {
        return completeStack; // Assuming completeStack is the Stack that stores completed transactions

    }

    ///////wiwu
    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // invokeLater is for safe practice of ui building
        SwingUtilities.invokeLater(() -> {
                    new MainDashboard();
            });

        /////////all this three can give a try and error. can remove comment to give a try
        ////tokenizer
        //Processor processor = new Processor();

        //// load customer data from file
        //processor.loadCustomerDataFromFile("customerlist.txt");

        //// simulate marking a customer as complete
        //CustomerInfo completedCustomer = processor.getCustomerList().get(0);
        //processor.markCustomerAsComplete(completedCustomer);

        ////display completed transactions in backend for test
        //System.out.println("Completed Transactions: " + processor.getCompletedTransactionCount());
        //System.out.println("Total Sales: $" + processor.calculateTotalSales());
        /////////////////
    }
}