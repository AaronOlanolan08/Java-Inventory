/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DA;

import DT.CustomerDT;
import Database.DatabaseHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Locale;
import java.util.Vector;

/**
 *
 * @author New User
 */
public class Customer {

    Connection conn = null;
    PreparedStatement prep = null;
    Statement state = null;
    ResultSet result = null;

    public Customer() {

        try {
            conn = new DatabaseHandler().getConn();
            state = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void addCustomer(CustomerDT customer) {
        try {
            String query = "SELECT * FROM customers WHERE fullname='"
                    + customer.getFullName() + "' AND location='"
                    + customer.getLocation() + "' AND contact='"
                    + customer.getContact() + "'";
            result = state.executeQuery(query);
            if (result.next()) {
                JOptionPane.showMessageDialog(null, "Customer alrady exists");
            } else {
                addNewCustomer(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNewCustomer(CustomerDT customer) {
        try {
            String query = "INSERT INTO customers VALUES(null,?,?,?)";
            prep = conn.prepareStatement(query);
            prep.setString(1, customer.getFullName());
            prep.setString(2, customer.getLocation());
            prep.setString(3, customer.getContact());
            prep.executeUpdate();
            JOptionPane.showMessageDialog(null, "New Customer has been added");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editCustomer(CustomerDT customer) {
        try {
            String query = "UPDATE customers SET fullname=?,location=?,contact=? WHERE Customer_ID=?";
            prep = conn.prepareStatement(query);
            prep.setString(1, customer.getFullName());
            prep.setString(2, customer.getLocation());
            prep.setString(3, customer.getContact());
            prep.setString(4, Integer.toString(customer.getCustomerID()));
            prep.executeUpdate();
            JOptionPane.showMessageDialog(null, "New Customer has been added");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCustomer(int customerID) {
        try {
            String custID = Integer.toString(customerID);
            String query = "DELETE FROM customers WHERE Customer_ID='" + custID + "'";
            state.executeUpdate(query);
            JOptionPane.showMessageDialog(null, "Customer removed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getQuery() {
        try {
            String query = "SELECT customerID,fullname,location,contact FROM customers";
            result = state.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ResultSet getCustomerSearch(String text) {
        try {
            String query = "SELECT Customer_ID,fullname,location,contact FROM customers"
                    + "WHERE Customer_ID LIKE '%" + text + "%' OR fullname LIKE '%" + text + "%' OR "
                    + "location LIKE '%" + text + "%' OR phone LIKE '%" + text + "%'";
            result = state.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ResultSet getName(String id) {
        try {
            String query = "SELECT * FROM customers WHERE Customer_ID='" + id + "'";
            result = state.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

//    public ResultSet getProdName(String prodCode) {
//        try {
//            String query = "SELECT productname,currentstock.quantity FROM products "
//                    + "INNER JOIN currentstock ON products.productcode=currentstock.productcode "
//                    + "WHERE currentstock.productcode='" + prodCode + "'";
//            resultSet = statement.executeQuery(query);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return resultSet;
//    }
//
//    // Method to display data set in tabular form
    public DefaultTableModel buildTableModel(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        Vector<String> columnNames = new Vector<String>();
        int colCount = metaData.getColumnCount();

        for (int col = 1; col <= colCount; col++) {
            columnNames.add(metaData.getColumnName(col).toUpperCase(Locale.ROOT));
        }

        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (resultSet.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int col = 1; col <= colCount; col++) {
                vector.add(resultSet.getObject(col));
            }
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }
}
