
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
    price NUMERIC(10, 2) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    image_url VARCHAR(255),

    category VARCHAR(50) NOT NULL
    );


CREATE TABLE IF NOT EXISTS restaurant_table (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    number INTEGER NOT NULL UNIQUE,
    seats INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);
-- Le prenotazioni con status = 'CANCELLED' non bloccano il tavolo.
-- Il controllo sugli slot occupati viene gestito nel service.
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
    ON DELETE CASCADE
    );


CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    order_date TIMESTAMP NOT NULL,
    total NUMERIC(10, 2) NOT NULL,

    -- Valori possibili per orders.status:
    -- PENDING, PREPARING, SERVED, CANCELLED
    status VARCHAR(30) NOT NULL,

    -- Valori possibili per orders.payment_status:
    -- NOT_PAID, PENDING, PAID, PAY_AT_COUNTER, CANCELLED
    payment_status VARCHAR(30) NOT NULL,

    -- Valori possibili per orders.order_type:
    -- TAKE_AWAY, ON_THE_TABLE
    order_type VARCHAR(30) NOT NULL,

    user_id UUID NOT NULL,
    table_id UUID,

    CONSTRAINT fk_order_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_order_table
    FOREIGN KEY (table_id)
    REFERENCES restaurant_table(id)
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

    created_at TIMESTAMP,
    resolved_at TIMESTAMP,
    table_id UUID NOT NULL,
    FOREIGN KEY (table_id) REFERENCES restaurant_table(id)
    );