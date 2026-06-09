CREATE TABLE IF NOT EXISTS customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    company VARCHAR(100)
);

INSERT INTO customers (name,  company) VALUES ('Gael Mora', 'Google Workspace User');
INSERT INTO customers (name, company) VALUES ('John Doe', 'ACME Corp');
