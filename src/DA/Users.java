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
            String query = "UPDATE users SET name=?,location=?,contact=? WHERE username=?";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, userDTO.getFullName());
            prepStatement.setString(2, userDTO.getLocation());
            prepStatement.setString(3, userDTO.getPhone());
            prepStatement.setString(4, userDTO.getUsername());
            prepStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Updated Successfully.");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // Method to delete existing user
    public void deleteUserDAO(String username) {
        try {
            String query = "DELETE FROM users WHERE username=?";
            prepStatement = (PreparedStatement) conn.prepareStatement(query);
            prepStatement.setString(1, username);
            prepStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "User Deleted.");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
         new UsersFrame().loadDataSet();
    }

    // Method to retrieve data set to display in table
    public ResultSet getQueryResult() {
        try {
            String query = "SELECT * FROM users";
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

//    public ResultSet getUserLogsDAO() {
//        try {
//            String query = "SELECT users.name,userlogs.username,in_time,out_time,location FROM userlogs"
//                    + " INNER JOIN users on userlogs.username=users.username";
//            resultSet = statement.executeQuery(query);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return resultSet;
//    }

//    public void addUserLogin(UsersDT userDTO) {
//        try {
//            // Query to insert a row into the userlogs table
//            String query = "INSERT INTO userlogs (id, in_time, out_time) VALUES (?, ?, ?, ?)";
//            PreparedStatement prepStatement = conn.prepareStatement(query);
//
//            // Assuming getUserID() returns the user ID from the users table
//            int userID = getUserID(String.valueOf(userDTO.getUsername()));
//
//            // Set the user ID obtained from UsersDT object
//            prepStatement.setInt(1, userID);
//            prepStatement.setString(3, userDTO.getInTime());
//            prepStatement.setString(4, userDTO.getOutTime());
//
//            prepStatement.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

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

    public ResultSet getPassDAO(String username, String password) {
        try {
            String query = "SELECT password FROM users WHERE username='"
                    + username
                    + "' AND password='"
                    + password
                    + "'";
            resultSet = statement.executeQuery(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return resultSet;
    }

    public void changePass(String username, String password) {
        try {
            String query = "UPDATE users SET password=? WHERE username='" + username + "'";
            prepStatement = (PreparedStatement) conn.prepareStatement(query);
            prepStatement.setString(1, password);
            prepStatement.setString(2, username);
            prepStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Password has been changed.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Method to display data set in tabular form
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
