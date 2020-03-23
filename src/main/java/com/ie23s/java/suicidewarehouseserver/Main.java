package com.ie23s.java.suicidewarehouseserver;

import com.ie23s.java.suicidewarehouseserver.io.Server;
import com.ie23s.java.suicidewarehouseserver.user.MySQL;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
	private static MySQL mySQL;

	public static void main(String[] args) throws IllegalAccessException, InstantiationException, ClassNotFoundException, SQLException {
		mySQL = new MySQL("localhost", "suicidewarehouse", "suicidewarehouse", "SequrityPassword");
		mySQL.connect();
		Server server = new Server();
		try {
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
//        int id = mySQL.authUser("User2", "Hash");
//        if(id != -1) {
//            User user = mySQL.getUser(id);
//            System.out.println(user.getSession());
//        }
//        System.out.println(id);
//        System.out.println(mySQL.getUser(id).getName());
//        System.out.println(mySQL.hasUser("User2"));
//        System.out.println(mySQL.hasUser("User-1"));
//        System.out.println(mySQL.authUser("User23", "Hash"));
//        System.out.println(mySQL.authUser("User2", "H2ash"));
//        System.out.println();
//        System.out.println(mySQL.checkUser(-1, "SessionID"));
//        System.out.println(mySQL.checkUser(9, "dssfds"));
//        System.out.println(mySQL.checkUser(9, "SessionID"));


	}

	public static MySQL getMySQL() {
		return mySQL;
	}
}
