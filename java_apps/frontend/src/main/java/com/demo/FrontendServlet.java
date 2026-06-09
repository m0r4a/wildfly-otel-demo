package com.demo;

import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/dashboard")
public class FrontendServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>APM Tracing Demo</title>");
        out.println("<style>body{font-family:Arial;margin:40px;}pre{background:#eee;padding:15px;border-radius:5px;}</style>");
        out.println("</head><body>");

        out.println("<h1>Monitoring Dashboard</h1>");
        out.println("<p>Retrieving data from the backend service.</p>");

        try {
            // Call the backend service running on the same WildFly instance
            URL url = new URL("http://localhost:8080/backend/api/data");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            rd.close();

            out.println("<h3>Backend Response</h3>");
            out.println("<pre>" + sb.toString() + "</pre>");

        } catch (Exception e) {
            out.println("<p style='color:red;'>Communication error: "
                    + e.getMessage() + "</p>");
        }

        out.println("</body></html>");
    }
}