/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DA;

import DT.SupplierDT;
import Database.DatabaseHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Locale;
import java.util.Vector;

public class Supplier {

    Connection conn = null;
    Statement statement = null;
    PreparedStatement prepStatement = null;
    ResultSet resultSet = null;

    public Supplier() {
        try {
            conn = new DatabaseHandler().getConn();
            statement = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addSupplierDAO(SupplierDT supplierDTO) {
        try {
            String query = "SELECT * FROM suppliers WHERE fullname='"
                    + supplierDTO.getFullName()
                    + "' AND location='"
                    + supplierDTO.getLocation()
                    + "' AND mobile='"
                    + supplierDTO.getPhone()
                    + "'";
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                JOptionPane.showMessageDialog(null, "This supplier already exists.");
            } else {
                addFunction(supplierDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addFunction(SupplierDT supplierDTO) {
        try {
            String query = "INSERT INTO suppliers VALUES(null,?,?,?)";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(2, supplierDTO.getFullName());
            prepStatement.setString(3, supplierDTO.getLocation());
            prepStatement.setString(4, supplierDTO.getPhone());
            prepStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "New supplier has been added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to edit existing suppleir details
    public void editSupplierDAO(SupplierDT supplierDTO) {
        try {
            String query = "UPDATE suppliers SET fullname=?,location=?,contact=? WHERE supplierID=?";
            
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, supplierDTO.getFullName());
            prepStatement.setString(2, supplierDTO.getLocation());
            prepStatement.setString(3, supplierDTO.getPhone());
            prepStatement.setString(4, String.valueOf(supplierDTO.getSuppID()));
            prepStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Supplier details have been updated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete existing supplier
    public void deleteSupplierDAO(int suppID) {
        try {
            String query = "DELETE FROM suppliers WHERE supplierID='" + suppID + "'";
            statement.executeUpdate(query);
            JOptionPane.showMessageDialog(null, "Supplier has been removed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Supplier data set retrieval method
    public ResultSet getQueryResult() {
        try {
            String query = "SELECT supplierID, fullname, location, contact FROM suppliers";
            resultSet = statement.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    // Search method
    public ResultSet getSearchResult(String searchText) {
        try {
            String query = "SELECT suppliercode, fullname, location, mobile FROM suppliers "
                    + "WHERE suppliercode LIKE '%" + searchText + "%' OR location LIKE '%" + searchText + "%' "
                    + "OR fullname LIKE '%" + searchText + "%' OR mobile LIKE '%" + searchText + "%'";
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    // Method to set/update supplier combo box
    public DefaultComboBoxModel<String> setComboItems(ResultSet resultSet) throws SQLException {
        Vector<String> suppNames = new Vector<>();
        while (resultSet.next()) {
            suppNames.add(resultSet.getString("fullname"));
        }
        return new DefaultComboBoxModel<>(suppNames);
    }

    // Method to display retrieved data set in tabular form
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
