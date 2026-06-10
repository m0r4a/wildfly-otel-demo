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
        String action = request.getParameter("action");

        if (action != null) {
            proxyRequest(request, response, "GET");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Observability APM</title>");
        out.println("<style>");
        out.println("  :root {");
        out.println("    --bg: #08080d;");
        out.println("    --surface: #111118;");
        out.println("    --accent: #8c52ff;");
        out.println("    --accent-soft: #b794f4;");
        out.println("    --text: #e4e4eb;");
        out.println("    --text-muted: #9b9bb0;");
        out.println("    --border: #2a2a3a;");
        out.println("    --danger: #ff6188;");
        out.println("    --warning: #ffcc66;");
        out.println("    --radius: 8px;");
        out.println("  }");
        out.println("  * { box-sizing: border-box; margin: 0; padding: 0; }");
        out.println("  body {");
        out.println("    background: var(--bg);");
        out.println("    color: var(--text);");
        out.println("    font-family: 'Inter', system-ui, -apple-system, sans-serif;");
        out.println("    padding: 48px 64px;");
        out.println("    line-height: 1.6;");
        out.println("  }");
        out.println("  h1 {");
        out.println("    font-weight: 500;");
        out.println("    font-size: 2.2rem;");
        out.println("    margin-bottom: 40px;");
        out.println("    letter-spacing: -0.5px;");
        out.println("    color: var(--text);");
        out.println("  }");
        out.println("  h1 strong { color: var(--accent); font-weight: 600; }");
        out.println("  .grid {");
        out.println("    display: grid;");
        out.println("    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));");
        out.println("    gap: 28px;");
        out.println("    margin-bottom: 36px;");
        out.println("  }");
        out.println("  .card {");
        out.println("    background: var(--surface);");
        out.println("    border: 1px solid var(--border);");
        out.println("    border-radius: var(--radius);");
        out.println("    padding: 32px;");
        out.println("    transition: border-color 0.2s ease;");
        out.println("    display: flex;");
        out.println("    flex-direction: column;");
        out.println("  }");
        out.println("  .card:hover {");
        out.println("    border-color: rgba(140,82,255,0.4);");
        out.println("  }");
        out.println("  .card h2 {");
        out.println("    font-weight: 500;");
        out.println("    font-size: 1.2rem;");
        out.println("    margin-bottom: 16px;");
        out.println("    color: var(--text);");
        out.println("    display: flex;");
        out.println("    align-items: center;");
        out.println("    gap: 8px;");
        out.println("  }");
        out.println("  .card p.desc {");
        out.println("    font-size: 0.85rem;");
        out.println("    color: var(--text-muted);");
        out.println("    margin-bottom: 24px;");
        out.println("  }");
        out.println("  .input-group {");
        out.println("    display: flex;");
        out.println("    gap: 12px;");
        out.println("    margin-bottom: 20px;");
        out.println("    align-items: center;");
        out.println("  }");
        out.println("  input, .btn {");
        out.println("    font-family: inherit;");
        out.println("    font-size: 0.9rem;");
        out.println("    border-radius: 6px;");
        out.println("    transition: all 0.15s ease;");
        out.println("  }");
        out.println("  input {");
        out.println("    background: #16161f;");
        out.println("    border: 1px solid var(--border);");
        out.println("    padding: 11px 14px;");
        out.println("    color: var(--text);");
        out.println("    width: 100%;");
        out.println("  }");
        out.println("  input:focus {");
        out.println("    outline: none;");
        out.println("    border-color: var(--accent);");
        out.println("  }");
        out.println("  .btn {");
        out.println("    background: transparent;");
        out.println("    border: 1px solid var(--accent);");
        out.println("    color: var(--accent);");
        out.println("    padding: 11px 20px;");
        out.println("    font-weight: 500;");
        out.println("    letter-spacing: 0.3px;");
        out.println("    cursor: pointer;");
        out.println("    display: inline-flex;");
        out.println("    align-items: center;");
        out.println("    justify-content: center;");
        out.println("    gap: 6px;");
        out.println("    margin-bottom: 16px;");
        out.println("    width: fit-content;");
        out.println("  }");
        out.println("  .btn:hover {");
        out.println("    background: var(--accent);");
        out.println("    color: #fff;");
        out.println("    border-color: var(--accent);");
        out.println("  }");
        out.println("  .btn.danger {");
        out.println("    border-color: var(--danger);");
        out.println("    color: var(--danger);");
        out.println("  }");
        out.println("  .btn.danger:hover {");
        out.println("    background: var(--danger);");
        out.println("    color: #fff;");
        out.println("    border-color: var(--danger);");
        out.println("  }");
        out.println("  .btn.warning {");
        out.println("    border-color: var(--warning);");
        out.println("    color: var(--warning);");
        out.println("  }");
        out.println("  .btn.warning:hover {");
        out.println("    background: var(--warning);");
        out.println("    color: #111;");
        out.println("    border-color: var(--warning);");
        out.println("  }");
        out.println("  .full-width { grid-column: 1 / -1; }");
        out.println("  pre#console {");
        out.println("    background: #0b0b12;");
        out.println("    color: var(--accent-soft);");
        out.println("    padding: 24px;");
        out.println("    border-radius: var(--radius);");
        out.println("    border-left: 3px solid var(--accent);");
        out.println("    min-height: 240px;");
        out.println("    max-height: 420px;");
        out.println("    overflow: auto;");
        out.println("    font-family: 'JetBrains Mono', 'Fira Code', monospace;");
        out.println("    font-size: 0.8rem;");
        out.println("    white-space: pre-wrap;");
        out.println("    line-height: 1.7;");
        out.println("    margin-top: 12px;");
        out.println("  }");
        out.println("  @media (max-width: 700px) {");
        out.println("    body { padding: 24px; }");
        out.println("    .grid { grid-template-columns: 1fr; }");
        out.println("  }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<h1>Observability <strong>Dashboard</strong></h1>");

        out.println("<div class='grid'>");

        // Card: Queries
        out.println("<div class='card'>");
        out.println("<h2>Queries & External Calls</h2>");
        out.println("<p class='desc'>Data retrieval and outgoing service interactions.</p>");
        out.println("<button class='btn' onclick=\"fetchData('action=getAll','GET')\">Fetch All Customers</button>");
        out.println("<div class='input-group'>");
        out.println("<input type='number' id='userId' placeholder='Customer ID'>");
        out.println("<button class='btn' style='margin-bottom:0;' onclick=\"fetchData('action=getOne&id='+document.getElementById('userId').value,'GET')\">Fetch Customer</button>");
        out.println("</div>");
        out.println("<button class='btn' style='margin-bottom:0;' onclick=\"fetchData('action=externalApi','GET')\">Trigger External API Call</button>");
        out.println("</div>");

        // Card: Anomaly
        out.println("<div class='card'>");
        out.println("<h2>Anomaly & Error Injection</h2>");
        out.println("<p class='desc'>Test error handling and alerting rules.</p>");
        out.println("<button class='btn warning' onclick=\"fetchData('action=randomStatus','GET')\">Simulate Random HTTP Status</button>");
        out.println("<button class='btn danger' style='margin-bottom:0;' onclick=\"fetchData('action=simulateError','GET')\">Trigger Fatal Exception</button>");
        out.println("</div>");

        // Card: Mutability
        out.println("<div class='card'>");
        out.println("<h2>Data Mutability</h2>");
        out.println("<p class='desc'>Create records and observe write telemetry.</p>");
        out.println("<input type='text' id='newName' placeholder='Customer Name' style='margin-bottom:16px;'>");
        out.println("<input type='text' id='newCompany' placeholder='Company Name' style='margin-bottom:20px;'>");
        out.println("<button class='btn' style='margin-bottom:0;' onclick=\"submitData()\">Insert Database Record</button>");
        out.println("</div>");

        // Console full width
        out.println("<div class='card full-width'>");
        out.println("<h2>Service Response Logger</h2>");
        out.println("<p class='desc'>Real-time telemetry‑rich responses from your services.</p>");
        out.println("<pre id='console'>System idle. Awaiting user interaction...</pre>");
        out.println("</div>");

        out.println("</div>");

        out.println("<script>");
        out.println("const consoleEl = document.getElementById('console');");
        out.println("function fetchData(query, method) {");
        out.println("  consoleEl.textContent = '[request] Dispatching...';");
        out.println("  fetch('/frontend/dashboard?' + query, { method })");
        out.println("    .then(async res => {");
        out.println("       const text = await res.text();");
        out.println("       const header = '[HTTP ' + res.status + ' ' + res.statusText + ']\\n\\n';");
        out.println("       try {");
        out.println("         const parsed = JSON.parse(text);");
        out.println("         consoleEl.textContent = header + JSON.stringify(parsed, null, 2);");
        out.println("       } catch(e) {");
        out.println("         consoleEl.textContent = header + text;");
        out.println("       }");
        out.println("    })");
        out.println("    .catch(err => consoleEl.textContent = '[error] Network failure: ' + err);");
        out.println("}");
        out.println("function submitData() {");
        out.println("  const name = document.getElementById('newName').value.trim();");
        out.println("  const company = document.getElementById('newCompany').value.trim();");
        out.println("  if(!name || !company) {");
        out.println("    consoleEl.textContent = '[validation] Please fill all fields.';");
        out.println("    return;");
        out.println("  }");
        out.println("  consoleEl.textContent = '[request] Inserting record...';");
        out.println("  fetch('/frontend/dashboard?action=insert', {");
        out.println("    method: 'POST',");
        out.println("    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },");
        out.println("    body: 'name=' + encodeURIComponent(name) + '&company=' + encodeURIComponent(company)");
        out.println("  })");
        out.println("  .then(async res => {");
        out.println("       const text = await res.text();");
        out.println("       const header = '[HTTP ' + res.status + ' ' + res.statusText + ']\\n\\n';");
        out.println("       try {");
        out.println("         consoleEl.textContent = header + JSON.stringify(JSON.parse(text), null, 2);");
        out.println("       } catch(e) {");
        out.println("         consoleEl.textContent = header + text;");
        out.println("       }");
        out.println("  });");
        out.println("}");
        out.println("</script>");
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action != null) {
            proxyRequest(request, response, "POST");
        }
    }

    private void proxyRequest(HttpServletRequest request, HttpServletResponse response, String method)
            throws IOException {
        String action = request.getParameter("action");
        String backendUrlStr = "http://localhost:8080/backend/api/data?action=" + action;

        if (request.getParameter("id") != null) {
            backendUrlStr += "&id=" + request.getParameter("id");
        }

        URL url = new URL(backendUrlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);

        if ("POST".equals(method)) {
            conn.setDoOutput(true);
            String params = "name=" + URLEncoder.encode(request.getParameter("name") != null ? request.getParameter("name") : "", "UTF-8") +
                            "&company=" + URLEncoder.encode(request.getParameter("company") != null ? request.getParameter("company") : "", "UTF-8");
            try (OutputStream os = conn.getOutputStream()) {
                os.write(params.getBytes("UTF-8"));
            }
        }

        int backendStatusCode = conn.getResponseCode();
        response.setStatus(backendStatusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        InputStream is = (backendStatusCode >= 400) ? conn.getErrorStream() : conn.getInputStream();

        if (is != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                PrintWriter out = response.getWriter();
                String line;
                while ((line = br.readLine()) != null) {
                    out.println(line);
                }
            }
        } else {
            response.getWriter().println("{\"error\": \"Proxy failed to retrieve stream from backend service.\"}");
        }
    }
}
