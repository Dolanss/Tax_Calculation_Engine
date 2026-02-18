-- ISS Service codes based on LC 116/2003 (Brazilian federal law)
-- Municipality codes follow IBGE standard
-- Aliquots are constrained by federal law: min 2%, max 5%

INSERT INTO service_tax_rules (service_code, municipality_code, description, aliquot, min_aliquot, max_aliquot) VALUES
-- São Paulo (3550308)
('1.01', '3550308', 'Análise e desenvolvimento de sistemas',            2.00, 2.00, 5.00),
('1.02', '3550308', 'Programação',                                      2.00, 2.00, 5.00),
('1.03', '3550308', 'Processamento, armazenamento de dados e hospedagem',2.00, 2.00, 5.00),
('1.04', '3550308', 'Elaboração de programas de computadores',           2.00, 2.00, 5.00),
('1.05', '3550308', 'Licenciamento ou cessão de direito de uso de programas', 2.00, 2.00, 5.00),
('4.01', '3550308', 'Medicina e biomedicina',                            5.00, 2.00, 5.00),
('4.02', '3550308', 'Análises clínicas e patologia',                     5.00, 2.00, 5.00),
('17.01','3550308', 'Assessoria ou consultoria de qualquer natureza',    5.00, 2.00, 5.00),
('17.06','3550308', 'Propaganda e publicidade',                          5.00, 2.00, 5.00),

-- Rio de Janeiro (3304557)
('1.01', '3304557', 'Análise e desenvolvimento de sistemas',            2.00, 2.00, 5.00),
('1.02', '3304557', 'Programação',                                      2.00, 2.00, 5.00),
('1.03', '3304557', 'Processamento e hospedagem de dados',              3.00, 2.00, 5.00),
('4.01', '3304557', 'Medicina e biomedicina',                            5.00, 2.00, 5.00),
('17.01','3304557', 'Assessoria ou consultoria de qualquer natureza',    5.00, 2.00, 5.00),
('17.06','3304557', 'Propaganda e publicidade',                          5.00, 2.00, 5.00),

-- Belo Horizonte (3106200)
('1.01', '3106200', 'Análise e desenvolvimento de sistemas',            2.00, 2.00, 5.00),
('1.02', '3106200', 'Programação',                                      3.00, 2.00, 5.00),
('4.01', '3106200', 'Medicina e biomedicina',                            5.00, 2.00, 5.00),
('17.01','3106200', 'Assessoria ou consultoria de qualquer natureza',    4.00, 2.00, 5.00),

-- Curitiba (4106902)
('1.01', '4106902', 'Análise e desenvolvimento de sistemas',            2.00, 2.00, 5.00),
('1.02', '4106902', 'Programação',                                      2.50, 2.00, 5.00),
('4.01', '4106902', 'Medicina e biomedicina',                            5.00, 2.00, 5.00),
('17.01','4106902', 'Assessoria ou consultoria de qualquer natureza',    5.00, 2.00, 5.00);

-- Default admin user (password: Admin@123 — BCrypt encoded)
INSERT INTO users (username, password, role) VALUES
('admin', '$2a$12$mKMUvqiR4rLK9oVy/rk08u2QRRbhQ8i5JGqLJYiKW0ywFHV66gy2q', 'ROLE_ADMIN'),
('api_user', '$2a$12$mKMUvqiR4rLK9oVy/rk08u2QRRbhQ8i5JGqLJYiKW0ywFHV66gy2q', 'ROLE_USER');
