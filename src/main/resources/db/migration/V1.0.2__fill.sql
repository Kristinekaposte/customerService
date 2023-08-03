INSERT INTO address (phone_number, country, city, postal_code)
VALUES
  ('1234567890', 'USA', 'New York', '10001'),
  ('9876543210', 'UK', 'London', '20001'),
  ('5554443333', 'Canada', 'Toronto', '30001');

INSERT INTO customer (email, password, first_name, last_name, address_id)
VALUES
  ('john.doe@example.com', 'password123', 'John', 'Doe', 1),
  ('jane.smith@example.com', 'pass456word', 'Jane', 'Smith', 2),
  ('bob.johnson@example.com', 'secret123', 'Bob', 'Johnson', 3);
