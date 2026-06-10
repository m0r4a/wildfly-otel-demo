package com.demo;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.Random;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/api/data")
public class BackendServlet extends HttpServlet {

    private Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection("jdbc:postgresql://postgres:5432/demo_db", "demo_user", "demo_pass");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        try {
            if ("getOne".equals(action)) {
                String id = request.getParameter("id");
                try (Connection conn = getConnection();
                     PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM customers WHERE id = ?")) {
                    pstmt.setInt(1, Integer.parseInt(id));
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        out.print("{\"id\":" + rs.getInt("id") + ", \"name\":\"" + rs.getString("name") + "\", \"company\":\"" + rs.getString("company") + "\"}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Propagate 404
                        out.print("{\"error\":\"Customer record not found in the database.\"}");
                    }
                }
            }
            else if ("externalApi".equals(action)) {
                // Generates an external HTTP client span
                URL url = new URL("https://httpbin.org/get?demo=observability_tracing");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // Read response safely even if it's not JSON
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) { sb.append(line); }
                rd.close();
                out.print("{\"status\":\"success\", \"external_payload\":" + sb.toString() + "}");
            }
            else if ("simulateError".equals(action)) {
                // Throws an exception to test APM unhandled exception reporting
                throw new RuntimeException("Critical backend failure simulated for APM trace analysis.");
            }
            else if ("randomStatus".equals(action)) {
                // Arrays of HTTP statuses to test alerting rules and metric aggregations
                int[] statusCodes = {
                    301, 302, // Redirection
                    400, 401, 403, 404, // Client Errors
                    500, 502, 503, 504  // Server Errors
                };
                int randomCode = statusCodes[new Random().nextInt(statusCodes.length)];

                response.setStatus(randomCode); // Set the random status
                out.print("{\"status\":\"anomaly_injected\", \"http_code_returned\":" + randomCode + ", \"message\":\"Simulated HTTP response code for monitoring validation.\"}");
            }
            else {
                // getAll (Default execution)
                try (Connection conn = getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM customers")) {
                    StringBuilder json = new StringBuilder("[");
                    while (rs.next()) {
                        if (json.length() > 1) json.append(",");
                        json.append("{")
                            .append("\"id\":").append(rs.getInt("id")).append(",")
                            .append("\"name\":\"").append(rs.getString("name")).append("\",")
                            .append("\"company\":\"").append(rs.getString("company")).append("\"")
                            .append("}");
                    }
                    json.append("]");
                    out.print(json.toString());
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // General 500
            out.print("{\"error\":\"Internal Server Exception: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        if ("insert".equals(action)) {
            String name = request.getParameter("name");
            String company = request.getParameter("company");

            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("INSERT INTO customers (name, company) VALUES (?, ?)")) {
                pstmt.setString(1, name);
                pstmt.setString(2, company);
                int rows = pstmt.executeUpdate();
                response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
                out.print("{\"status\":\"success\", \"rows_inserted\":" + rows + ", \"message\": \"Customer record successfully provisioned.\"}");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // DB error 500
                out.print("{\"error\":\"Database transaction failed: " + e.getMessage() + "\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            out.print("{\"error\":\"Unsupported POST action requested.\"}");
        }
    }
}
