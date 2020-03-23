package com.ie23s.java.suicidewarehouseserver.user;

import java.sql.SQLException;
import java.util.Objects;

public class User {
	private int id;
	private String name;
	private String session;

	private float x = 0;
	private float y = 0;
	private int role = 0;
	private int killer = -1;
	private MySQL mySQL;

	public User(int id, String name, String session, float x, float y, int role, int killer, MySQL mySQL) {
		this.id = id;
		this.name = name;
		this.session = session;
		this.x = x;
		this.y = y;
		this.role = role;
		this.killer = killer;
		this.mySQL = mySQL;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public float getX() {
		return x;
	}

	public void setCords(float x, float y) {
		this.x = x;
		this.y = y;
		try {
			mySQL.setCords(id, x, y);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public float getY() {
		return y;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public int getKiller() {
		return killer;
	}

	public void setKiller(int killer) {
		this.killer = killer;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return id == user.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}


}
