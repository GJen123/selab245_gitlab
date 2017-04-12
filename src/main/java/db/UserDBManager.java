package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.gitlab.api.models.GitlabUser;

import data.User;

public class UserDBManager {

	private static UserDBManager DB_MANAGER = new UserDBManager();

	public static UserDBManager getInstance() {
		return DB_MANAGER;
	}

	private IDatabase database = new MySqlDatabase();

	private UserDBManager() {

	}
	
	public void addUser(GitlabUser user){
		Connection conn = database.getConnection();
		PreparedStatement preStmt = null;
		Statement stmt = null;
		String sql = "INSERT INTO Student(gitLabId, stuId, name, password, email)  VALUES(?, ?, ?, ?, ?)";
		String query = "SELECT * FROM Student";
		
		try{
			preStmt = conn.prepareStatement(sql);
			preStmt.setInt(1, user.getId());
			preStmt.setString(2, user.getUsername());
			preStmt.setString(3, user.getName());
			preStmt.setString(4, user.getUsername());
			preStmt.setString(5, user.getEmail());
			preStmt.executeUpdate();
			preStmt.close();
			
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			System.out.println("List All Students");
			while(rs.next()){
				System.out.println("GitLabId: " + rs.getString("gitLabId") + ", StuId: " + rs.getString("stuId")
				+ ", Name: " + rs.getString("name") + ", Email: " + rs.getString("email"));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<User> listAllUsers() {
		List<User> lsUsers = new ArrayList<User>();

		Connection conn = database.getConnection();
		String sql = "SELECT * FROM Student";
		Statement stmt = null;

		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String gitLabId = rs.getString("gitLabId");
				String stuId = rs.getString("stuId");
				String name = rs.getString("name");
				String password = rs.getString("password");
				String email = rs.getString("email");

				User user = new User();
				user.setGitLabId(gitLabId);
				user.setUserName(stuId);
				user.setName(name);
				user.setPassword(password);
				user.setEmail(email);
				lsUsers.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lsUsers;
	}
}
