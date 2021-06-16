package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.techelevator.projects.model.Department;
import org.springframework.jdbc.core.JdbcTemplate;

import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.ProjectDAO;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDBCProjectDAO implements ProjectDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCProjectDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Project> getAllActiveProjects() {
		String sql = "SELECT project_id, name, from_date, to_date from project";

		SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
		List<Project> projects = new ArrayList<Project>();

		while (rows.next()) {
			Project project = mapRowToProject(rows);
			projects.add(project);
		}
		return projects;
	}

	@Override
	public void removeEmployeeFromProject(Long projectId, Long employeeId) {
		String sql = "DELETE FROM project_employee WHERE project_id = ? AND employee_id = ?";
		//had to swap to correct order (we originally add employeeId and then projectId
		jdbcTemplate.update(sql, projectId, employeeId);
		
	}

	@Override
	public void addEmployeeToProject(Long projectId, Long employeeId) {
		String sql = "INSERT INTO project_employee (project_id, employee_id) " +
				"VALUES (?, ?) RETURNING project_id";
		SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, projectId, employeeId );
		rows.next();

	}

	private Project mapRowToProject(SqlRowSet row) {
		Project project = new Project();

		project.setId(row.getLong("project_id"));
		project.setName(row.getString("name"));

		if (row.getDate("from_date") != null) {
			project.setStartDate(row.getDate("from_date").toLocalDate());
		}

		if (row.getDate("to_date") != null) {
			project.setEndDate(row.getDate("to_date").toLocalDate());
		}

		return project;
	}


}
