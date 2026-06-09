package com.demo;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/api/data")
public class BackendServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            out.print("{\"error\":\"PostgreSQL driver not found\"}");
            return;
        }

        // Connect to the PostgreSQL service defined in Docker Compose
        try (Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://postgres:5432/demo_db",
                    "demo_user",
                    "demo_pass");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM customers")) {

            StringBuilder json = new StringBuilder("[");

            while (rs.next()) {
                if (json.length() > 1) {
                    json.append(",");
                }

                json.append("{")
                    .append("\"id\":").append(rs.getInt("id")).append(",")
                    .append("\"name\":\"").append(rs.getString("name")).append("\",")
                    .append("\"company\":\"").append(rs.getString("company")).append("\"")
                    .append("}");
            }

            json.append("]");
            out.print(json.toString());

        } catch (Exception e) {
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
