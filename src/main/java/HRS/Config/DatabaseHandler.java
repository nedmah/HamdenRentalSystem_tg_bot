package HRS.Config;

import HRS.Service.Tool;
import HRS.Service.Type;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHandler {

    private static final String URL = "jdbc:sqlite:D:\\JavaFiles\\HamdenRentalSystem\\HamdenRentalSystem.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public List<Tool> catalog() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        Statement stmt = conn.createStatement();
        List<Tool> tools = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Tool");
        while (rs.next()) {
            Tool tool = new Tool(rs.getString("id"),rs.getString("name"),Double.parseDouble(rs.getString("price_per_hour")),Boolean.parseBoolean(rs.getString("deliverable")), Type.valueOf(rs.getString("tool_type")));
            tools.add(tool);
        }
        return tools;
    }

    public Map<String,String> userData() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        Statement stmt = conn.createStatement();
        Map<String,String> usersData = new HashMap();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Userr");
        while (rs.next()) {
          usersData.put(rs.getString("name"),rs.getString("password"));
        }
        return usersData;
    }

    //id текущего пользователя
    public String userID(String s) throws SQLException{
        Connection conn = DriverManager.getConnection(URL);
        Statement stmt = conn.createStatement();
        StringBuilder finish = new StringBuilder();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Userr WHERE name = '" + s + "'");
        while (rs.next()) {
            finish.append(rs.getString("id"));
        }
        return finish.toString();
    }

    //id текущего заказа
    public String orderID(String s) throws SQLException{
        Connection conn = DriverManager.getConnection(URL);
        Statement stmt = conn.createStatement();
        List a = new ArrayList();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Orderr WHERE fk_user_id = '" + s + "'");
        while (rs.next()) {
            a.add(rs.getString("id"));
        }
        return (String) a.get(a.size()-1);
    }

}





