INSERT INTO roles (id, name, description) 
VALUES 
    ('11111111-1111-1111-1111-111111111111', 'ADMIN', 'Quản trị viên hệ thống'),
    ('22222222-2222-2222-2222-222222222222', 'ORGANIZER', 'Người tổ chức sự kiện'),
    ('33333333-3333-3333-3333-333333333333', 'EMPLOYEE', 'Nhân viên bình thường');

INSERT INTO departments (id, name, description) 
VALUES 
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Department 01', 'Phòng ban số 01');

INSERT INTO employees (id, fullname, email, employee_code, password, status, role_id, department_id) 
VALUES 
    ('99999999-9999-9999-9999-999999999999', 'Admin Tổng', 'admin@vt.com', 'ADMIN001', '$2a$10$E1jUq9IfPg3YVxATfknZ5.iWOcPb/CtaGaXLhhawMYbfX2hJCqcE6', 'ACTIVE', '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa');
