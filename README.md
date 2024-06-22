# Library Management System

this is a link to the loom video explaining the UI and i implemented the OOP principles.
https://www.loom.com/share/cb4831c05dce4c2bb5b7f816f7ab87d5?sid=8405c6d8-74dc-49db-a815-69ceb9d63e38

A Java-based library management system that allows you to manage books, patrons, book issues and book returns.

## Features

- Add, update, delete, and view books.
- Add, update, delete, and view patrons.
- Issue books to patrons and manage issued books.
- Search for books and patrons.
- Return issued books.
- Simple and intuitive GUI using JavaFX.

## Installation

### Prerequisites

- Java Development Kit (JDK) 22
- JavaFX SDK
- An IDE like IntelliJ IDEA or Eclipse (optional but recommended)
- Database setup (e.g., MySQL)

### Steps

1. **Clone the repository**

    ```sh
    git clone https://github.com/Abynahisblue/library-management-system.git
    cd library-management-system
    ```

2. **Set up the database**

    - Create a database named `library_db`.
    - Run the SQL scripts provided in the `db` folder to set up the required tables.

3. **Configure the database connection**

   Update the database connection details in the `DB.java` file:

    ```java
    public class DB {
        private static final String URL = "jdbc:mysql://localhost:3306/library_db";
        private static final String USER = "your_db_username";
        private static final String PASSWORD = "your_db_password";
        // Other code
    }
    ```

4. **Build and run the project**

    - If using an IDE, import the project and build it.
    - If using the command line, navigate to the project directory and run:

    ```sh
    javac -d bin src/**/*.java
    java -cp bin Main
    ```

## Usage

1. **Run the application**

    - Launch the application by running the `Main` class.
      - The main window will appear with options to manage books, patrons, issued books and return books.

2. **Manage books**

    - Use the "Books" menu to add, update, delete, and view books in the library.

3. **Manage patrons**

    - Use the "Patrons" menu to add, update, delete, and view library patrons.

4. **Issue books**

    - Use the "Issue Books" menu to issue books to patrons. You can view all issued books and manage them.
   
5. **Return books**
    - Use the "Return Books" menu to return books. You can view all returned books.

## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes.
4. Commit your changes (`git commit -m 'Add some feature'`).
5. Push to the branch (`git push origin feature-branch`).
6. Open a pull request.


## Acknowledgements

- JavaFX for the GUI components.
- MySQL for the database.
- Open source libraries and tools used in this project.

