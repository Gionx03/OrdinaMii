
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,

    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(30),

    role VARCHAR(30) NOT NULL,

    created_at TIMESTAMP,
    updated_at TIMESTAMP
    );


CREATE TABLE IF NOT EXISTS dish (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(150) NOT NULL,
    description TEXT,
    price NUMERIC(10,2) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,

    category VARCHAR(50) NOT NULL
    );


CREATE TABLE IF NOT EXISTS restaurant_table (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    number INTEGER NOT NULL UNIQUE,
    seats INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS reservation (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

   date DATE NOT NULL,
    time TIME NOT NULL,

    number_of_people INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL,

    user_id UUID NOT NULL,
    table_id UUID NOT NULL,

    CONSTRAINT fk_reservation_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_reservation_table
    FOREIGN KEY (table_id)
    REFERENCES restaurant_table(id)
    ON DELETE CASCADE,

    CONSTRAINT uk_table_date_time UNIQUE (table_id, date, time)
    );


CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    order_date TIMESTAMP NOT NULL,
    total NUMERIC(10,2),

    status VARCHAR(30) NOT NULL,
    payment_status VARCHAR(30) NOT NULL,

    user_id UUID NOT NULL,
    reservation_id UUID,

    CONSTRAINT fk_order_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_order_reservation
    FOREIGN KEY (reservation_id)
    REFERENCES reservation(id)
    ON DELETE SET NULL
    );


CREATE TABLE IF NOT EXISTS order_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    quantity INTEGER NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL,

    order_id UUID NOT NULL,
    dish_id UUID NOT NULL,

    CONSTRAINT fk_item_order
    FOREIGN KEY (order_id)
    REFERENCES orders(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_item_dish
    FOREIGN KEY (dish_id)
    REFERENCES dish(id)
    );


CREATE TABLE IF NOT EXISTS assistance_request (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    message TEXT NOT NULL,

    status VARCHAR(30) NOT NULL,

    created_at TIMESTAMP
    );