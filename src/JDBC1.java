import java.sql.*;
import java.util.Scanner;

public class JDBC1 {
    static String jdbcURL = "jdbc:postgresql://localhost:5432/postgres";
    static String username = "postgres";
    static String password = "0795";
    static Connection connection;

    private static String Userid;



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
        String usern = scanner.next();

//        boolean idTaken = true;
//        String temp;
//
//        while (idTaken) {
//            temp = scanner.next();
//
//            String sql1 = "SELECT * FROM user1 WHERE user_id = '"+temp+"';";
//            Statement statement = connection.createStatement();
//            ResultSet resultSet1 = statement.executeQuery(sql1);
//
//            if(resultSet1.next()){
//                System.out.println("This username is already in use!");
//                System.out.println("Enter your username: ");
//                    //System.out.println("Welcome "+resultSet1.getString("user_id"));
//            }
//
//                if(!temp.contains("@")){
//
//                }
//                usern = scanner.next();
//
//            }else{
//                System.out.println("This username is already in use!");
//                System.out.println("Enter your username: ");
//                scanner.next();
//                continue;
//            }
//            idTaken = false;
//        }

        System.out.println("Enter your email: ");
        String email = scanner.next();
        System.out.println("Enter your password: ");
        String password = scanner.next();
        System.out.println("Enter your first name: ");
        String firstn = scanner.next();
        System.out.println("Enter your last name: ");
        String lastn = scanner.next();
        System.out.println("Enter your phone number: ");

        long phonenum = 0;
        boolean notnum = true;
        while (notnum) {
            if (scanner.hasNextLong())
                phonenum = scanner.nextLong();
            else {
                System.out.println("Please enter a 10 digit number!");
                System.out.println("Enter your phone number: ");
                scanner.next();
                continue;
            }
            notnum = false;
        }

        String sql2 = "INSERT INTO project.user(user_id, email, password, first_name, last_name, phone_number) VALUES" + " ('"+usern+"', '"+email+"', '"+password+"', '"+firstn+"', '"+lastn+"', '"+phonenum+"');";

        Statement statement1 = connection.createStatement();
        statement1.executeUpdate(sql2);
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

        Userid = result.getString("user_id");
        sql = "Select * FROM project.user WHERE email = '"+email+"' AND password ='"+password+"'AND isOwner='"+true+"';";
        statement = connection.createStatement();
        result = statement.executeQuery(sql);

        if (result.next()){
            ownerActions();
        }else{
            userActions();
        }

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
        System.out.println("Inventory:");
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

    }

    public static void addBook() throws SQLException{
        Scanner scanner = new Scanner(System.in);
//        System.out.println("Enter publisher name: ");
//        String pubName = scanner.next();

        System.out.println("Enter book ISBN: ");
        String isbn = scanner.next();

        String sql = "SELECT count(*) AS book_in_library FROM project.book WHERE isbn = '"+isbn+"';";
        Statement findIsbn = connection.createStatement();
        ResultSet matchBook = findIsbn.executeQuery(sql);

        int inLibrary = 0;
        while(matchBook.next()){
            inLibrary = Integer.parseInt(matchBook.getString("book_in_library"));
        }
        if(inLibrary == 1){
            System.out.println("Book found in library");
            System.out.println("Enter the quantity of this books that you would like to add: ");
            int stock_increase = checkQuantity(scanner, "Enter the quantity of this books that you would like to add: ");

            String sql1 = "UPDATE project.book SET inventory = inventory + '"+stock_increase+"' WHERE isbn = '"+isbn+"';";

            Statement increaseStock = connection.createStatement();
            increaseStock.executeUpdate(sql1);

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
        }
    }

    public static void linkPublisher(String isbn) throws SQLException {
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
            inPublisher = Integer.parseInt(matchPublisher.getString("publisher_exist"));
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
            address_id = Integer.parseInt(matchAddress.getString("address_id"));
        }

        //Adds the link between publisher and publisher address
        String sql3 = "INSERT INTO project.publisherAddress(publisher_name, address_id) values " +
                " ('"+publisher_name+"', '"+address_id+"');";

        Statement linkPublisherToBook = connection.createStatement();
        linkPublisherToBook.executeUpdate(sql3);

        System.out.println("Publisher has been added to the system");
        System.out.println("Book is now linked to publisher");
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
                System.out.println("Please enter a 10 digit number!");
                System.out.println("Enter your phone number: ");
                scanner.next();
                continue;
            }
            notnum = false;
        }
        return phonenum;
    }

    public static void removeBook() throws SQLException{

    }
    public static void salesCheck() throws SQLException{

    }

    public static void userActions() throws SQLException{
        System.out.println("Welcome to Look Inna Book");
        System.out.println("Options:");
        System.out.println("type s to search for a book");
        System.out.println("type a to add a book to your cart");
        System.out.println("type r to remove from your cart");
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
                scanner.nextLine();
                String scan = scanner.nextLine();
                long ISBN = Long.parseLong(scan);

                String sql = "Select * FROM project.book WHERE ISBN ='%"+ISBN+"%';";
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(sql);
                while (result.next()){
                    System.out.println("Search Results: ");
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
                while (result.next()){
                    System.out.println("Search Results: ");
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
                while (result.next()){
                    System.out.println("Search Results: ");
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
                while (result.next()){
                    System.out.println("Search Results: ");
                    System.out.println("ISBN: "+ result.getLong("ISBN") + " Title: " + result.getString("Title") + " Genre: "+result.getString("genre") + " Author: " + result.getString("author_firstn")+" "+result.getString("author_lastn") + " Costs: "+ result.getInt("sell_price"));

                }
            }

        }

    }


    public static void addToCart() throws SQLException{


    }
    public static void removeCart() throws SQLException{

    }
    public static void checkout() throws SQLException{

    }
}
