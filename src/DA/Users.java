package DA;

import DT.UsersDT;
import Database.DatabaseHandler;
import UI.UsersFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Locale;
import java.util.Vector;

public class Users {

    Connection conn = null;
    PreparedStatement prepStatement = null;
    Statement statement = null;
    ResultSet resultSet = null;

    public Users() {
        try {
            conn = new DatabaseHandler().getConn();
            statement = conn.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void addUserDAO(UsersDT userDTO) {
        try {
            String username = userDTO.getUsername();

            // Check if the username already exists
            String usernameQuery = "SELECT * FROM users WHERE username='" + username + "'";
            resultSet = statement.executeQuery(usernameQuery);

            if (resultSet.next()) {
                JOptionPane.showMessageDialog(null, "Username already exists");
            } else {
                // Check if the user with the same details already exists
                String query = "SELECT * FROM users WHERE name='"
                        + userDTO.getFullName()
                        + "' AND location='"
                        + userDTO.getLocation()
                        + "' AND contact='"
                        + userDTO.getPhone()
                        + "'";
                resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                    JOptionPane.showMessageDialog(null, "User already exists");
                } else {
                    addFunction(userDTO);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addFunction(UsersDT userDTO) {
        try {
            String username = null;
            String password = null;
            String oldUsername = null;
            String resQuery = "SELECT * FROM users";
            resultSet = statement.executeQuery(resQuery);

            if (!resultSet.next()) {
                username = "root";
                password = "root";
            }

            String query = "INSERT INTO users (name,location,contact,username,password) "
                    + "VALUES(?,?,?,?,?)";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, userDTO.getFullName());
            prepStatement.setString(2, userDTO.getLocation());
            prepStatement.setString(3, userDTO.getPhone());
            prepStatement.setString(4, userDTO.getUsername());
            prepStatement.setString(5, userDTO.getPassword());
            prepStatement.executeUpdate();

            JOptionPane.showMessageDialog(null, "New user added.");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Method to edit existing user
    public void editUserDAO(UsersDT userDTO) {

        try {
            String query = "UPDATE users SET name=?,location=?,contact=? WHERE username=? "
                    + "AND password=?";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, userDTO.getFullName());
            prepStatement.setString(2, userDTO.getLocation());
            prepStatement.setString(3, userDTO.getPhone());
            prepStatement.setString(4, userDTO.getUsername());
            prepStatement.setString(5, userDTO.getPassword());
            prepStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Updated Successfully.");

        } catch (SQLException throwables) {
            JOptionPane.showMessageDialog(null, "Username and password don't match.");
            throwables.printStackTrace();
        }
    }

    // Method to retrieve data set to display in table
    public ResultSet getQueryResult() {
        try {
            String query = "SELECT userID,name,location,contact,username FROM users";
            resultSet = statement.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet getUserDAO(String username) {
        try {
            String query = "SELECT * FROM users WHERE username='" + username + "'";
            resultSet = statement.executeQuery(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return resultSet;
    }

    public void getFullName(UsersDT userDTO, String username) {
        try {
            String query = "SELECT * FROM users WHERE username='" + username + "' LIMIT 1";
            resultSet = statement.executeQuery(query);
            String fullName = null;
            if (resultSet.next()) {
                fullName = resultSet.getString(2);
            }
            userDTO.setFullName(fullName);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public ResultSet getUserLogsDAO() {
        try {
            String query = "SELECT logID,users.name,in_time,out_time FROM userlogs"
                    + " INNER JOIN users on userlogs.id=users.userID "
                    + "ORDER BY logID DESC";
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public void addUserLogin(UsersDT userDTO) {
        try {
            // Query to insert a row into the userlogs table
            String query = "INSERT INTO userlogs (id, in_time, out_time) VALUES (?, ?, ?)";
            PreparedStatement prepStatement = conn.prepareStatement(query);

            // Assuming getUserID() returns the user ID from the users table
            int userID = getUserID(String.valueOf(userDTO.getUsername()));

            // Set the user ID obtained from UsersDT object
            prepStatement.setInt(1, userID);
            prepStatement.setString(2, userDTO.getInTime());
            prepStatement.setString(3, userDTO.getOutTime());

            prepStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getUserID(String username) {
        int userID = -1; // Default value if no user is found or error occurs
        String query = "SELECT userID FROM users WHERE username = ?";
        try (PreparedStatement prepStatement = conn.prepareStatement(query)) {
            prepStatement.setString(1, username);

            try (ResultSet rs = prepStatement.executeQuery()) {
                if (rs.next()) {
                    userID = rs.getInt("userID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userID;
    }

    String password;

    public String getPassDAO(String username, String password) {
        try {
            String query = "SELECT password FROM users WHERE username='"
                    + username
                    + "' AND password='"
                    + password
                    + "'";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                this.password = resultSet.getString("password");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return this.password;
    }

    public void changePass(String username, String password, String newpass) {
        try {
            String query = "UPDATE users SET password=? WHERE username=? AND password=?";
            prepStatement = (PreparedStatement) conn.prepareStatement(query);
            prepStatement.setString(1, newpass);
            prepStatement.setString(2, username);
            prepStatement.setString(3, password);
            prepStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Password has been changed.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public ResultSet getUserSearch(String searchText) {
        try {
            String query = "SELECT userID,name,location,contact,username FROM users "
                    + "WHERE userID LIKE '%" + searchText + "%' OR name LIKE '%" + searchText + "%' "
                    + "OR location LIKE '%" + searchText + "%' OR contact LIKE '%" + searchText + "%' "
                    + "OR username LIKE '%" + searchText + "%' ORDER BY userID DESC";
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet getLogsSearch(String searchText) {
        try {
            String query = "SELECT logID,users.name,in_time,out_time FROM userlogs "
                    + "INNER JOIN users on userlogs.id=users.userID "
                    + "WHERE logID LIKE '%" + searchText + "%' OR users.name LIKE '%" + searchText + "%' "
                    + "OR in_time LIKE '%" + searchText + "%' OR out_time LIKE '%" + searchText + "%' "
                    + "ORDER BY logID DESC";
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public void deleteLogDAO(int ID) {
        try {
            String query = "DELETE FROM userlogs WHERE logID=?";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setInt(1, ID);
            prepStatement.executeUpdate();

            JOptionPane.showMessageDialog(null, "Transaction has been removed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to display data set in tabular form
    public DefaultTableModel buildTableModel(ResultSet resultSet, String[] customColumnNames) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int colCount = metaData.getColumnCount();

        Vector<String> columnNames = new Vector<>();
        for (int col = 1; col <= colCount; col++) {
            columnNames.add(customColumnNames[col - 1]); // Use custom column names provided
        }

        Vector<Vector<Object>> data = new Vector<>();
        while (resultSet.next()) {
            Vector<Object> vector = new Vector<>();
            for (int col = 1; col <= colCount; col++) {
                vector.add(resultSet.getObject(col));
            }
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }
}
