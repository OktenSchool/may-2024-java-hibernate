package org.okten.may2024;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class JdbcDemo {

    // Java DataBase Connectivity (JDBC)
    // JDBC determines how to work with database via jdbcUrl
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/XXX";
        String username = "postgres";
        String password = "postgres";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connected to DB: " + connection.isValid(5));

            createPersonsTable(connection);

            demo1(connection);

            demo2(connection);
        } catch (SQLException e) {
            System.out.println("Unable to work with database: " + e.getMessage());
        }
    }

    public static void demo2(Connection connection) {
        System.out.println("Person 2: " + getPersonById(connection, 2));

        System.out.println("Person 5: " + getPersonById(connection, 5));
    }

    public static void demo1(Connection connection) {
        // Statement & PreparedStatement difference:

        // Statement
        createPersonWithStatement(connection, new Person(2, "Mike"));
        // demonstrates SQL Injection
        createPersonWithStatement(connection, new Person(3, "test'); DELETE FROM persons WHERE '' = ('"));

        // PreparedStatement
        createPersonWithPreparedStatement(connection, new Person(4, "Arthur"));
        // demonstrates protection from SQL Injection
        createPersonWithPreparedStatement(connection, new Person(5, "test'); DELETE FROM persons WHERE '' = ('"));
    }

    @SneakyThrows
    public static void createPersonsTable(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS persons (id INT, name VARCHAR(255))");

            System.out.println("Persons table was created if not exists already");
        }
    }

    @SneakyThrows
    public static void createPersonWithStatement(Connection connection, Person person) {
        try (Statement statement = connection.createStatement()) {
            String query = "INSERT INTO persons (id, name) VALUES (" + person.getId() + ", '" + person.getName() + "')";
            System.out.println("Executing the following query: " + query);
            statement.execute(query);

            System.out.println("Person " + person + " was created");
        }
    }

    @SneakyThrows
    public static void createPersonWithPreparedStatement(Connection connection, Person person) {
        String query = "INSERT INTO persons (id, name) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, person.getId());
            preparedStatement.setString(2, person.getName());
            preparedStatement.execute();
            System.out.println("Person " + person + " was created with PreparedStatement");
        }
    }

    @SneakyThrows
    public static Optional<Person> getPersonById(Connection connection, Integer id) {
        String query = "SELECT * FROM persons WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int personId = resultSet.getInt("id");
                String personName = resultSet.getString("name");
                return Optional.of(new Person(personId, personName));
            }
        }

        return Optional.empty();
    }
}
