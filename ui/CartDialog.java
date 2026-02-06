package ui;

import models.CartItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CartDialog extends JDialog {

    private JLabel totalLabel;
    private DefaultTableModel tableModel;
    private JTable table;
    private List<CartItem> cartItems;

    public CartDialog(JFrame parent, List<CartItem> cartItems) {
        super(parent, "Shopping Cart", true);
        this.cartItems = cartItems;

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

         // TABLE 
        tableModel = new DefaultTableModel(
                new String[]{"Product", "Price", "Qty", "Total"}, 0
        );
        table = new JTable(tableModel);

        refreshTable();

        add(new JScrollPane(table), BorderLayout.CENTER);

         // BOTTOM PANEL 
        JPanel bottomPanel = new JPanel(new BorderLayout());

        totalLabel = new JLabel();
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        updateTotal();

        JButton removeBtn = new JButton("Remove Selected");
        JButton closeBtn = new JButton("Close");

        JPanel btnPanel = new JPanel();
        btnPanel.add(removeBtn);
        btnPanel.add(closeBtn);

        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(btnPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

         // EVENTS 

        removeBtn.addActionListener(e -> removeSelectedItem());
        closeBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

     // HELPERS

    private void refreshTable() {
        tableModel.setRowCount(0);

        for (CartItem item : cartItems) {
            tableModel.addRow(new Object[]{
                    item.getProduct().getName(),
                    item.getProduct().getPrice(),
                    item.getQuantity(),
                    item.getProduct().getPrice() * item.getQuantity()
            });
        }
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        totalLabel.setText("Total: $" + total);
    }

    private void removeSelectedItem() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an item to remove");
            return;
        }

        cartItems.remove(row);
        refreshTable();
        updateTotal();
    }
}
