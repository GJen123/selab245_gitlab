package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import data.Project;

public class ProjectDBManager {

	private static ProjectDBManager DB_MANAGER = new ProjectDBManager();

	public static ProjectDBManager getInstance() {
		return DB_MANAGER;
	}

	private IDatabase database = new MySqlDatabase();

	private ProjectDBManager() {

	}
	
	public void addProject(Project project){
		Connection conn = database.getConnection();
		PreparedStatement preStmt = null;
		Statement stmt = null;
		String sql = "INSERT INTO Assignment(name, description, hasTemplate, type)  VALUES(?, ?, ?, ?)";
		String query = "SELECT * FROM Student";
		
		try{
			preStmt = conn.prepareStatement(sql);
			preStmt.setString(1, project.getName());
			preStmt.setString(2, project.getDescription());
			preStmt.setBoolean(3, project.isHasTemplate());
			preStmt.setString(4, project.getType());
			preStmt.executeUpdate();
			preStmt.close();
			
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			System.out.println("List All Students");
			while(rs.next()){
				System.out.println("Name: " + rs.getString("name") + ", Description: " + rs.getString("description") + 
						", HasTemplate: " + rs.getBoolean("hasTemplate") + ", Type: " + rs.getString("type"));
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
}
