package com.bankzecure.webapp.repository;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.bankzecure.webapp.entity.*;
import com.bankzecure.webapp.JdbcUtils;

public class CustomerRepository {
	private final static String DB_URL = "jdbc:mysql://localhost:3306/springboot_bankzecure?serverTimezone=GMT";
	private final static String DB_USERNAME = "bankzecure";
	private final static String DB_PASSWORD = "Ultr4B4nk@L0nd0n";

	public Customer findByIdentifierAndPassword(final String identifier, final String password) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			statement = connection.prepareStatement("SELECT * FROM customer WHERE identifier = ? AND password = ?");
			statement.setString(1, identifier);
			statement.setString(2, password);

			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				Long id = resultSet.getLong("id");
				String firstName = resultSet.getString("first_name");
				String lastName = resultSet.getString("last_name");
				String email = resultSet.getString("email");
				return new Customer(id, identifier, firstName, lastName, email);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(resultSet);
			JdbcUtils.closeStatement(statement);
			JdbcUtils.closeConnection(connection);
		}
		return null;
	}

	public Customer update(String identifier, String newEmail, String newPassword) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			// Connection and statement
			connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

			// Don't set the password in the update query, if it's not provided
			if (newPassword != "") {
				statement = connection.prepareStatement("UPDATE customer SET email = ?, password = ? WHERE identifier = ?");
				statement.setString(1, newEmail);
				statement.setString(2, newPassword);
				statement.setString(3, identifier);
			} else {
				statement = connection.prepareStatement("UPDATE customer SET email = ? WHERE identifier = ?");
				statement.setString(1, newEmail);
				statement.setString(2, identifier);
			}

			if (statement.executeUpdate() != 1) {
				throw new SQLException("failed to update data");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeStatement(statement);
			JdbcUtils.closeConnection(connection);
		}

		try {
			connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			statement = connection.prepareStatement("SELECT * FROM customer WHERE identifier = ?");
			statement.setString(1, identifier);
			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				Long id = resultSet.getLong("id");
				String firstName = resultSet.getString("first_name");
				String lastName = resultSet.getString("last_name");
				String email = resultSet.getString("email");
				return new Customer(id, identifier, firstName, lastName, email);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(resultSet);
			JdbcUtils.closeStatement(statement);
			JdbcUtils.closeConnection(connection);
		}
		return null;
	}
}