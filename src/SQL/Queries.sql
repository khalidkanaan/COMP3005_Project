DROP Schema project CASCADE;
CREATE schema project;

CREATE TABLE project.user (
    user_id 	varchar(20) PRIMARY KEY,
    email 	varchar(100) UNIQUE,
    password 	varchar(16) NOT NULL,
    first_name 	varchar(20) NOT NULL,
    last_name 	varchar(20) NOT NULL,
    phone_number numeric(10,0),
    isOwner bool DEFAULT 'false' NOT NULL
    
);

CREATE TABLE project.owner (
    user_id 	varchar(20) REFERENCES project.user(user_id) ON DELETE CASCADE,
    expenditure numeric(10,2) DEFAULT 0,

    CONSTRAINT owner_pkey PRIMARY KEY (user_id)
);

CREATE TABLE project.book (
    isbn 	numeric(13,0) PRIMARY KEY,
    title 	varchar(150) NOT NULL,
    author_firstn   varchar(50) NOT NULL,
    author_lastn    varchar(50),
    genre   varchar(50)  NOT NULL,
    page_num 	numeric(4,0) NOT NULL,
    sell_price 	numeric(5,2)  NOT NULL,
    publisher_fee 	numeric(4,2)  NOT NULL,
    inventory integer DEFAULT 0,
    sales integer DEFAULT 0
);

CREATE TABLE project.address (
    address_id 	BIGSERIAL NOT NULL PRIMARY KEY,
    street_num 	numeric(8,0) NOT NULL,
    street_name varchar(100) NOT NULL,
    apartment 	varchar(10),
    city 	varchar(50) NOT NULL,
    province 	varchar(60) NOT NULL,
    country 	varchar(50) NOT NULL,
    postal_code varchar(6) NOT NULL,
    isShipping bool DEFAULT 'true' NOT NULL
);

CREATE TABLE project.publisher (
    publisher_name 	varchar(150) PRIMARY KEY,
    email 	varchar(100) UNIQUE,
    phone_number 	numeric(10,0) UNIQUE,
    bank_account 	numeric(15,2) DEFAULT 0
);

CREATE TABLE project.cart(
    cart_id 	BIGSERIAL NOT NULL PRIMARY KEY

);

CREATE TABLE project.order (
    order_num 	 	UUID NOT NULL PRIMARY KEY,
    tracking_num 	varchar(13) UNIQUE,
    order_date 	timestamp without time zone DEFAULT NOW(),
    carrier varchar(20) DEFAULT 'Canada Post' NOT NULL,
    total_price 	numeric(9,2)
);


CREATE TABLE project.publishes (
    isbn 	numeric(13,0) REFERENCES project.book(isbn) ON DELETE CASCADE,
    publisher_name 	varchar(150) REFERENCES project.publisher NOT NULL,
    year 	numeric(4,0),

    CONSTRAINT publishes_pkey PRIMARY KEY (isbn)
);


CREATE TABLE project.cartItem (
    cart_id 	BIGSERIAL NOT NULL REFERENCES project.cart(cart_id),
    isbn 	numeric(13,0) REFERENCES project.book(isbn) ON DELETE CASCADE,
    quantity 	integer,

    CONSTRAINT cart_item_pkey PRIMARY KEY (cart_id, isbn)
);

CREATE TABLE project.checkout (
    cart_id 	BIGSERIAL NOT NULL REFERENCES project.cart(cart_id),
    order_num 	UUID NOT NULL UNIQUE REFERENCES project.order(order_num),

    CONSTRAINT checkout_pkey PRIMARY KEY (cart_id, order_num)
);

CREATE TABLE project.userCart (
    cart_id 	BIGSERIAL NOT NULL REFERENCES project.cart(cart_id),
    user_id 	varchar(20) REFERENCES project.user(user_id) ON DELETE CASCADE,

    CONSTRAINT user_cart_pkey PRIMARY KEY (cart_id)
);

CREATE TABLE project.orderAddress (
    address_id 	BIGSERIAL REFERENCES project.address(address_id),
    order_num 	UUID REFERENCES project.order(order_num),

    CONSTRAINT order_address_pkey PRIMARY KEY (order_num, address_id),
    UNIQUE (order_num)

);

CREATE TABLE project.userAddress (
    address_id 	BIGSERIAL NOT NULL REFERENCES project.address(address_id),
    user_id 	varchar(20) NOT NULL REFERENCES project.user(user_id) ON DELETE CASCADE,

    CONSTRAINT 	user_address_pkey PRIMARY KEY (user_id, address_id)
);

CREATE TABLE project.publisherAddress (
    publisher_name 	varchar(150) REFERENCES project.publisher,
    address_id 	BIGSERIAL NOT NULL UNIQUE REFERENCES project.address(address_id),

    CONSTRAINT 	publisher_address_pkey PRIMARY KEY (publisher_name)
);