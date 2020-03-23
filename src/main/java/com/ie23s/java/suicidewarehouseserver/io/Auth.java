package com.ie23s.java.suicidewarehouseserver.io;

import com.ie23s.java.suicidewarehouseserver.Main;
import com.ie23s.java.suicidewarehouseserver.user.MySQL;
import com.ie23s.java.suicidewarehouseserver.user.User;

import java.sql.SQLException;

public class Auth {
	private ConnectionUtil connectionUtil;
	private User user;
	private int id = -1;
	//private String session = null;

	public Auth(ConnectionUtil connectionUtil) {
		this.connectionUtil = connectionUtil;
	}

	int runAuth(byte[] bytesdata) throws SQLException {
		//Chars section is a command 0 - auth with session, 1 - auth with password, 2 - register;

		String[] data = new String(bytesdata).split(":", 3);

		switch (Integer.parseInt(data[0])) {
			case 0:

				if (Main.getMySQL().checkUser(Integer.parseInt(data[1]), data[2])) {
					user = Main.getMySQL().getUser(Integer.parseInt(data[1]));
					return 210;                                                                        //\S Session
				}
				return 220;                                                                            //\E Session

			case 1:
				id = Main.getMySQL().authUser(data[1], data[2]);

				if (id != -1) {
					user = Main.getMySQL().getUser(id);
					return 211;                                                        //\S NEED SESSION CODE
				}
				return 221;                                                            //\E Login or password incorrect;
			case 2:

				if (!registerCheckLogin(data[1]))
					return 223;                                        //Login incorrect

				if (Main.getMySQL().hasUser(data[1]))
					return 222;                                        //Login've been created

				id = Main.getMySQL().createUser(data[1], data[2]);
				user = Main.getMySQL().getUser(id);
				return 212;                                            //\S Registration OK! Session Written!
		}
		return 299;                                                    //Unknown auth error!
	}

	//TODO LOLLL!
	boolean registerCheckLogin(String login) {
//		if(login.length() < 5 || login.length() > 10)
//			return false;
		return true;
	}

	String prepareData(int code) {
		if (code / 100 != 2)
			return format(229, "Unknown error");                    //incorrect code
		switch (code % 100) {
			case 10:                                                        //Auth with session OK
				return format(code, "OK");
			case 11:
			case 12:
				return format(code, id, user.getSession());
			case 20:
				return format(code, "Session is incorrect");
			case 21:
				return format(code, "Login or password is incorrect");
			case 22:
				return format(code, "Login is unavailable");
			case 23:
				return format(code, "Login is incorrect");
		}
		return format(229, "Unknown error");                        //incorrect code
	}

	String format(int code, String s) {
		return code + ":" + s;
	}

	String format(int code, int id, String s) {
		return code + ":" + id + ":" + s;
	}

	public User getUser() {
		return user;
	}
}
