// Save as ExpenseTrackerGUI.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ExpenseTrackerGUI extends JFrame {
    // Inner Expense class
    private static class Expense {
        private static int counter = 1; // auto ID
        private int id;
        private String description;
        private double amount;
        private LocalDate date;
        private String note;

        Expense(String description, double amount, LocalDate date, String note) {
            this.id = counter++;
            this.description = description;
            this.amount = amount;
            this.date = date;
            this.note = note;
        }

        public int getId() { return id; }
        public String getDescription() { return description; }
        public double getAmount() { return amount; }
        public LocalDate getDate() { return date; }
        public String getNote() { return note; }

        public void setDescription(String d) { description = d; }
        public void setAmount(double a) { amount = a; }
        public void setDate(LocalDate d) { date = d; }
        public void setNote(String n) { note = n; }
    }

    private final ArrayList<Expense> expenses = new ArrayList<>();
    private final DefaultTableModel tableModel;
    private final JTable table;

    public ExpenseTrackerGUI() {
        setTitle("Expense Tracker");
        setSize(900, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        topPanel.setBackground(new Color(60, 179, 113));

        JButton addBtn = new JButton("Add Expense");
        JButton updateBtn = new JButton("Update Expense");
        JButton deleteBtn = new JButton("Delete Expense");
        JButton totalBtn = new JButton("Show Total");

        styleButton(addBtn);
        styleButton(updateBtn);
        styleButton(deleteBtn);
        styleButton(totalBtn);

        topPanel.add(addBtn);
        topPanel.add(updateBtn);
        topPanel.add(deleteBtn);
        topPanel.add(totalBtn);
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Description", "Amount", "Date", "Note"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button actions
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { addExpense(); }
        });
        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { updateExpense(); }
        });
        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { deleteExpense(); }
        });
        totalBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showTotal(); }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(30, 144, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(170, 34));
    }

    private void addExpense() {
        JTextField descField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField noteField = new JTextField();

        Object[] message = {"Description:", descField, "Amount:", amountField, "Note (optional):", noteField};

        int option = JOptionPane.showConfirmDialog(this, message, "Add Expense", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String desc = descField.getText().trim();
            String amtText = amountField.getText().trim();
            String note = noteField.getText().trim();

            if (desc.isEmpty() || amtText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter description and amount.", "Input error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double amount = Double.parseDouble(amtText);
                if (amount < 0) throw new NumberFormatException();
                Expense exp = new Expense(desc, amount, LocalDate.now(), note);
                expenses.add(exp);
                tableModel.addRow(new Object[]{exp.getId(), exp.getDescription(), exp.getAmount(), exp.getDate(), exp.getNote()});
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Amount must be a valid non-negative number.", "Input error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteExpense() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select  row to delete.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected expense?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            expenses.remove(row);
            tableModel.removeRow(row);
        }
    }

    private void updateExpense() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to update.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Expense exp = expenses.get(row);
        JTextField descField = new JTextField(exp.getDescription());
        JTextField amountField = new JTextField(String.valueOf(exp.getAmount()));
        JTextField noteField = new JTextField(exp.getNote());

        Object[] message = {"New Description:", descField, "New Amount:", amountField, "New Note:", noteField};

        int option = JOptionPane.showConfirmDialog(this, message, "Update Expense", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String newDesc = descField.getText().trim();
            String amtText = amountField.getText().trim();
            String newNote = noteField.getText().trim();

            if (newDesc.isEmpty() || amtText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Description and amount required.", "Input error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double newAmount = Double.parseDouble(amtText);
                if (newAmount < 0) throw new NumberFormatException();

                exp.setDescription(newDesc);
                exp.setAmount(newAmount);
                exp.setDate(LocalDate.now());
                exp.setNote(newNote);

                tableModel.setValueAt(exp.getDescription(), row, 1);
                tableModel.setValueAt(exp.getAmount(), row, 2);
                tableModel.setValueAt(exp.getDate(), row, 3);
                tableModel.setValueAt(exp.getNote(), row, 4);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Amount must be valid.", "Input error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showTotal() {
        double total = 0;
        for (Expense e : expenses) total += e.getAmount();
        JOptionPane.showMessageDialog(this, String.format("Total Spent: %.2f", total), "Total Expenses", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ExpenseTrackerGUI();
            }
        });
    }
}
