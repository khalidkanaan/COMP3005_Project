import java.sql.*;
import java.util.Scanner;

public class JDBC1 {
    static String jdbcURL = "jdbc:postgresql://localhost:5432/postgres";
    static String username = "postgres";
    static String password = "0795";
    static Connection connection;

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
}
