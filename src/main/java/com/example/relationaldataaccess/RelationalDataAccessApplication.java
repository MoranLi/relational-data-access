package com.example.relationaldataaccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class RelationalDataAccessApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(RelationalDataAccessApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(RelationalDataAccessApplication.class, args);
	}

	@Autowired
	JdbcTemplate template = new JdbcTemplate(new DataSource() {
		@Override
		public Connection getConnection() throws SQLException {
			return null;
		}

		@Override
		public Connection getConnection(String username, String password) throws SQLException {
			return null;
		}

		@Override
		public PrintWriter getLogWriter() throws SQLException {
			return null;
		}

		@Override
		public void setLogWriter(PrintWriter out) throws SQLException {

		}

		@Override
		public void setLoginTimeout(int seconds) throws SQLException {

		}

		@Override
		public int getLoginTimeout() throws SQLException {
			return 0;
		}

		@Override
		public <T> T unwrap(Class<T> iface) throws SQLException {
			return null;
		}

		@Override
		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return false;
		}

		@Override
		public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
			return null;
		}
	});

	@Override
	public void run(String... strings) throws Exception {
		log.info("Create tables");
		template.execute("DROP TABLE consumer IF EXISTS;");
		template.execute("CREATE TABLE consumer (id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255));");

		List<Object []> splitedNames = Arrays.asList("John Wang", "James Li", "Carter Mao").stream()
				.map(name -> name.split(" ")).collect(Collectors.toList());

		splitedNames.forEach(name ->
				log.info(String.format("Insert consumer record for %s %s", name[0], name[1])));

		template.batchUpdate("INSERT INTO consumer(first_name, last_name) VALUES (?,?)", splitedNames);

		log.info("Find consumer whose first name is Carter");

		template.query("SELECT * FROM consumer WHERE first_name = ?", new Object[]{"Carter"},
				(rs,rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"),
						rs.getString("last_name"))).forEach(customer -> {log.info(customer.toString());});


	}


}
