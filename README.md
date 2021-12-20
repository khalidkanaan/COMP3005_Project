# COMP3005_Project

Description:

The following project is a book store application implelemented using postgres database. Functions such as searching for a book query the database in order to 
fulfill the user's desires. The application has two interfaces, a User and Owner interface. User's must register or login to gain access to the User portal.
Owners must login using the special Owner email and pass (Mail: admin@admin.com, Pass:123). A run down of the different functions is below.

Run Instructions:

- Open the project using a java IDE

- In the main function, there are two strings used for login and password to postgres. Edit the values of the string to match the localhost's login information.

- The database should be automatically added if you are running for the first time. If this does not work, copy paste the DDL then the Querires and finally the Functions files 
into postgres using command prompt.


Option Rundown:

The project will be run using a text based simulation of the book store. When first running the project, the user will be asked if it is the first time they are running the project. If so, then the sql files in the package are imported and all contents executed. If the program has already run and the Yes is chosen, then any alterations made by previous users(registration, orders, etc…) will be overwritten and a fresh bookstore is created. If No is chosen, the data altered previously is accessed.


the user will be prompted to either register, log-in if their information is already registered or to simply ask for additional information.


Entering r will begin the registration process(Note that the Owner’s info is already registered). The User will then be prompted to enter their personal information. The information is then inserted into the database.


If previously registered, The user can simply login using their email and password.


After completing an option, the choice of going back to the menu or back is given. Choosing the menu option brings the user back to their designated portal (User/Owner). Choosing the back  option restarts the option picked. For example, choosing back after search would search again


USER:
After registration or login is complete, a Cart and UserAddress is linked to the user. The User is shown a list of all books available at the library and also given access to the User Portal. The User Portal gives the user the option of searching for a specific book, adding a book to their cart, removing a book from their cart, and viewing the items in their cart, view their order history, track any orders, or to checkout their current order.


Now that the User has access to the user portal. Let's go through the options they have.



Search Demo. After selecting the Search option, the user can choose to search using the ISBN of  a book, it’s Title, Author, or Genre. Title will be shown in this demo. If the user cannot fully remember the title of the book they are looking for, that should not pose problems. Titles similar to the search are queried in the book database and outputted to the User. The ILIKE logical operator was used to implement the search option. ILIKE ignores case sensitivity in the pattern dictated by the user, and when used in conjunction with a % symbol which represents a set of characters, we can search for all the tuples containing the String pattern in the title, author name, or genre of all books. For search by ISBN, the full ISBN must be entered to perform the search.


Add Demo. Selecting the Add option prompts the user to enter the ISBN of the book they want. Thankfully the menu has the Information of every book in the library, so Users do not have to memorize the ISBN. After entering the ISBN, the system will output the matching title of the book and ask the user how many copies they would like. The books are added to the cart.
If the ISBN is of a book already in the User’s cart, the quantity of copies is simply incremented.
The maximum number of copies that you can add to cart for any specific book depends on its stock. If a user enters a number of copies that exceeds the inventory/stock value of a particular book, then the number of books in the cart is set to the total amount in the inventory.


Remove Demo. Selecting the Remove option prompts the user to enter the ISBN of the book they want to remove from their cart. First, the book is searched for in the cart, if the book is not in the cart, the system will print out a message signifying so. Remove works in two ways when the desired book is found. The user is prompted to enter the amount of copies they want removed, if the amount of copies is equal or greater than the amount of said book in the cart, the book is removed entirely from the cart with no trace. However, if the number of copies to be removed is lower than the amount of copies in the cart, the number of copies is simply deducted.


View Cart. This option uses the cart_id associated with the current user_id to query the list of all the books in the cart. Additional information such as the price before tax of all the books, the GST at 13%, as well as the overall price including the GST are calculated and outputted in the console.


Checkout Demo. The User confirms their desire to checkout. The User is asked if they want to use the same shipping address as their billing address. If all is confirmed, the order is created, the tracking number is outputted, and the user cart is cleared. The function uuid_generate_v4() is then queried to generate a universally unique identifier to be inserted as the order_id associated with the order. To be able to use this function the extension for “uuid-ossp” needs to be created. This is done automatically in the functions.sql file. 


View order history Demo. Once an order has been made, the order information is stored in the database. View order history outputs all past order information and arranges them in DESCending order based on their date of purchase, this outputs the most recent order first in the list for the user.


Track Order Demo. After making an order, The User can track their order. The order number, and tracking number are shown as well as the date the order. The carrier and shipping address is outputted.  





OWNER:

To access the owner portal, the user must login using the special admin login. The user table contains a boolean ‘isOwner’ signifies the user’s ownership status. The project checks if this at login and sends the user to the owner portal. The following picture also shows what happens if invalid information is entered.


The Owner functions in a similar way to the user. The options are given out and the owner picks their desired one.


Display Full Demo. This option is rather simple. It outputs the total number of unique books, and then outputs each book.


Add Book Demo. Adding a book works similarly to adding a book as a user. The book's information is entered based on the prompt. The publisher information is also needed. If the publisher already exists within the database, it will be linked to it. Otherwise, the publisher is added to the publisher database. Based on the stock amount entered and publisher price, the expenditure of adding the book is added to the owner. 


Remove Book Demo. Is used when a book must be shelved. The book’s ISBN is needed. The book is then removed from the database.


Check Reports Demo. When the reports option is chosen, the owner can choose whether they want to view the sales by genre, by author or the total sales vs expenditure. Here the Total Revenue report is chosen. If you can remember from the User demo, the user bought a book  totaling 17.39. The expenditure is the total stock of all the books multiplied by the publisher fee. We assumed that the publisher fee is the cost of the books straight from the publisher. 

