import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class JDBC1 {
    static String jdbcURL = "jdbc:postgresql://localhost:5432/postgres";
    static String username = "postgres";
    static String password = "0795";
    static Connection connection;

    private static String Login;



    static {
        try {
            connection = DriverManager.getConnection(jdbcURL, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
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
                if(s1.equals("exit")) {
                    System.exit(0);

                }else if(s1.equals("r")){
                    registerAccount();
                }else if(s1.equals("l")){
                    logIn();
                }else if(s1.equals("h")){
                    System.out.println("h : help");
                    System.out.println("r : register");
                    System.out.println("s : search");
                    System.out.println("cart: view cart");
                    System.out.println("checkout : buy items in cart");
                    System.out.println();
                }
            }
            String email;
            email = scanner.nextLine();
            String pass;
            System.out.println("Enter your password:");
            pass = scanner.nextLine();
            String sql = "SELECT * FROM project.user WHERE email = '"+email+"' AND "+"password = '"+pass+"';";

            Statement statement = connection.createStatement();

            ResultSet resultSet1 = statement.executeQuery(sql);

            while(resultSet1.next()){
                System.out.println("Welcome "+resultSet1.getString("user_id"));
            }

            connection.close();

        }catch (SQLException e){
            System.out.println("Connection error to PostgreSQL server");
            e.printStackTrace();
        }

    }

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

    public static void ownerActions() throws SQLException{
        System.out.println("You have Accessed Owner portal");
        System.out.println("Options:");
        System.out.println("type d to display full inventory");
        System.out.println("type a to add a book");
        System.out.println("type r to remove a book");
        System.out.println("type s to check reports");
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

        //function that creates the address and links it to cart
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
                int salesSum = 0;
                while (result.next()){
                    salesSum = result.getInt("salesSum");
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

    public static void userActions() throws SQLException{
        //displays the books for the user;
        displayBooks();

        System.out.println("Options:");
        System.out.println("type s to search for a book");
        System.out.println("type a to add a book to your cart");
        System.out.println("type r to remove from your cart");
        System.out.println("type v to view your cart");
        System.out.println("type c to checkout");
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


    public static void addToCart() throws SQLException{
        Scanner scanner =  new Scanner(System.in);
        System.out.println("Enter ISBN Book you would like to Add");
        int quantity = 0;
        long book_id = 0;
        int cartID=0;
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
            boolean inCart = false;
            String sql3 = "SELECT * FROM project.cartItem WHERE isbn ='"+book_id+"';";
            Statement statement3 = connection.createStatement();
            ResultSet result3 = statement3.executeQuery(sql3);
            while(result3.next()){
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

            if (inCart){
                String sql4 = "UPDATE project.cartItem SET quantity = quantity + '"+quantity+"' WHERE cart_id = '"+cartID+"'AND isbn = '"+book_id+"';";
                Statement statement4 = connection.createStatement();
                statement4.executeUpdate(sql4);
            }else{
                String sql2 = "INSERT INTO project.cartItem(cart_id, isbn, quantity) VALUES ('"+cartID+"', '"+book_id+"','"+quantity+"');";
                Statement statement2 = connection.createStatement();
                statement2.executeUpdate(sql2);
                System.out.println("Added " + quantity+" copies of :" +title+ " to your cart.");
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


            System.out.println("How many copies would you like?");
            quantity = checkQuantity(scanner,"How many copies would you like?");

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

        System.out.println("Your cart");
        while(result1.next()){
            count++;
            isbn = result1.getLong("isbn");
            quantity = result1.getInt("quantity");
            title = result1.getString("title");
            sell_price = result1.getDouble("sell_price");
            total_per_book = sell_price * quantity;
            total_cart_value+=total_per_book;

            System.out.println("Item "+count+": ISBN: "+isbn+" Title: "+title+" Quantity: "+quantity+" price/book: "+sell_price+" Total price: "+total_per_book);
        }
        System.out.println("Price Before Tax: "+total_cart_value);
        System.out.println("GST: "+ Math.round(total_cart_value*0.13*100.0)/100.0);
        System.out.println("Overall Price: : "+ Math.round(total_cart_value*1.13*100.0)/100.0);


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
    public static void checkout() throws SQLException{
        Scanner scanner = new Scanner(System.in);
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
                System.out.println("\nDo you want to use the same Address created at registration? (yes/no) ");
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

    }

    public static void createOrder() throws SQLException{

    }
}
