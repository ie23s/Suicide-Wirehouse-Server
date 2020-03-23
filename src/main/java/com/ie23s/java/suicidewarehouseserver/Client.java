package com.ie23s.java.suicidewarehouseserver;

import com.ie23s.java.suicidewarehouseserver.client.ConnectionUtil;

public class Client {

	public static void main(String[] args) {

		new Thread(() -> System.out.println("dfsfsdfds" + new ConnectionUtil().openConnection())).start();
	}

}