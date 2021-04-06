package me.infinity.utils.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;

public class SQLUtils {
	private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	/* Думаю не стоит это здесь оставлять не будет стринг клианера */
	private static final String DB_IP = "whyuleet.ru:3306";
	private static final String DB_NAME = "u1247083_whyuleet";
	private static final String DB_USER = "...";
	private static final String DB_PASSWORD = "...";
	
	private static final String CONNECTION_STRING = "jdbc:mysql://" + DB_IP + "/" + DB_NAME;
	
	public static DBUser getUserByNameFromDB(String name) {
		DBUser dbUser = null;
		try {
			Class.forName(DRIVER);
			Connection con = DriverManager.getConnection(CONNECTION_STRING, DB_USER, DB_PASSWORD);
			
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM `GDN_User` WHERE `Name` = \"" + name + "\"");
			
			if (rs.next())
				dbUser = new DBUser(rs);
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return dbUser;
	}
	
	public static DBUser getUserByEmailFromDB(String email) {
		DBUser dbUser = null;
		try {
			Class.forName(DRIVER);
			Connection con = DriverManager.getConnection(CONNECTION_STRING, DB_USER, DB_PASSWORD);
			
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM `GDN_User` WHERE `Email` = \"" + email + "\"");
			
			if (rs.next())
				dbUser = new DBUser(rs);
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return dbUser;
	}
	
	public static LinkedList<DBUser> getUsersFromDB() {
		LinkedList<DBUser> users = new LinkedList<DBUser>();
		try {
			Class.forName(DRIVER);
			Connection con = DriverManager.getConnection(CONNECTION_STRING, DB_USER, DB_PASSWORD);
			
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM `GDN_User`");
			
			while (rs.next()) {
				DBUser user = new DBUser(rs);
				if(!user.isDeleted())
					users.add(user);
			}
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return users;
	}
	
	
}
