/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DA;

import DT.ProdDT;
import DT.SupplierDT;
import Database.DatabaseHandler;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Locale;
import java.util.Vector;

public class Prod {

    Connection conn = null;
    PreparedStatement prep1 = null;
    PreparedStatement prep2 = null;
    Statement statement = null;
    Statement statement2 = null;
    ResultSet resultSet = null;
    SupplierDT supp = new SupplierDT();

    public Prod() {
        try {
            conn = new DatabaseHandler().getConn();
            statement = conn.createStatement();
            statement2 = conn.createStatement();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ResultSet getSuppliers() {
        try {
            String query = "SELECT * FROM suppliers";
            resultSet = statement.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet getCustomers() {
        try {
            String query = "SELECT * FROM customers";
            resultSet = statement.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet getProdStock() {
        try {
            String query = "SELECT * FROM currentstock";
            resultSet = statement.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet getProdInfo() {
        try {
            String query = "SELECT * FROM products";
            resultSet = statement.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public Double getProdCost(String prodCode) {
        Double costPrice = null;
        try {
            String query = "SELECT costprice FROM products WHERE productcode='" + prodCode + "'";
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                costPrice = resultSet.getDouble("costprice");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return costPrice;
    }

    public Double getProdSell(String prodname) {
        Double sellPrice = null;
        try {
            String query = "SELECT sellprice FROM products WHERE productname='" + prodname + "'";
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                sellPrice = resultSet.getDouble("sellprice");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sellPrice;
    }

    String suppID;

    public String getSuppID(String suppName) {
        try {
            String query = "SELECT supplierID FROM suppliers WHERE fullname='" + suppName + "'";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                suppID = resultSet.getString("supplierID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppID;
    }

    String prodCode;

    String prodID;

    public String getProdID(String prodname) {
        try {
            String query = "SELECT productID FROM products WHERE productname='" + prodname + "'";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                prodID = resultSet.getString("productID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prodID;
    }

    // Method to check for availability of stock in Inventory
    boolean flag = false;

    public boolean checkStock(String prodCode) {
        try {
            String query = "SELECT * FROM currentstock WHERE productcode='" + prodCode + "'";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    // Methods to add a new product
    public void addProductDAO(ProdDT productDTO) {
        try {
            String query = "SELECT * FROM products WHERE productname='"
                    + productDTO.getProdName()
                    + "' AND costprice='"
                    + productDTO.getCostPrice()
                    + "' AND sellprice='"
                    + productDTO.getSellPrice()
                    + "' AND brand='"
                    + productDTO.getBrand()
                    + "' AND stock='"
                    + productDTO.getQuantity();
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                JOptionPane.showMessageDialog(null, "Product has already been added.");
            } else {
                addFunction(productDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addFunction(ProdDT productDTO) {
        try {
            String query = "INSERT INTO products VALUES(null,?,?,?,?,?)";
            prep1 = (PreparedStatement) conn.prepareStatement(query);
            prep1.setString(1, productDTO.getProdName());
            prep1.setDouble(2, productDTO.getCostPrice());
            prep1.setDouble(3, productDTO.getSellPrice());
            prep1.setString(4, productDTO.getBrand());
            prep1.setString(5, String.valueOf(productDTO.getQuantity()));

            prep1.executeUpdate();
            JOptionPane.showMessageDialog(null, "Product added and ready for sale.");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addRestock(ProdDT prod) {
        try {
            String query = "UPDATE products SET stock=? WHERE productname=?";
            prep1 = (PreparedStatement) conn.prepareStatement(query);
            prep1.setString(1, String.valueOf(prod.getQuantity()));
            prep1.setString(2, prod.getProdName());

            prep1.executeUpdate();
            JOptionPane.showMessageDialog(null, "Product added and ready for sale.");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // Method to add a new purchase transaction
    public void addPurchaseDAO(ProdDT productDTO) {
        try {
            String query = "INSERT INTO purchaseinfo VALUES(null,?,?,?,?,?)";
            prep1 = conn.prepareStatement(query);
            prep1.setString(1, Integer.toString(productDTO.getProdID()));
            prep1.setString(2, Integer.toString(productDTO.getSuppID()));
            prep1.setString(3, productDTO.getDate());
            prep1.setInt(4, productDTO.getQuantity());
            prep1.setDouble(5, productDTO.getTotalCost());

            prep1.executeUpdate();
            JOptionPane.showMessageDialog(null, "Purchase log added.");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void addRestockPurchase(ProdDT productDTO) {
        try {
            String query = "INSERT INTO purchaseinfo VALUES(null,?,?,?,?,?)";
            prep1 = conn.prepareStatement(query);
            prep1.setString(1, Integer.toString(productDTO.getProdID()));
            prep1.setString(2, Integer.toString(productDTO.getSuppID()));
            prep1.setString(3, productDTO.getDate());
            prep1.setInt(4, productDTO.getRestock());
            prep1.setDouble(5, productDTO.getTotalCost());

            prep1.executeUpdate();
            JOptionPane.showMessageDialog(null, "Purchase log added.");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    // Method to update existing product details
    public void editProdDAO(ProdDT productDTO) {
        try {
            String query = "UPDATE products SET productname=?,costprice=?,sellprice=?,brand=? WHERE productcode=?";
            prep1 = (PreparedStatement) conn.prepareStatement(query);
            prep1.setString(1, productDTO.getProdName());
            prep1.setDouble(2, productDTO.getCostPrice());
            prep1.setDouble(3, productDTO.getSellPrice());
            prep1.setString(4, productDTO.getBrand());
            prep1.setString(5, productDTO.getProdCode());

            String query2 = "UPDATE currentstock SET quantity=? WHERE productcode=?";
            prep2 = conn.prepareStatement(query2);
            prep2.setInt(1, productDTO.getQuantity());
            prep2.setString(2, productDTO.getProdCode());

            prep1.executeUpdate();
            prep2.executeUpdate();
            JOptionPane.showMessageDialog(null, "Product details updated.");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // Methods to handle updating of stocks in Inventory upon any transaction made
    public void editPurchaseStock(String code, int quantity) {
        try {
            String query = "SELECT * FROM currentstock WHERE productcode='" + code + "'";
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                String query2 = "UPDATE currentstock SET quantity=quantity-? WHERE productcode=?";
                prep1 = conn.prepareStatement(query2);
                prep1.setInt(1, quantity);
                prep1.setString(2, code);
                prep1.executeUpdate();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void editSoldStock(String code, int quantity) {
        try {
            String query = "SELECT * FROM currentstock WHERE productcode='" + code + "'";
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                String query2 = "UPDATE currentstock SET quantity=quantity+? WHERE productcode=?";
                prep1 = conn.prepareStatement(query2);
                prep1.setInt(1, quantity);
                prep1.setString(2, code);
                prep1.executeUpdate();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deleteStock() {
        try {
            String query = "DELETE FROM currentstock WHERE productcode NOT IN(SELECT productcode FROM purchaseinfo)";
            String query2 = "DELETE FROM salesinfo WHERE productcode NOT IN(SELECT productcode FROM products)";
            statement.executeUpdate(query);
            statement.executeUpdate(query2);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // Method to permanently delete a product from inventory
    public void deleteProductDAO(String code) {
        try {
            String query = "DELETE FROM products WHERE productcode=?";
            prep1 = conn.prepareStatement(query);
            prep1.setString(1, code);

            String query2 = "DELETE FROM currentstock WHERE productcode=?";
            prep2 = conn.prepareStatement(query2);
            prep2.setString(1, code);

            prep1.executeUpdate();
            prep2.executeUpdate();

            JOptionPane.showMessageDialog(null, "Product has been removed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        deleteStock();
    }

    public void deletePurchaseDAO(int ID) {
        try {
            String query = "DELETE FROM purchaseinfo WHERE purchaseID=?";
            prep1 = conn.prepareStatement(query);
            prep1.setInt(1, ID);
            prep1.executeUpdate();

            JOptionPane.showMessageDialog(null, "Transaction has been removed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        deleteStock();
    }

    public void deleteSaleDAO(int ID) {
        try {
            String query = "DELETE FROM salesinfo WHERE salesID=?";
            prep1 = conn.prepareStatement(query);
            prep1.setInt(1, ID);
            prep1.executeUpdate();

            JOptionPane.showMessageDialog(null, "Transaction has been removed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        deleteStock();
    }

    // Sales transaction handling
    public void buyProductDAO(ProdDT productDTO, int uid) {
        try {
            int quantity = getStock(productDTO.getProdName());

            if (productDTO.getQuantity() > quantity) {
                JOptionPane.showMessageDialog(null, "Insufficient stock for this product.");
            } else if (productDTO.getQuantity() <= 0) {
                JOptionPane.showMessageDialog(null, "Please enter a valid quantity");
            } else {
                String stockQuery = "UPDATE products SET stock=stock-? WHERE productID=?";
                String salesQuery = "INSERT INTO salesinfo VALUES (null, ?, ?, ?, ?, ?)";

                PreparedStatement stockPrepStmt = conn.prepareStatement(stockQuery);
                stockPrepStmt.setInt(1, productDTO.getQuantity());
                stockPrepStmt.setInt(2, productDTO.getProdID());
                stockPrepStmt.executeUpdate();

                PreparedStatement salesPrepStmt = conn.prepareStatement(salesQuery);
                salesPrepStmt.setInt(1, productDTO.getProdID());
                salesPrepStmt.setInt(2, uid);
                salesPrepStmt.setString(3, productDTO.getDate());
                salesPrepStmt.setInt(4, productDTO.getQuantity());
                salesPrepStmt.setDouble(5, productDTO.getTotalRevenue());
                salesPrepStmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Product sold.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Products data set retrieval for display
    public ResultSet getQueryResult() {
        try {
            String query = "SELECT productID,productname,costprice,sellprice,brand,stock FROM products";
            resultSet = statement.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return resultSet;
    }

    // Purchase table data set retrieval
    public ResultSet getPurchaseInfo() {
        try {
            String query = "SELECT PurchaseID,purchaseinfo.ProductCode,ProductName,Quantity,Totalcost "
                    + "FROM purchaseinfo INNER JOIN products "
                    + "ON products.productcode=purchaseinfo.productcode ORDER BY purchaseid;";
            resultSet = statement.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return resultSet;
    }

    // Stock table data set retrieval
    public ResultSet getCurrentStockInfo() {
        try {
            String query = """
                    SELECT currentstock.ProductCode,products.ProductName,
                    currentstock.Quantity,products.CostPrice,products.SellPrice
                    FROM currentstock INNER JOIN products
                    ON currentstock.productcode=products.productcode;
                    """;
            resultSet = statement.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return resultSet;
    }

    // Sales table data set retrieval
    public ResultSet getSalesInfo() {
        try {
            String query = """
                    SELECT salesid,salesinfo.productcode,productname,
                    salesinfo.quantity,revenue,users.name AS Sold_by
                    FROM salesinfo INNER JOIN products
                    ON salesinfo.productcode=products.productcode
                    INNER JOIN users
                    ON salesinfo.soldby=users.username;
                    """;
            resultSet = statement.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return resultSet;
    }

    // Search method for products
    public ResultSet getProductSearch(String text) {
        try {
            String query = "SELECT productcode,productname,costprice,sellprice,brand FROM products "
                    + "WHERE productcode LIKE '%" + text + "%' OR productname LIKE '%" + text + "%' OR brand LIKE '%" + text + "%'";
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet getProdFromCode(String text) {
        try {
            String query = "SELECT productcode,productname,costprice,sellprice,brand FROM products "
                    + "WHERE productcode='" + text + "' LIMIT 1";
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    // Search method for sales
    public ResultSet getSalesSearch(String text) {
        try {
            String query = "SELECT salesid,salesinfo.productcode,productname,\n"
                    + "                    salesinfo.quantity,revenue,users.name AS Sold_by\n"
                    + "                    FROM salesinfo INNER JOIN products\n"
                    + "                    ON salesinfo.productcode=products.productcode\n"
                    + "                    INNER JOIN users\n"
                    + "                    ON salesinfo.soldby=users.username\n"
                    + "                    INNER JOIN customers\n"
                    + "                    ON customers.customercode=salesinfo.customercode\n"
                    + "WHERE salesinfo.productcode LIKE '%" + text + "%' OR productname LIKE '%" + text + "%' "
                    + "OR users.name LIKE '%" + text + "%' OR customers.fullname LIKE '%" + text + "%' ORDER BY salesid;";
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    // Search method for purchase logs
    public ResultSet getPurchaseSearch(String text) {
        try {
            String query = "SELECT PurchaseID,purchaseinfo.productcode,products.productname,quantity,totalcost "
                    + "FROM purchaseinfo INNER JOIN products ON purchaseinfo.productcode=products.productcode "
                    + "INNER JOIN suppliers ON purchaseinfo.suppliercode=suppliers.suppliercode"
                    + "WHERE PurchaseID LIKE '%" + text + "%' OR productcode LIKE '%" + text + "%' OR productname LIKE '%" + text + "%' "
                    + "OR suppliers.fullname LIKE '%" + text + "%' OR purchaseinfo.suppliercode LIKE '%" + text + "%' "
                    + "OR date LIKE '%" + text + "%' ORDER BY purchaseid";
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet getProdName(String prodname) {
        try {
            String query = "SELECT productname FROM products WHERE productname='" + prodname + "'";
            resultSet = statement.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return resultSet;
    }

    public int getStock(String prodname) {
        String stocks = null;
        try {
            String query = "SELECT stock FROM products WHERE productname='" + prodname + "'";
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                stocks = resultSet.getString("stock");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        int stock = Integer.parseInt(stocks);
        return stock;
    }

    public String getPurchaseDate(int ID) {
        String date = null;
        try {
            String query = "SELECT date FROM purchaseinfo WHERE purchaseid='" + ID + "'";
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                date = resultSet.getString("date");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return date;
    }

    public String getSaleDate(int ID) {
        String date = null;
        try {
            String query = "SELECT date FROM salesinfo WHERE salesid='" + ID + "'";
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                date = resultSet.getString("date");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return date;
    }

    // Method to display product-related data set in tabular form
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
