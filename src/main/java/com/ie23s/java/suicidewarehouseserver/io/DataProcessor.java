package com.ie23s.java.suicidewarehouseserver.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DataProcessor {
	private ConnectionUtil connectionUtil;
	private Auth auth;

	public DataProcessor(ConnectionUtil connectionUtil, Auth auth) {
		this.connectionUtil = connectionUtil;
		this.auth = auth;
	}

	//Processor of queries...
	public void runProcessor() throws IOException {

		for(;;) {
			String[] data = new String(connectionUtil.getData(), StandardCharsets.UTF_8).split(":");
			switch (Integer.parseInt(data[0])) {
				case 0:
						getCords(data[1]);						//getting cords
					break;
				case 1:
					System.out.println(1);
					break;
			}
		}
	}

	private void getCords(String data) {
		String[] cord = data.split(":");

		auth.getUser().setCords(Float.parseFloat(cord[0]), Float.parseFloat(cord[1]));
		connectionUtil.getServer().getConnection(auth.getUser().getKiller()).sendData(("0:" + data).getBytes());
	}
}
