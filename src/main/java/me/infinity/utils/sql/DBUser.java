package me.infinity.utils.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUser {
	private int id;
	private String name;
	private String passwordHash;
	private String hashMethod;
	private String photo;
	private String location;
	private String email;
	private boolean confirmed;
	private boolean verified;
	private boolean banned;
	private boolean deleted;

	public DBUser(ResultSet rs) throws Exception {
		this(rs.getInt("UserID"), rs.getString("Name"), rs.getString("Password"), rs.getString("HashMethod"),
				rs.getString("Photo"), rs.getString("Location"), rs.getString("Email"), rs.getBoolean("Confirmed"),
				rs.getBoolean("Verified"), rs.getBoolean("Banned"), rs.getBoolean("Deleted"));
	}

	public DBUser(int id, String name, String passwordHash, String hashMethod, String photo, String location,
			String email, boolean confirmed, boolean verified, boolean banned, boolean deleted) {
		this.name = name;
		this.passwordHash = passwordHash;
		this.hashMethod = hashMethod;
		this.photo = photo;
		this.location = location;
		this.email = email;
		this.confirmed = confirmed;
		this.verified = verified;
		this.banned = banned;
		this.deleted = deleted;
	}

	public String getEmail() {
		return email;
	}

	public int getId() {
		return id;
	}

	public String getHashMethod() {
		return hashMethod;
	}

	public String getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public String getPhoto() {
		return photo;
	}

	public boolean isBanned() {
		return banned;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public boolean isVerified() {
		return verified;
	}
}