CREATE OR REPLACE FUNCTION check_stock_amount(book_isbn numeric(13,0))
    RETURNS BOOLEAN AS $$
BEGIN
    RETURN 10 > (
        SELECT inventory
        FROM project.book
        WHERE isbn = book_isbn
    );
END;
$$ 	LANGUAGE plpgsql;
--

CREATE OR REPLACE FUNCTION check_two_addresses(id_user varchar(20))
    RETURNS BOOLEAN AS $$
BEGIN
    RETURN 1 < (
        SELECT count(*)
        FROM project.address NATURAL JOIN project.userAddress
        WHERE user_id = id_user
    ) ;
END;
$$ 	LANGUAGE plpgsql;
--

CREATE OR REPLACE FUNCTION check_schema_exists()
    RETURNS BOOLEAN AS $$
BEGIN
    RETURN 1 = (
        SELECT count(*) FROM information_schema.schemata
        WHERE schema_name = 'project'
    ) ;
END;
$$ 	LANGUAGE plpgsql;
--
