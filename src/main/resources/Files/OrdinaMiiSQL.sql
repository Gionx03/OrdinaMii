
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS restaurant_table (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    number INTEGER NOT NULL,
    seats INTEGER NOT NULL,
    available BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS dish (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE PRECISION NOT NULL,
    available BOOLEAN NOT NULL,
    category VARCHAR(50) NOT NULL,
    CONSTRAINT chk_dish_category CHECK (category IN ('ANTIPASTO', 'PRIMO', 'SECONDO', 'CONTORNO', 'DOLCE', 'BEVANDE'))
);

CREATE TABLE IF NOT EXISTS reservation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    date TIMESTAMP NOT NULL,
    number_of_people INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    user_id UUID NOT NULL,
    table_id UUID NOT NULL,

    CONSTRAINT fk_reservation_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_reservation_table
        FOREIGN KEY (table_id)
        REFERENCES restaurant_table(id),

    CONSTRAINT chk_reservation_status CHECK (status IN ('CONFIRMED', 'CANCELLED', 'COMPLETED'))
);

CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_date TIMESTAMP NOT NULL,
    total NUMERIC(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50),
    user_id UUID NOT NULL,
    reservation_id UUID,

    CONSTRAINT fk_order_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_order_reservation
        FOREIGN KEY (reservation_id)
        REFERENCES reservation(id),

    CONSTRAINT chk_order_status CHECK (status IN ('PENDING', 'PREPARING', 'SERVED', 'PAID', 'CANCELLED')),
    CONSTRAINT chk_payment_status CHECK (payment_status IS NULL OR payment_status IN ('NOT_PAID', 'PENDING', 'PAID', 'PAY_AT_COUNTER', 'CANCELLED'))
);

CREATE TABLE IF NOT EXISTS order_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quantity INTEGER NOT NULL,
    unit_price DOUBLE PRECISION NOT NULL,
    order_id UUID NOT NULL,
    dish_id UUID NOT NULL,

    CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id),

    CONSTRAINT fk_order_item_dish
        FOREIGN KEY (dish_id)
        REFERENCES dish(id)
);

CREATE TABLE IF NOT EXISTS assistance_request (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    message TEXT,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    resolved_at TIMESTAMP,
    table_id UUID,

    CONSTRAINT fk_assistance_request_table
        FOREIGN KEY (table_id)
        REFERENCES restaurant_table(id),

    CONSTRAINT chk_assistance_request_status CHECK (status IN ('PENDING', 'RESOLVED', 'CANCELLED'))
);
