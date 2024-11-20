import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Queue;
import java.util.Stack;

public class QueueWindow extends JFrame {
    private JTable table;
    private JScrollPane tableScrollPane;
    private JPanel actionPanel;
    private Queue<CustomerInfo> queue;
    private Stack<CustomerInfo> completeStack;
    private Processor processor;

    public QueueWindow(String title, Queue<CustomerInfo> queue, Processor processor) {
        this.queue = queue;
        this.processor = processor;
        this.completeStack = new Stack<>();

        setTitle(title);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeComponents();
        updateTable();
    }

    private void initializeComponents() {
        // Initialize table
        String[] columnNames = {"Customer ID", "Customer Name", "Vehicle Plate No.", "No of Services", "Total Service Cost"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        tableScrollPane = new JScrollPane(table);
        add(tableScrollPane, BorderLayout.CENTER);

        // Initialize action panel
        actionPanel = createActionPanel();
        add(actionPanel, BorderLayout.SOUTH);
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        JButton btnCreateReceipt = new JButton("Receipt");
        JButton btnCancelOrder = new JButton("Cancel");

        btnCreateReceipt.addActionListener(e -> processReceipt());
        btnCancelOrder.addActionListener(e -> cancelOrder());

        panel.add(btnCreateReceipt);
        panel.add(btnCancelOrder);

        return panel;
    }

    private void updateTable() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (CustomerInfo customer : queue) {
            model.addRow(new Object[]{
                customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getVehiclePlateNumber(),
                customer.getCountOfServices(),
                customer.calculateTotalCost()
            });
        }
    }

    private void processReceipt() {
        if (queue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No customers in this queue.");
            return;
        }

        CustomerInfo customer = queue.poll();
        completeStack.push(customer);
        try {
            processor.markCustomerAsComplete(customer);
            String receiptData = generateReceiptData(customer);
            JOptionPane.showMessageDialog(this, receiptData, "Receipt", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error processing the receipt: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        updateTable();
    }

    private String generateReceiptData(CustomerInfo customer) {
        StringBuilder receiptBuilder = new StringBuilder();
        receiptBuilder.append("Receipt for ").append(customer.getCustomerName()).append("\n");
        receiptBuilder.append("Customer ID: ").append(customer.getCustomerId()).append("\n");
        receiptBuilder.append("Vehicle Plate No.: ").append(customer.getVehiclePlateNumber()).append("\n");
        receiptBuilder.append("Services: ").append(customer.getCountOfServices()).append("\n");
        receiptBuilder.append("Total Cost: $").append(String.format("%.2f", customer.calculateTotalCost())).append("\n");
        return receiptBuilder.toString();
    }

    private void cancelOrder() {
        if (queue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No customers in this queue.");
            return;
        }

        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this order?", "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            CustomerInfo customer = queue.poll();
            JOptionPane.showMessageDialog(this, "Order canceled for " + customer.getCustomerName());
            updateTable();
        }
    }
}