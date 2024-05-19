package UI;

import DA.Prod;
import DA.Users;
import DT.ProdDT;
import java.sql.*;
import javax.swing.JOptionPane;

public class OrdersFrame extends javax.swing.JPanel {

    String username;

    public OrdersFrame(String username) {
        this.username = username;
        initComponents();
        loadDataSet();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        prodTable = new javax.swing.JTable();
        searchField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        nameField = new javax.swing.JTextField();
        jSpinner1 = new javax.swing.JSpinner();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        buyButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        sellPriceField = new javax.swing.JTextField();
        clearButton = new javax.swing.JButton();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        prodTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(prodTable);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 620, 470));

        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchFieldKeyReleased(evt);
            }
        });
        add(searchField, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 10, 120, -1));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Search:");
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(448, 6, 50, 30));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Enter Order Details"));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        nameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                nameFieldKeyReleased(evt);
            }
        });
        jPanel1.add(nameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 116, 175, -1));
        jPanel1.add(jSpinner1, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 252, 175, -1));
        jPanel1.add(jDateChooser1, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 184, 175, -1));

        buyButton.setText("Buy Product");
        buyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buyButtonActionPerformed(evt);
            }
        });
        jPanel1.add(buyButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 380, -1, -1));

        jLabel1.setText("Product Name:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 82, 94, -1));

        jLabel2.setText("Quantity:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 224, -1, -1));

        jLabel3.setText("Date:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 156, 37, -1));

        jLabel4.setText("Selling Price:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 292, 76, -1));
        jPanel1.add(sellPriceField, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 326, 175, -1));

        clearButton.setText("Clear");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        jPanel1.add(clearButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 380, 63, -1));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 40, 280, 470));
    }// </editor-fold>//GEN-END:initComponents

    private void buyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buyButtonActionPerformed
        ProdDT prod = new ProdDT();
        Prod prd = new Prod();
        Users user = new Users();
        if (nameField.getText().equals("") || jDateChooser1.getDate() == null || sellPriceField.getText().equals(""))
            JOptionPane.showMessageDialog(null, "Please enter all the required details.");
        else {
            try {
                ResultSet resultSet = prd.getProdName(nameField.getText());
                if (resultSet.next()) {
                    prod.setProdName(nameField.getText());
                    int uid = user.getUserID(username);
                    String pid = prd.getProdID(nameField.getText());
                    prod.setProdID(Integer.parseInt(pid));
                    prod.setDate(jDateChooser1.getDate().toString());
                    Double sellPrice = Double.valueOf(sellPriceField.getText());
                    int quantity = (int) jSpinner1.getValue();
                    double totalRevenue = sellPrice * quantity;
                    prod.setTotalRevenue(totalRevenue);
                    prod.setQuantity(quantity);
                    prd.buyProductDAO(prod, uid);
                    loadDataSet();
                } else {
                    JOptionPane.showMessageDialog(this, "This product does not exist.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_buyButtonActionPerformed

    private void nameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nameFieldKeyReleased
        try {
            ResultSet resultSet = new Prod().getProdName(nameField.getText());
            loadSearchData(nameField.getText());
            if (resultSet.next()) {
                Double sellPrice = new Prod().getProdSell(nameField.getText());
                sellPriceField.setText(sellPrice.toString());
            } else {
                sellPriceField.setText("");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_nameFieldKeyReleased

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        nameField.setText("");
        jDateChooser1.setDate(null);
        jSpinner1.setValue(0);
        sellPriceField.setText("");
    }//GEN-LAST:event_clearButtonActionPerformed

    private void searchFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyReleased
        loadSearchData(searchField.getText());
    }//GEN-LAST:event_searchFieldKeyReleased

    public void loadDataSet() {
        try {
            Prod productDAO = new Prod();
            prodTable.setModel(productDAO.buildTableModel(productDAO.getQueryResult()));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void loadSearchData(String text) {
        try {
            Prod productDAO = new Prod();
            prodTable.setModel(productDAO.buildTableModel(productDAO.getProductSearch(text)));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buyButton;
    private javax.swing.JButton clearButton;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTextField nameField;
    private javax.swing.JTable prodTable;
    private javax.swing.JTextField searchField;
    private javax.swing.JTextField sellPriceField;
    // End of variables declaration//GEN-END:variables
}
