/*
Function that checks if a books stock falls below the minimum threshold
*/
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

/*
When registering, the user inputs their billing address. At checkout they are asked if they would like to add an aditional address to be used as shipping address.
A new address is linked to the user and they checkout. If the user goes through checkout once more and picks the different address option, instead of a new address being created,
the previous shipping address would be updated.
*/
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

/*
Function used when overwriting the schema. 
*/
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
