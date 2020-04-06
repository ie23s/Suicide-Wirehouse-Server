package com.ie23s.java.suicidewarehouseserver.user;

import com.ie23s.java.suicidewarehouseserver.utils.MySQLUtil;
import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL extends MySQLUtil {
	SecureRandom secureRandom;

	{
		secureRandom = new SecureRandom();
	}

	public MySQL(String host, short port, String database, String user, String password) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		super(host, port, database, user, password);
	}

	public MySQL(String host, String database, String user, String password) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		super(host, (short) 3306, database, user, password);
	}

	private static String encodeSHA256(String data) {
		return encodeSHA256(data.getBytes());
	}

	private static String encodeSHA256(byte[] data) {
		String s = "";
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] hash = messageDigest.digest(data);
			s = new String(Hex.encode(hash)).toUpperCase();
		} catch (NoSuchAlgorithmException ignore) {
		}
		return s;
	}

	@Override
	public void init() throws SQLException {
		execute("CREATE TABLE `suicidewarehouse`.`members` ( `id` INT(11) NOT NULL AUTO_INCREMENT ," +
				"`name` VARCHAR(16) NOT NULL , `hash` VARCHAR(64) NOT NULL , `session` VARCHAR(64) NOT NULL ," +
				"`role` TINYINT(1) NOT NULL DEFAULT '0', `killer` INT(11) NULL DEFAULT NULL ," +
				"`last_seen` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ," +
				"`x` FLOAT NOT NULL DEFAULT '0' , `y` FLOAT NOT NULL DEFAULT '0' , PRIMARY KEY (`id`)," +
				"UNIQUE (`name`)) ENGINE = InnoDB;");
	}

	public int createUser(String user, String hash) throws SQLException {
		String session = "";
		execute(String.format("INSERT INTO `members`(`name`, `hash`, `session`)" +
						"VALUES ('%s', '%s', '%s')",
				strip(user), encodeSHA256(hash), session));
		ResultSet resultSet =
				executeQuery("SELECT LAST_INSERT_ID() as id");
		resultSet.next();
		return resultSet.getInt("id");

	}

	public boolean hasUser(String user) throws SQLException {
		ResultSet resultSet =
				executeQuery(String.format("SELECT `id` FROM `members` WHERE `name` = '%s'", strip(user)));
		return resultSet.next();
	}

	public int authUser(String name, String hash) throws SQLException {
		int id = -1;
		ResultSet resultSet =
				executeQuery(String.format("SELECT `id`, `hash` FROM `members` WHERE `name` = '%s'", strip(name)));
		if (resultSet.next() && resultSet.getString("hash").equalsIgnoreCase(encodeSHA256(hash))) {
			id = resultSet.getInt("id");
			execute(
					String.format("UPDATE `members` SET `session` = '%s' WHERE `id` = %d",
							randomSHA256(), id)
			);
		}

		return id;
	}

	public boolean checkUser(int id, String session) throws SQLException {
		ResultSet resultSet =
				executeQuery(String.format("SELECT `session` FROM `members` WHERE `id` = '%d'", id));


		return resultSet.next() && resultSet.getString("session").equalsIgnoreCase(session);
	}

	public User getUser(int id) throws SQLException {
		ResultSet resultSet =
				executeQuery(String.format("SELECT * FROM `members` WHERE `id` = '%d'", id));
		resultSet.next();
		return new User(resultSet.getInt("id"), resultSet.getString("name"),
				resultSet.getString("session"), resultSet.getFloat("x"),
				resultSet.getFloat("y"), resultSet.getInt("role"),
				resultSet.getInt("killer"), this);
	}

	public void setCords(int id, float x, float y) throws SQLException {
		execute(String.format("UPDATE `members` " +
						"SET `last_seen` = NOW(), `x` = '%f', `y` = '%f' WHERE `members`.`id` = %d", x,y, id));
	}

	private String randomSHA256() {
		byte[] bytes = new byte[16];
		secureRandom.nextBytes(bytes);
		return encodeSHA256(bytes);
	}
}
