import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static String jdbcURL = "jdbc:postgresql://localhost:5432/postgres";
    static String username = "postgres";
    static String password = "0795";
    static Connection connection;
    private static String Login; //Stores the user_id of the current user/owner

    static {
        try {
            connection = DriverManager.getConnection(jdbcURL, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main function
     * @param args
     * @throws SQLException
     */
    public static void main(String[] args) {
        startMenu();
    }

    /**
     * Allows the user to login or register an account.
     */
    public static void startMenu() {
        try {
            System.out.println("Welcome to The Book Store!");
            System.out.println();
            System.out.println("type r to register an account");
            System.out.println("type l to login to your account");
            System.out.println("type h for more help");
            System.out.println();

            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNext()){
                String s1 = scanner.next();
                //exits the program if you write exit
                if(s1.equals("exit") || s1.equals("quit") || s1.equals("q")) {
                    System.exit(0);

                }else if(s1.equals("r")){
                    registerAccount();
                }else if(s1.equals("l")){
                    logIn();
//                }else if(s1.equals("v")){
//                    displayBooks();
//                }else if(s1.equals("s")){
//                    findBook();
                } else if(s1.equals("h")) {
                    System.out.println("r : register");
                    System.out.println("l : login");
                    System.out.println("q : terminate program");
//                    System.out.println("v : view books in store");
//                    System.out.println("s : search for a book in store");
//                    System.out.println();
                }
            }

        }catch (SQLException e){
            System.out.println("Connection error to PostgreSQL server");
            e.printStackTrace();
        }
    }

    /**
     * Allows a new user to register a new account. Uses Scanner to store the inputted
     * values to be inserted into the user entity in the project schema.
     * @throws SQLException
     */
    public static void registerAccount() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String usern = checkIdTaken(scanner);
        System.out.println("Enter your email: ");
        String email = scanner.next();
        System.out.println("Enter your password: ");
        scanner.nextLine();
        String password = scanner.nextLine();
        System.out.println("Enter your first name: ");
        String firstn = scanner.next();
        System.out.println("Enter your last name: ");
        String lastn = scanner.next();
        System.out.println("Enter your phone number: ");
        long phonenum = checkLong(scanner,"Enter your phone number: " );

        String sql2 = "INSERT INTO project.user(user_id, email, password, first_name, last_name, phone_number) VALUES" + " ('"+usern+"', '"+email+"', '"+password+"', '"+firstn+"', '"+lastn+"', '"+phonenum+"');";

        Statement statement1 = connection.createStatement();
        statement1.executeUpdate(sql2);
        createAndLinkUserAddress(usern);
        linkUserToCart(usern);
        Login = usern;
        userActions();

    }

    /**
     * Allows the user/owner to login to the bookstore using their email
     * and password.
     * @throws SQLException
     */
    public static void logIn() throws SQLException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your Email: ");
        String email = scanner.next();

        System.out.println("Enter your password: ");
        String password = scanner.next();

        String sql = "Select * FROM project.user WHERE email = '"+email+"' AND password ='"+password+"';";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);

        while (!result.next()){
            System.out.println("Invalid Email and Password, Try Again!");
            System.out.println("Enter your Email: ");
            email = scanner.next();

            System.out.println("Enter your password: ");
            password = scanner.next();

            sql = "Select * FROM project.user WHERE email = '"+email+"' AND password ='"+password+"';";
            statement = connection.createStatement();
            result = statement.executeQuery(sql);
        }

        Login = result.getString("user_id");
        sql = "Select * FROM project.user WHERE email = '"+email+"' AND password ='"+password+"'AND isOwner='"+true+"';";
        statement = connection.createStatement();
        result = statement.executeQuery(sql);

        if (result.next()){
            ownerActions();
        }else{
            System.out.println("Welcome to Look Inna Book");
            userActions();
        }

    }

    /**
     * Displays the list of all books in the library.
     * @throws SQLException
     */
    public static void displayBooks() throws SQLException {
        System.out.println("\nInventory:");
        String sql= "Select count (*) AS totalBooks FROM project.book;";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
        while (result.next()){
            System.out.println("Different Books: "+ result.getString("totalBooks"));
        }

        sql = "Select * FROM project.book;";
        statement = connection.createStatement();
        result = statement.executeQuery(sql);
        while (result.next()){
            System.out.println("ISBN: "+ result.getLong("ISBN") + " \t Title: " + result.getString("Title") + " Genre: "+result.getString("genre") + " Author: " + result.getString("author_firstn")+" "+result.getString("author_lastn") + " In Stock: "+ result.getInt("inventory") + " Copies Sold: "+ result.getInt("sales"));
        }
        System.out.println();
    }

    /**
     * Prints out the owner actions for the owner after login. Uses scanner
     * to choose action to be performed.
     * @throws SQLException
     */
    public static void ownerActions() throws SQLException{
        System.out.println("You have Accessed Owner portal");
        System.out.println("Options:");
        System.out.println("type d to display full inventory");
        System.out.println("type a to add a book");
        System.out.println("type r to remove a book");
        System.out.println("type s to check reports");
        System.out.println("type l to logout of admin account");
        System.out.println("type h for more help");
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()){
            String s1 = scanner.next();
            //exits the program if you write exit
            if(s1.equals("exit")) {
                System.exit(0);

            }else if(s1.equals("d")){
                fullDisplay();
            }else if(s1.equals("a")){
                addBook();
            }else if(s1.equals("r")){
                removeBook();
            }else if(s1.equals("s")) {
                salesCheck();
            }else if(s1.equals("l")) {
                startMenu();
            }else if(s1.equals("h")){
                System.out.println("h : help");
                System.out.println("r : register");
                System.out.println("s : search");
                System.out.println("cart: view cart");
                System.out.println("checkout : buy items in cart");
                System.out.println();
            }
        }


    }

    /**
     * Allows the user to view all the books in the book store.
     * @throws SQLException
     */
    public static void fullDisplay() throws SQLException{
        displayBooks();

        Scanner scanner = new Scanner(System.in);
        System.out.println("\ntype m to access menu");
        System.out.println("type b to go back");
        while(scanner.hasNext()){
            String s = scanner.next();
            if (s.equals("m")){
                ownerActions();
            }else if (s.equals("b")){
                fullDisplay();
            }
        }

    }

    /**
     * Allows the owner to add a book to project.book using the isbn. If the book isbn
     * already exists in book, then the owner will be asked to enter the quantity of the
     * of that book they would like to add to inventory. Else, if the book is not in
     * the entity book, then the owner will is prompted to enter book information as
     * well as the information of the publisher.
     * @throws SQLException
     */
    public static void addBook() throws SQLException{
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter book ISBN: ");
        Long isbn = checkLong(scanner, "Enter book ISBN: ");

        String sql = "SELECT count(*) AS book_in_library FROM project.book WHERE isbn = '"+isbn+"';";
        Statement findIsbn = connection.createStatement();
        ResultSet matchBook = findIsbn.executeQuery(sql);

        int inLibrary = 0;
        while(matchBook.next()){
            inLibrary = matchBook.getInt("book_in_library");
        }
        if(inLibrary == 1){
            System.out.println("Book found in library");
            System.out.println("Enter the quantity of this books that you would like to add: ");
            int stock_increase = checkQuantity(scanner, "Enter the quantity of this books that you would like to add: ");

            String sql1 = "UPDATE project.book SET inventory = inventory + '"+stock_increase+"' WHERE isbn = '"+isbn+"';";

            Statement increaseStock = connection.createStatement();
            increaseStock.executeUpdate(sql1);

            transferMoney(stock_increase, isbn);

        }else{
            System.out.println("Enter book name: ");
            scanner.nextLine();
            String book_name = scanner.nextLine();

            System.out.println("Enter Author's first name: ");
            String author_fn = scanner.next();
            System.out.println("Enter Author's last name: ");
            String author_ln = scanner.next();
            System.out.println("Enter genre: ");
            String genre = scanner.next();
            System.out.println("Enter number of pages: ");
            int page_number = checkQuantity(scanner, "Enter number of pages: ");

            System.out.println("Enter total price: ");
            double total_price = checkMoney(scanner, "Enter total price: ");

            System.out.println("Enter publisher fee: ");
            double publish_fee = checkMoney(scanner, "Enter publisher fee: ");

            System.out.println("Enter stock amount: ");
            int stock = checkQuantity(scanner, "Enter stock amount: ");

            String sql2 = "INSERT INTO project.book(isbn, title, author_firstn, author_lastn, genre, page_num, sell_price, publisher_fee, inventory) values " +
                    " ('"+isbn+"', '"+book_name+"', '"+author_fn+"', '"+author_ln+"', '"+genre+"', '"+page_number+"', '"+total_price+"', '"+publish_fee+"', '"+stock+"');";

            Statement insertBook = connection.createStatement();
            insertBook.executeUpdate(sql2);

            linkPublisher(isbn);
            transferMoney(stock, isbn);
        }
        System.out.println("\ntype m to access menu");
        System.out.println("type b to go back");
        while(scanner.hasNext()){
            String s = scanner.next();
            if (s.equals("m")){
                ownerActions();
            }else if (s.equals("b")){
                addBook();
            }
        }
    }

    /**
     * Transfers the money between publisher and owner when the owner orders more
     * books to add to the inventory. The owner pays the publisher_fee price for each
     * book ordered multiplied by the quantity. The publisher receives the money from
     * the owner.
     * @param stock_increase
     * @param isbn
     * @throws SQLException
     */
    public static void transferMoney(int stock_increase, long isbn) throws SQLException {
        //Find the publisher fee of the book through its isbn primary key
        String sql = "SELECT publisher_fee FROM project.book WHERE isbn = '"+isbn+"';";
        Statement get_fee = connection.createStatement();
        ResultSet fee = get_fee.executeQuery(sql);
        double fee_amount = 0;
        while(fee.next()){
            fee_amount = fee.getDouble("publisher_fee");
        }
        double total = fee_amount * stock_increase;

        //Find the publisher of that specific book
        String sql1 = "SELECT publisher_name FROM project.publishes WHERE isbn = '"+isbn+"';";
        Statement getPublisher = connection.createStatement();
        ResultSet publisherResult = getPublisher.executeQuery(sql1);
        String publisher_name = "";
        while(publisherResult.next()){
            publisher_name = publisherResult.getString("publisher_name");
        }

        //Add the amount calculated to the publisher.
        String sql2 = "UPDATE project.publisher SET bank_account = bank_account + '"+total+"' WHERE publisher_name = '"+publisher_name+"';";
        Statement addToBankAccount = connection.createStatement();
        addToBankAccount.executeUpdate(sql2);

        //deduct the same amount calculated from the owner.
        String sql3 = "UPDATE project.owner SET expenditure = expenditure - '"+total+"' WHERE user_id ='admin';";
        Statement deductFromExpenditure = connection.createStatement();
        deductFromExpenditure.executeUpdate(sql3);
    }

    /**
     * links a publisher to the book they publish
     * @param isbn
     * @throws SQLException
     */
    public static void linkPublisher(Long isbn) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter publisher name: ");
        String publisher = scanner.nextLine();

        System.out.println("Enter year published for book: ");
        int year = checkQuantity(scanner,"Enter year published for book: ");

        String sql = "SELECT publisher_name, count(*) AS publisher_exist FROM project.publisher WHERE " +
                     "publisher_name ILIKE '"+publisher+"' GROUP BY publisher_name;";
        Statement findPublisher = connection.createStatement();
        ResultSet matchPublisher = findPublisher.executeQuery(sql);

        int inPublisher = 0;
        while(matchPublisher.next()){
            inPublisher = matchPublisher.getInt("publisher_exist");
            publisher = matchPublisher.getString("publisher_name"); //if the publisher name was entered in lower case, this is corrected here.
        }
        if(inPublisher == 1) {
            System.out.println("Publisher already exists as a publisher");
            System.out.println("Book is now linked to an existing publisher");

            String sql1 = "INSERT INTO project.publishes(isbn, publisher_name, year) values " +
                    " ('"+isbn+"', '"+publisher+"', '"+year+"');";

            Statement linkPublisherToBook = connection.createStatement();
            linkPublisherToBook.executeUpdate(sql1);

        }else{ //Else, if the publisher does not exist in the publisher relation, then add publisher and link to book through publishes.
            System.out.println("Enter publisher's email: ");
            String email = scanner.next();

            System.out.println("Enter publisher's phone number: ");
            long phone_num = checkLong(scanner,"Enter publisher's phone number: ");

            String sql2 = "INSERT INTO project.publisher(publisher_name, email, phone_number) values " +
                    " ('"+publisher+"', '"+email+"', '"+phone_num+"');";

            Statement insertPublisher= connection.createStatement();
            insertPublisher.executeUpdate(sql2);

            //book is linked to publisher
            String sql3 = "INSERT INTO project.publishes(isbn, publisher_name, year) values " +
                    " ('"+isbn+"', '"+publisher+"', '"+year+"');";

            Statement linkPublisherToBook = connection.createStatement();
            linkPublisherToBook.executeUpdate(sql3);

            createAndLinkPublisherAddress(publisher);
        }
    }

    /**
     * Function containing a scanner that allows the user to create their address.
     * @throws SQLException
     */
    public static void createUserAddress() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your street number: ");
        int street_num = checkQuantity(scanner, "Enter your street number: ");
        System.out.println("Enter your street name: ");
        scanner.nextLine();
        String street_name = scanner.nextLine();
        System.out.println("Enter your apartment (number or letter): ");
        String apartment = scanner.nextLine();
        System.out.println("Enter your city name:");
        String city = scanner.next();
        System.out.println("Enter your province name: ");
        String province = scanner.next();
        System.out.println("Enter your country name: ");
        scanner.nextLine();
        String country = scanner.nextLine();
        System.out.println("Enter your postal code: ");
        String postal = scanner.next();


        String sql = "INSERT INTO project.address(street_num, street_name, apartment, city, province, country, postal_code) VALUES" +
                " ('"+street_num+"', '"+street_name+"', '"+apartment+"', '"+city+"', '"+province+"', '"+country+"', '"+postal+"');";


        Statement statement1 = connection.createStatement();
        statement1.executeUpdate(sql);
    }

    /**
     * Calls the function that prompts the user to enter their address and links the
     * newly created user address to the address table via the relation userAddress present
     * in the project schema.
     * @param user_id
     * @throws SQLException
     */
    public static void createAndLinkUserAddress(String user_id) throws SQLException {
        //calls the function that creates the address
        createUserAddress();

        //looks up the address that we just added and finds the address_id
        String sql2 = "SELECT currval('project.address_address_id_seq'::regclass);";
        Statement findAddress = connection.createStatement();
        ResultSet matchAddress = findAddress.executeQuery(sql2);

        int address_id = 0;
        while(matchAddress.next()){
            address_id = matchAddress.getInt("currval");
        }

        //Adds the link between user and address
        String sql3 = "INSERT INTO project.userAddress(address_id, user_id) values " +
                " ('"+address_id+"', '"+user_id+"');";

        Statement linkUserToAddress = connection.createStatement();
        linkUserToAddress.executeUpdate(sql3);
    }

    public static void linkUserToCart(String user_id) throws SQLException {
        //increments the bigserial of the cart_id in cart
        String sql = "INSERT INTO project.cart VALUES(nextval('project.cart_cart_id_seq'::regclass));";
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);

        String sql1 = "SELECT currval('project.cart_cart_id_seq'::regclass);";
        Statement findCart = connection.createStatement();
        ResultSet matchCart = findCart.executeQuery(sql1);
        int cart_id = 0;
        while(matchCart.next()){
            cart_id = matchCart.getInt("currval");
        }

        String sql2 = "INSERT INTO project.userCart(user_id, cart_id) VALUES ('"+user_id+"', '"+cart_id+"');";
        Statement statement1 = connection.createStatement();
        statement1.executeUpdate(sql2);
    }

        //function that

    /**
     * Creates the address for any publisher the owner adds and links it that newly
     * created address to the address table via the relation publihserAddress in the project
     * schema.
     * @param publisher_name
     * @throws SQLException
     */
    public static void createAndLinkPublisherAddress(String publisher_name) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter street number: ");
        int street_num = checkQuantity(scanner, "Enter street number: ");
        System.out.println("Enter street name: ");
        scanner.nextLine();
        String street_name = scanner.nextLine();
        System.out.println("Enter apartment (number or letter): ");
        String apartment = scanner.nextLine();
        System.out.println("Enter city name:");
        String city = scanner.next();
        System.out.println("Enter province name: ");
        String province = scanner.next();
        System.out.println("Enter country name: ");
        scanner.nextLine();
        String country = scanner.nextLine();
        System.out.println("Enter postal code: ");
        String postal = scanner.next();

        String sql = "INSERT INTO project.address(street_num, street_name, apartment, city, province, country, postal_code) VALUES" +
                                            " ('"+street_num+"', '"+street_name+"', '"+apartment+"', '"+city+"', '"+province+"', '"+country+"', '"+postal+"');";


        Statement statement1 = connection.createStatement();
        statement1.executeUpdate(sql);

        //looks up the address that we just added and finds the address_id
        String sql2 = "SELECT * FROM project.address WHERE street_name = '"+street_name+"' AND street_num = '"+street_num+"' AND apartment = '"+apartment+"';";
        Statement findAddress = connection.createStatement();
        ResultSet matchAddress = findAddress.executeQuery(sql2);
        int address_id = 0;
        while(matchAddress.next()){
            address_id = matchAddress.getInt("address_id");
        }

        //Adds the link between publisher and publisher address
        String sql3 = "INSERT INTO project.publisherAddress(publisher_name, address_id) values " +
                " ('"+publisher_name+"', '"+address_id+"');";

        Statement linkPublisherToBook = connection.createStatement();
        linkPublisherToBook.executeUpdate(sql3);

        System.out.println("Publisher has been added to the system");
        System.out.println("Book is now linked to publisher");

        System.out.println("\ntype m to access menu");
        System.out.println("type b to go back");

    }

    /**
     * Checks if a user_id is already taken at registration. Prompts the user to enter a
     * valid id again and prints a suggested id for a user that can be used if the user wishes.
     * @param scanner
     * @return
     * @throws SQLException
     */
    public static String checkIdTaken(Scanner scanner) throws SQLException {
        String id = "";
        int userExists = 0;
        boolean exists = true;
        while(exists) {
            if (scanner.hasNext()) {
                id = scanner.next();
            }
            String sql = "SELECT count(*) AS userFound FROM project.user WHERE user_id = '"+id+"';";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);

            while (result.next()){
                userExists = result.getInt("userFound");
            }

            if(userExists == 0){
                return id;

            }else if(userExists == 1) {
                int[] intArray = {0,1,2,3,4,5,6,7,8,9};
                String idx = String.valueOf(new Random().nextInt(intArray.length));
                for(int i = 0; i<4; i++){
                    idx = idx + new Random().nextInt(intArray.length);
                }
                String recommendedName = id+idx;
                System.out.println("This username is already in use!");
                System.out.println("try "+recommendedName);
                System.out.println("Enter your username: ");
                continue;
            }
            exists = false;
        }
        return id;
    }

    /**
     * Function that makes sure the user of the program does not enter mismatched
     * values into the scanner that will cause an error in the SQL statement execution.
     * @param scanner the current scanner in use.
     * @param message the message of the question to be printed again.
     * @return the money value
     */
    public static double checkMoney(Scanner scanner, String message){
        double money = 0;
        boolean notnum = true;
        while (notnum) {
            if (scanner.hasNextDouble())
                money = scanner.nextDouble();
            else {
                System.out.println("Please enter money value!");
                System.out.println(message);
                scanner.next();
                continue;
            }
            notnum = false;
        }
        return money;
    }

    /**
     * Function that makes sure the user enters an integer value in the scanner.
     * Any other value that is not int will prompt the user to enter the quantity again.
     * @param scanner the current scanner in use.
     * @param message the message of the question to be printed again.
     * @return the money value
     */
    public static int checkQuantity(Scanner scanner, String message){
        int quantity = 0;
        boolean notnum = true;
        while (notnum) {
            if (scanner.hasNextInt())
                quantity = scanner.nextInt();
            else {
                System.out.println("Please enter integer value!");
                System.out.println(message);
                scanner.next();
                continue;
            }
            notnum = false;
        }
        return quantity;
    }

    /**
     * Function that confirms the user of the program does not enter mismatched values
     * into scanner that is not of type long that which might cause an error in the SQL
     * statement execution. checlLong is used for isbn and phone numbers as these too values
     * can exceed the max value of int.
     * @param scanner the current scanner in use.
     * @param message the message of the question to be printed again.
     * @return the money value
     */
    public static long checkLong(Scanner scanner, String message){
        long phonenum = 0;
        boolean notnum = true;
        while (notnum) {
            if (scanner.hasNextLong())
                phonenum = scanner.nextLong();
            else {
                System.out.println("Please enter a valid number!");
                System.out.println(message);
                scanner.next();
                continue;
            }
            notnum = false;
        }
        return phonenum;
    }

    /**
     * Allows the owner to completely remove a book from the book store. Or
     * decrease the inventory of a specific book in stock.
     * @throws SQLException
     */
    public static void removeBook() throws SQLException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter book ISBN: ");
        String isbn = scanner.next();
        int before=0;
        int after=0;

        String sql= "Select count (*) AS totalBooks FROM project.book;";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
        while (result.next()){
            before = result.getInt("totalBooks");
        }
        if(before ==0){
            System.out.println("No books to remove.");
            return;
        }

        sql = "DELETE FROM project.publishes WHERE isbn = '"+isbn+"';";
        Statement statement1 = connection.createStatement();
        statement1.executeUpdate(sql);


        sql = "DELETE FROM project.book WHERE isbn = '"+isbn+"';";
        Statement statement2 = connection.createStatement();
        statement2.executeUpdate(sql);

        sql= "Select count (*) AS totalBooks FROM project.book;";
        Statement statement3 = connection.createStatement();
        ResultSet result2 = statement3.executeQuery(sql);
        while (result2.next()){
            after = result2.getInt("totalBooks");
        }

        if (before > after){
            System.out.println("Book Removed Succesfully!");
        }else{
            System.out.println(("Failed to remove Book!"));
        }
        System.out.println("\ntype m to access menu");
        System.out.println("type b to go back");
        while(scanner.hasNext()){
            String s = scanner.next();
            if (s.equals("m")){
                ownerActions();
            }else if (s.equals("b")){
                removeBook();
            }
        }
    }

    /**
     * Allows the owner to print reports. The owner has the option to choose from a list of
     * four different reports as per project requirement.
     * @throws SQLException
     */
    public static void salesCheck() throws SQLException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("type p for publisher profit report"); //money paid to each publisher
        System.out.println("type t for total sales report"); //show sales vs. expenditures
        System.out.println("type g for total sales per genre report"); //sales per genres
        System.out.println("type a for total sales per author report"); //sales per author

        while(scanner.hasNext()){
            String s1 = scanner.next();
            if(s1.equals("p")){
                String sql = "Select publisher_name, bank_account FROM project.publisher;";
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(sql);
                while (result.next()){
                    System.out.println("Publisher profit report: ");
                    System.out.println("Publisher: "+ result.getString("publisher_name") + " \t Publisher Store Profit: " + result.getString("bank_account"));
                }
            }

            if(s1.equals("t")){
                System.out.println("Total Sales Report: ");
                String sql = "Select sum(sales * sell_price) AS salesSum FROM project.book;";
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(sql);
                double salesSum = 0;
                while (result.next()){
                    salesSum = result.getDouble("salesSum");
                }
                String sql1 = "SELECT expenditure FROM project.owner;";
                Statement statement1 = connection.createStatement();
                ResultSet result1 = statement.executeQuery(sql1);
                while (result1.next()){
                    System.out.println("Total Revenue: "+salesSum+" Total Expenditure: "+result1.getDouble("expenditure"));
                }
            }

            if(s1.equals("g")){
                System.out.println("Genre Sales Report: ");
                String sql = "SELECT DISTINCT genre, sum(sales * sell_price) AS revenue, sum(sales) AS salesSum FROM project.book GROUP BY genre;";
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(sql);
                while (result.next()){
                    System.out.println("Genre: "+result.getString("genre")+" Total Sales: "+result.getInt("salesSum")+" Total Revenue: "+result.getDouble("revenue"));
                }
            }

            if (s1.equals("a")){
                System.out.println("Author Sales Report: ");
                String sql = "SELECT DISTINCT author_firstn, author_lastn, sum(sales * sell_price) AS revenue, sum(sales) AS salesSum FROM project.book GROUP BY author_firstn, author_lastn;";
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(sql);
                while (result.next()){
                    System.out.println("Author: "+result.getString("author_firstn")+" "+result.getString("author_lastn")+ " Total Sales: "+result.getInt("salesSum")+" Total Revenue: "+result.getDouble("revenue"));
                }

            }
            break;

        }
        System.out.println("\ntype m to access menu");
        System.out.println("type b to go back");
        while(scanner.hasNext()){
            String s = scanner.next();
            if (s.equals("m")){
                ownerActions();
            }else if (s.equals("b")){
                salesCheck();
            }
        }

    }

    /**
     * Prints out the user actions for a user after login. Uses scanner
     * to choose action to be performed.
     * @throws SQLException
     */
    public static void userActions() throws SQLException{
        //displays the books for the user;
        displayBooks();

        System.out.println("Options:");
        System.out.println("type s to search for a book");
        System.out.println("type a to add a book to your cart");
        System.out.println("type r to remove from your cart");
        System.out.println("type v to view your cart");
        System.out.println("type o to view your order history");
        System.out.println("type t to track your order");
        System.out.println("type c to checkout");
        System.out.println("type l to logout");
        System.out.println("type h for more help");
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()){
            String s1 = scanner.next();
            //exits the program if you write exit
            if(s1.equals("exit")) {
                System.exit(0);

            }else if(s1.equals("s")){
                findBook();
            }else if(s1.equals("a")){
                addToCart();
            }else if(s1.equals("r")){
                removeCart();
            }else if(s1.equals("v")) {
                viewUserCart();
            }else if(s1.equals("c")) {
                checkout();
            }else if(s1.equals("o")) {
                viewOrders();
            }else if(s1.equals("t")) {
                trackOrder();
            }else if(s1.equals("l")) {
                startMenu();
            }else if(s1.equals("h")){
                System.out.println("h : help");
                System.out.println("r : register");
                System.out.println("s : search");
                System.out.println("cart: view cart");
                System.out.println("checkout : buy items in cart");
                System.out.println();
            }
        }

    }

    /**
     * Allows the user to search for a specific book. Users can by book isbn,
     * title, author, or genre. If a user enters part of a book title ex: "lord of the"
     * ILIKE is used in the SQL statement to ignore case sensitivity and find all the
     * books that contain those words in them.
     * @throws SQLException
     */
    public static void findBook() throws SQLException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("type i to search by ISBN");
        System.out.println("type t to search by Title");
        System.out.println("type a to search by Author");
        System.out.println("type g to search by Genre");
        while(scanner.hasNext()){
            String s1 = scanner.next();
            if(s1.equals("i")){
                System.out.println("Enter ISBN");
                long ISBN = checkLong(scanner,"Enter ISBN");

                String sql = "Select * FROM project.book WHERE ISBN ='"+ISBN+"';";
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(sql);
                System.out.println("Search Results: ");
                while (result.next()){
                    System.out.println("ISBN: "+ result.getLong("ISBN") + " \t Title: " + result.getString("Title") + " Genre: "+result.getString("genre") + " Author: " + result.getString("author_firstn")+" "+result.getString("author_lastn") + " Costs: "+ result.getInt("sell_price"));

                }
            }


            if(s1.equals("t")){
                System.out.println("Enter Title");
                scanner.nextLine();
                String title = scanner.nextLine();


                String sql = "Select * FROM project.book WHERE title ILIKE'%"+title+"%';";
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(sql);
                System.out.println("Search Results: ");
                while (result.next()){
                    System.out.println("ISBN: "+ result.getLong("ISBN") + " \t Title: " + result.getString("Title") + " \t Genre: "+result.getString("genre") + " \t Author: " + result.getString("author_firstn")+" "+result.getString("author_lastn") + " \t Costs: "+ result.getInt("sell_price"));

                }
            }

            if(s1.equals("a")){
                System.out.println("Enter Author Last Name");
                scanner.nextLine();
                String author = scanner.nextLine();

                String sql = "Select * FROM project.book WHERE author_lastn ILIKE'%"+author+"%';";
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(sql);
                System.out.println("Search Results: ");
                while (result.next()){
                    System.out.println("ISBN: "+ result.getLong("ISBN") + " \t Title: " + result.getString("Title") + " Genre: "+result.getString("genre") + " Author: " + result.getString("author_firstn")+" "+result.getString("author_lastn") + " Costs: "+ result.getInt("sell_price"));

                }
            }

            if (s1.equals("g")){
                System.out.println("Enter genre");
                scanner.nextLine();
                String genre = scanner.nextLine();

                String sql = "Select * FROM project.book WHERE genre ILIKE'%"+genre+"%';";
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(sql);
                System.out.println("Search Results: ");
                while (result.next()){
                    System.out.println("ISBN: "+ result.getLong("ISBN") + " Title: " + result.getString("Title") + " Genre: "+result.getString("genre") + " Author: " + result.getString("author_firstn")+" "+result.getString("author_lastn") + " Costs: "+ result.getInt("sell_price"));

                }
            }
            break;
        }
        System.out.println("\ntype m to access menu");
        System.out.println("type b to go back");
        while(scanner.hasNext()){
            String s = scanner.next();
            if (s.equals("m")){
                userActions();
            }else if (s.equals("b")){
                findBook();
            }
        }

    }

    /**
     * Allows the user to add book to cart by using the isbn of a book. If the
     * book isbn is invalid the scanner will prompt the user to try again with a
     * valid isbn. If a user enters more copies in their cart than what is available
     * in the book inventory. Then the quantity of the book is set to inventory.
     * @throws SQLException
     */
    public static void addToCart() throws SQLException{
        Scanner scanner =  new Scanner(System.in);
        System.out.println("Enter ISBN Book you would like to Add");
        int quantity = 0;
        long book_id = 0;
        int cartID = 0;
        String title="";

        boolean exists = false;
        book_id = checkLong(scanner,"Enter ISBN Book you would like to Add");
        String sql = "SELECT * FROM project.book WHERE isbn ='"+book_id+"';";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
        while(result.next()){
            title = result.getString("title");
            System.out.println("adding Book: "+title);
            exists = true;

        }
        if(exists){
            String getStock = "SELECT inventory FROM project.book WHERE isbn ='"+book_id+"';";
            Statement getStockAmount = connection.createStatement();
            ResultSet stockResult = getStockAmount.executeQuery(getStock);
            int inventory = 0;
            while(stockResult.next()){
                inventory = stockResult.getInt("inventory");
            }

            boolean inCart = false;
            String sql3 = "SELECT * FROM project.cartItem WHERE isbn ='"+book_id+"';";
            Statement statement3 = connection.createStatement();
            ResultSet result3 = statement3.executeQuery(sql3);
            int quantityInCart = 0;
            while(result3.next()){
                quantityInCart = result3.getInt("quantity");
                inCart = true;
            }

            System.out.println("How many copies would you like?");

            quantity = checkQuantity(scanner,"How many copies would you like?");
            String sql1 = "SELECT cart_id FROM project.userAddress NATURAL JOIN project.userCart WHERE user_id = '"+Login+"';";
            Statement statement1 = connection.createStatement();
            ResultSet result1 = statement1.executeQuery(sql1);

            while (result1.next()){
                cartID = result1.getInt("cart_id");
            }

            if (inCart && quantity != 0){
                //can't add more books to cart than what the store has in stock
                if(quantity+quantityInCart >= inventory){
                    quantity = inventory-quantityInCart;
                }
                String sql4 = "UPDATE project.cartItem SET quantity = quantity + '"+quantity+"' WHERE cart_id = '"+cartID+"'AND isbn = '"+book_id+"';";
                Statement statement4 = connection.createStatement();
                statement4.executeUpdate(sql4);
            }else if(quantity == 0) {
                System.out.println("No books were added to your cart.");
            }else{
                //can't add more books to cart than what the store has in stock
                if(quantity >= inventory){
                    quantity = inventory;
                }
                String sql2 = "INSERT INTO project.cartItem(cart_id, isbn, quantity) VALUES ('"+cartID+"', '"+book_id+"','"+quantity+"');";
                Statement statement2 = connection.createStatement();
                statement2.executeUpdate(sql2);
                System.out.println("Added " + quantity+" copies of: " +title+ " to your cart.");
            }

        }else{
            System.out.println("Book is not available at this store. Sorry :(");
        }


        System.out.println("\ntype m to access menu");
        System.out.println("type b to go back");
        while(scanner.hasNext()){
            String str = scanner.next();
            if (str.equals("m")){
                userActions();
            }else if (str.equals("b")){
                addToCart();
            }
        }
    }

    /**
     * Allows the user to remove a specific quantity of a book from their cart.
     * If the user removes more of a book than what was in cart, then the book is
     * removed completely from cart i.e. (cart quantity = 0).
     * @throws SQLException
     */
    public static void removeCart() throws SQLException{
        Scanner scanner =  new Scanner(System.in);
        System.out.println("Enter ISBN Book you would like to Remove");
        int quantity = 0;
        long s = 0;
        int cartID=0;
        String title="";

        s = checkLong(scanner,"Enter ISBN Book you would like to Remove");
        String sql = "SELECT * FROM project.cartItem WHERE isbn ='"+s+"';";

        boolean exists = false;
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
        while(result.next()){
            exists = true;
        }

        if (exists){
            String sq = "SELECT * FROM project.book WHERE isbn ='"+s+"';";
            Statement state = connection.createStatement();
            ResultSet r = state.executeQuery(sq);
            while(r.next()){
                title = r.getString("title");
                System.out.println("removing Book: "+title);
            }


            System.out.println("How many copies of this book would you like to remove?");
            quantity = checkQuantity(scanner,"How many copies of this book would you like to remove?");

            int inCart =0;
            String sql3 = "SELECT quantity FROM project.cartItem WHERE isbn ='"+s+"';";
            Statement statement3 = connection.createStatement();
            ResultSet result3 = statement3.executeQuery(sql3);
            while(result3.next()){
                inCart = result3.getInt("quantity");
            }

            String sql1 = "SELECT cart_id FROM project.userAddress NATURAL JOIN project.cart WHERE user_id = '"+Login+"';";
            Statement statement1 = connection.createStatement();
            ResultSet result1 = statement1.executeQuery(sql1);

            while (result1.next()){
                cartID = result1.getInt("cart_id");
            }

            if(inCart <= quantity){
                String sql2 = "DELETE FROM project.cartItem WHERE cart_id = '"+cartID+"'AND isbn = '"+s+"';";
                Statement statement2 = connection.createStatement();
                statement2.executeUpdate(sql2);
                System.out.println("Removed "+title+ " from cart.");
            }else if(inCart > quantity){
                String sql2 = "UPDATE project.cartItem SET quantity = quantity - '"+quantity+"' WHERE cart_id = '"+cartID+"'AND isbn = '"+s+"';";
                Statement statement2 = connection.createStatement();
                statement2.executeUpdate(sql2);
                System.out.println("Removed " + quantity+" copies of :" +title+ " from your cart.");
            }

        }else{
            System.out.println("That book is not in Cart");
        }

        System.out.println("\ntype m to access menu");
        System.out.println("type b to go back");

        while(scanner.hasNext()){
            String str = scanner.next();
            if (str.equals("m")){
                userActions();
            }else if (str.equals("b")){
                removeCart();
            }
        }

    }

    /**
     * Allows the user to view the items in their cart. The total quantity of each
     * book and the total expected price including tax.
     * @throws SQLException
     */
    public static void viewUserCart() throws SQLException{
        String sql = "SELECT cart_id FROM project.userCart WHERE user_id = '"+Login+"';";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
        String cart_id = "";
        while(result.next()){
            cart_id = result.getString("cart_id");
        }
        String sql1 = "SELECT isbn, quantity, title, sell_price FROM project.cartItem NATURAL JOIN project.book WHERE cart_id = '"+cart_id+"';";
        Statement statement1 = connection.createStatement();
        ResultSet result1 = statement1.executeQuery(sql1);
        long isbn = 0;
        int quantity = 0;
        String title = "";
        double sell_price = 0;
        int count = 0;
        double total_per_book = 0;
        double total_cart_value = 0;

        System.out.println("Your cart: ");
        while(result1.next()){
            count++;
            isbn = result1.getLong("isbn");
            quantity = result1.getInt("quantity");
            title = result1.getString("title");
            sell_price = result1.getDouble("sell_price");
            total_per_book = sell_price * quantity;
            total_cart_value+=total_per_book;

            System.out.println("Item "+count+": ISBN: "+isbn+" Title: "+title+" Quantity: "+quantity+" price/book: $"+sell_price+" Total price: $"+total_per_book);
        }
        System.out.println("Price Before Tax: $"+total_cart_value);
        System.out.println("GST: $"+ Math.round(total_cart_value*0.13*100.0)/100.0);
        System.out.println("Overall Price: : $"+ Math.round(total_cart_value*1.13*100.0)/100.0);


        System.out.println("\ntype m to access menu");
        System.out.println("type a to add a book");
        System.out.println("type r to remove a book");
        System.out.println("type c to checkout");

        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNext()){
            String str = scanner.next();
            if (str.equals("m")){
                userActions();
            }else if (str.equals("a")){
                addToCart();
            }else if (str.equals("r")) {
                removeCart();
            }else if (str.equals("c")) {
                checkout();
            }
        }
    }

    /**
     * Checks if the cart is empty by invoking SQL function created in postgres.
     * If the stock for a book is below 10 after a purchase, then the SQL function
     * returns true, otherwise false.
     * @return true if the stock < 10, false otherwise
     * @throws SQLException
     */
    public static boolean cartEmpty() throws SQLException {
        String sql = "SELECT cart_id FROM project.userCart WHERE user_id = '"+Login+"';";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
        String cart_id = "";
        while(result.next()){
            cart_id = result.getString("cart_id");
        }

        String sql1 = "SELECT count(*) FROM project.cartItem WHERE cart_id = '"+cart_id+"';";
        Statement statement1 = connection.createStatement();
        ResultSet result1 = statement1.executeQuery(sql1);
        int count = 0;
        while(result1.next()){
            count = result1.getInt("count");
        }
        if (count == 0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Allows the user to finalize their order. The user is asked to confirm their
     * checkout decision. If they want to checkout, then the shipping address created
     * at registration is printed for the user to confirm. If the user decides to change
     * their shipping address then a new Scanner is created to update the shipping
     * address of the current user.
     * @throws SQLException
     */
    public static void checkout() throws SQLException{
        Scanner scanner = new Scanner(System.in);

        if(!cartEmpty()){
            System.out.println("Are you sure you want to checkout?");

            while(scanner.hasNext()){
                String s1 = scanner.next();
                if(s1.equals("yes") || s1.equals("y") || s1.equals("Yes") || s1.equals("Y")){
                    String sql = "SELECT street_num, street_name, apartment, city, province, country, postal_code FROM project.address NATURAL JOIN project.userAddress WHERE user_id = '"+Login+"';";
                    Statement statement = connection.createStatement();
                    ResultSet result = statement.executeQuery(sql);
                    System.out.println("Your current address is: ");
                    String st_num = "";
                    String st_name = "";
                    String app = "";
                    String city = "";
                    String prov = "";
                    String ctry = "";
                    String postal = "";

                    while (result.next()){
                        st_num = result.getString("street_num");
                        st_name = result.getString("street_name");
                        app = result.getString("apartment");
                        city = result.getString("city");
                        prov = result.getString("province");
                        ctry = result.getString("country");
                        postal = result.getString("postal_code");

                        System.out.println("Street Number: "+st_num);
                        System.out.println("Street Name: "+st_name);
                        System.out.println("Apartment: "+app);
                        System.out.println("City: "+city);
                        System.out.println("Province: "+prov);
                        System.out.println("Country: "+ctry);
                        System.out.println("Postal Code: "+postal);
                    }
                    System.out.println("\nDo you want to use the same shipping address created at registration? (yes/no) ");
                    String s2 = scanner.next();
                    if(s2.equals("yes") || s2.equals("y") || s2.equals("Yes") || s2.equals("Y")){
                        createOrder();
                        return;

                    }else if(s2.equals("no") || s2.equals("n") || s2.equals("No") || s2.equals("N")){
                        //continue from here
                        createUserAddress();
                        Scanner scanner1 = new Scanner(System.in);
                        System.out.println("\nDo you want to make this shipping address your default shipping address? (yes/no) ");

                        String s3 = scanner1.next();
                        if(s3.equals("yes") || s3.equals("y") || s3.equals("Yes") || s3.equals("Y")){
                            return;
                        }else if(s3.equals("no") || s3.equals("n") || s3.equals("No") || s3.equals("N")){
                            createOrder();
                            return;
                        }
                    }
                }

                if(s1.equals("no") || s1.equals("n") || s1.equals("No") || s1.equals("N")){
                    userActions();
                }
                break;
            }
        }else{
            System.out.println("Your cart is empty.");
            System.out.println("Type m to return to menu");
            while(scanner.hasNext()){
                String s = scanner.next();
                if (s.equals("m")){
                    userActions();
                }
            }
        }

    }

    public static void updateShippingAddress(){
    }

    /**
     * Creates an order when a user checks out. In the while loop of the resultSet
     * the functions updateBookSales(isbn, quantity) and lessThanTenStock(isbn) are
     * called to update the sales quantity in book and prompt the owner to add more
     * books in case the inventory of a book drops below 10 books.
     * @throws SQLException
     */
    public static void createOrder() throws SQLException{
        int[] intArray = {0,1,2,3,4,5,6,7,8,9};
        String idx = String.valueOf(new Random().nextInt(intArray.length));
        for(int i = 0; i < 8; i++){
            idx = idx + new Random().nextInt(intArray.length);
        }
        String trackingNum = "CL"+idx+"CA";
        System.out.println("Your tracking number is: "+ trackingNum);
        //CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA project;


        //Find the address_id for the user shipping address
        String sql = "SELECT address_id FROM project.address NATURAL JOIN project.userAddress WHERE user_id = '"+Login+"' AND isShipping = true;";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
        int address_id = 0;

        while (result.next()){
            address_id = result.getInt("address_id");
        }

        //Find the cart_id of the current user;
        String sql1 = "SELECT cart_id FROM project.userCart WHERE user_id = '"+Login+"';";
        Statement statement1 = connection.createStatement();
        ResultSet result1 = statement1.executeQuery(sql1);
        int cart_id = 0;
        while(result1.next()){
            cart_id = result1.getInt("cart_id");
        }

        //Finds all the cart items associated with the cart_id of the current user;
        String sql2 = "SELECT isbn, quantity, title, sell_price FROM project.cartItem NATURAL JOIN project.book WHERE cart_id = '"+cart_id+"';";
        Statement statement2 = connection.createStatement();
        ResultSet result2 = statement2.executeQuery(sql2);
        int quantity = 0;
        double sell_price = 0;
        double total_per_book = 0;
        double total_cart_value = 0;
        long isbn = 0;

        while(result2.next()){
            isbn = result2.getLong("isbn");
            quantity = result2.getInt("quantity");
            sell_price = result2.getDouble("sell_price");
            total_per_book = sell_price * quantity;
            total_cart_value+=total_per_book;

            updateBookSales(isbn, quantity); //updates the sales number and inventory of each book that was ordered
            lessThanTenStock(isbn); //checks if the inventory of a book drops below 10 and adds 15 more books to that book's inventory.
        }
        total_cart_value = Math.round(total_cart_value*1.13*100.0)/100.0;

        //generate a UUID and store it in a variable to be inserted into order
        String sql3 = "SELECT uuid_generate_v4() AS order_id;";
        Statement statement3 = connection.createStatement();
        ResultSet result3 = statement3.executeQuery(sql3);
        String order_id= "";

        while (result3.next()){
            order_id = result3.getString("order_id");
        }

        String sql4 = "INSERT INTO project.order(order_num, tracking_num, total_price) VALUES ('"+order_id+"', '"+trackingNum+"', '"+total_cart_value+"');";
        Statement statement4 = connection.createStatement();
        statement4.executeUpdate(sql4);

        linkCheckoutToOrder(cart_id, order_id);
        linkOrderToAddress(address_id, order_id);
        clearUserCart(cart_id);

        Scanner scanner = new Scanner(System.in);
        System.out.println("\nType m to return to menu");
        while(scanner.hasNext()){
            String s = scanner.next();
            if (s.equals("m")){
                userActions();
            }
        }
    }

    /**
     * Clears all the books associated with a specific cart_id for a user after a
     * successful purchase from the book store.
     * @param cart_id
     * @throws SQLException
     */
    public static void clearUserCart(int cart_id) throws SQLException {
        String sql = "DELETE FROM project.cartItem WHERE cart_id = '"+cart_id+"';";
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
    }

    /**
     * Updates the sales quantity of any book after a user order. The quantity of the
     * book is deducted from the inventory (stock) of the book and added to the sales.
     * @param isbn
     * @param quantity
     * @throws SQLException
     */
    public static void updateBookSales(long isbn, int quantity) throws SQLException {
        //Updates the sales and inventory column of the book table in the project schema
        String sql = "UPDATE project.book SET sales = sales + '"+quantity+"', inventory = inventory - '"+quantity+"' WHERE isbn = '"+isbn+"';";
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
    }

    /**
     * checks if a specific book's stock drops below 10 books then 15 more books are
     * added by the owner. The function transferMoney(quantity, isbn) is called to
     * transfer the money from the owner to the publisher of a specific book.
     * @param isbn
     * @throws SQLException
     */
    public static void lessThanTenStock(long isbn) throws SQLException {
        //uses the function created to check if the amount of books is less than 10 after an order has been created.
        String sql = "SELECT check_stock_amount('"+isbn+"') AS inventory";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
        boolean lessThanTen = false;

        while (result.next()){
            lessThanTen = result.getBoolean("inventory");
        }

        if(lessThanTen){
            //orders 15 more books if the function check_stock_amount(isbn) returns true for any book
            String sql1 = "UPDATE project.book SET inventory = inventory + 15 WHERE isbn = '"+isbn+"';";
            Statement statement1 = connection.createStatement();
            statement1.executeUpdate(sql1);

            //transfer money from owner to publisher. 15*publisher_fee of the book is added to the publisher and deducted from owner
            transferMoney(15, isbn);
        }
    }

    /**
     * Links the order to cart via the relation checkout
     * @param cart_id
     * @param uuid
     * @throws SQLException
     */
    public static void linkCheckoutToOrder(int cart_id, String uuid) throws SQLException {
        String sql = "INSERT INTO project.checkout(cart_id, order_num) VALUES ('"+cart_id+"', '"+uuid+"');";
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
    }

    /**
     * Links the order to address via the relation orderAddress. The registration
     * address is not necessarily the same shipping address used for the order.
     * @param address_id
     * @param uuid
     * @throws SQLException
     */
    public static void linkOrderToAddress(int address_id, String uuid) throws SQLException {
        String sql = "INSERT INTO project.orderAddress(address_id, order_num) VALUES ('"+address_id+"', '"+uuid+"');";
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
    }

    /**
     * Allows the user to view all their past orders arranged by descending order date which
     * displays the most recent first at the top of the list in case of multiple orders
     * @throws SQLException
     */
    public static void viewOrders() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String sql = "SELECT order_num, tracking_num, order_date, carrier, total_price, A.address_id from project.order " +
                     "NATURAL JOIN project.orderAddress A, project.userAddress B WHERE user_id = '"+Login+"' ORDER BY order_date DESC;";

        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
        String order_number = "";
        String tracking_number = "";
        String order_date = "";
        String shipper = "";
        double total_price = 0.00;
        int address_id = 0;
        System.out.println("\nOrders: ");
        while(result.next()){
            order_number = result.getString("order_num");
            tracking_number = result.getString("tracking_num");
            order_date = result.getString("order_date");
            shipper = result.getString("carrier");
            address_id = result.getInt("address_id");
            total_price = result.getDouble("total_price");

            System.out.println("\nOrder Number: "+order_number);
            System.out.println("Tracking Number: "+tracking_number);
            System.out.println("Date of Purchase: "+order_date);
            System.out.println("Carrier: "+shipper);
            System.out.println("Total: $"+total_price);
            System.out.println("Shipped to: "+getAddress(address_id));
        }
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("\ntype m to go back to user menu");
        while(scanner.hasNext()){
            String s = scanner.next();
            if (s.equals("m")){
                userActions();
            }
        }
    }

    /**
     * tracks an order using a valid tracking number. To obtain a valid tracking number, the user must
     * view their orders in order to retrieve a tracking number.
     * @throws SQLException
     */
    public static void trackOrder() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the tracking number of the order you would like to track");
        String tracking_number = scanner.next();

        String sql = "SELECT order_num, order_date, carrier, address_id from project.order NATURAL JOIN project.orderAddress WHERE tracking_num = '"+tracking_number+"';";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
        String order_number = "";
        String order_date = "";
        String shipper = "";
        int address_id = 0;
        boolean empty = true;

        while (result.next()){
            order_number = result.getString("order_num");
            order_date = result.getString("order_date");
            shipper = result.getString("carrier");
            address_id = result.getInt("address_id");

            System.out.println("\nOrder Number: "+order_number);
            System.out.println("Ordered On: "+order_date);
            System.out.println("Carrier: "+shipper);
            System.out.println("Shipped to: "+getAddress(address_id));

            empty = false;
        }

        if(empty){
            System.out.println("You have entered an invalid tracking number");
            System.out.println("Check order history for tracking number");
        }

        System.out.println("\ntype o to view to your order history");
        System.out.println("type t to track another order");
        System.out.println("type m to go back to user menu");

        while(scanner.hasNext()){
            String s = scanner.next();
            if (s.equals("o")){
                viewOrders();
            }else if(s.equals("t")){
                trackOrder();
            }else if(s.equals("m")){
                userActions();
            }
        }
    }

    /**
     * Function that returns the address of the user as a string.
     * @param address_id
     * @return the full address of the user as a single line String
     * @throws SQLException
     */
    public static String getAddress(int address_id) throws SQLException {
        String sql = "SELECT street_num, street_name, apartment, city, province, country, postal_code FROM project.address NATURAL JOIN project.userAddress WHERE user_id = '"+Login+"';";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
        String st_num = "";
        String st_name = "";
        String app = "";
        String city = "";
        String prov = "";
        String ctry = "";
        String postal = "";
        String fullAddress = "";
        while (result.next()){
            st_num = result.getString("street_num");
            st_name = result.getString("street_name");
            app = result.getString("apartment");
            city = result.getString("city");
            prov = result.getString("province");
            ctry = result.getString("country");
            postal = result.getString("postal_code");

            fullAddress = st_num +" "+ st_name +" "+ app +" "+ prov +" "+ city +" "+ prov +" "+ ctry +" "+ postal;
        }

        return fullAddress;
    }
}
