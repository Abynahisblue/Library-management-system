package com.example.lms.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DbConnection {
    private static DbConnection dbConnection;
    private final Connection connection;

    private DbConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Updated the driver class name
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/library?createDatabaseIfNotExist=true&allowMultiQueries=true",
                    "root",
                    "Sandy_@98"
            );
            PreparedStatement pstm = connection.prepareStatement("SHOW TABLES");
            ResultSet resultSet = pstm.executeQuery();
            if (!resultSet.next()) {
                String sql =
                        "CREATE TABLE IF NOT EXISTS `member_detail` (" +
                                "  `id` varchar(11) NOT NULL ," +
                                "  `name` varchar(50) DEFAULT NULL," +
                                "  `address` varchar(50) DEFAULT NULL," +
                                "  `contact` varchar(12) DEFAULT NULL," +
                                "  PRIMARY KEY (`id`)" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;" +
                                "CREATE TABLE IF NOT EXISTS `book_detail` (" +
                                "  `id` varchar(10) NOT NULL," +
                                "  `title` varchar(50) DEFAULT NULL," +
                                "  `author` varchar(50) DEFAULT NULL," +
                                "  `status` varchar(20) DEFAULT NULL," +
                                "  PRIMARY KEY (`id`)" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;" +
                                "CREATE TABLE IF NOT EXISTS `issue_table` (" +
                                "  `issueId` varchar(10) NOT NULL," +
                                "  `date` date DEFAULT NULL," +
                                "  `patronId` varchar(11) DEFAULT NULL," + // Changed to int(11)
                                "  `bookId` varchar(10) DEFAULT NULL," + // Changed to varchar(10)
                                "  PRIMARY KEY (`issueId`)," +
                                "  FOREIGN KEY (`patronId`) REFERENCES `member_detail` (`id`)," +
                                "  FOREIGN KEY (`bookId`) REFERENCES `book_detail` (`id`)" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;" +
                                "CREATE TABLE IF NOT EXISTS `return_detail` (" +
                                "  `id` varchar(11) NOT NULL ," + // Added AUTO_INCREMENT
                                "  `issuedDate` date NOT NULL," +
                                "  `returnedDate` date DEFAULT NULL," +
                                "  `fine` float(10) DEFAULT NULL," +
                                "  `issueId` varchar(10) DEFAULT NULL," + // Changed to varchar(10)
                                "  PRIMARY KEY (`id`)," +
                                "  FOREIGN KEY (`issueId`) REFERENCES `issue_table` (`issueId`)" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

                pstm = connection.prepareStatement(sql);
                pstm.execute();
            }
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    public static DbConnection getInstance() {
        if (dbConnection == null) {
            dbConnection = new DbConnection();
        }
        return dbConnection;
    }

    public Connection getConnection() {
        return connection;
    }
}
