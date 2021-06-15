package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.techelevator.projects.model.Department;
import org.springframework.jdbc.core.JdbcTemplate;

import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.EmployeeDAO;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDBCEmployeeDAO implements EmployeeDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCEmployeeDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Employee> getAllEmployees() {
		String sql = "SELECT employee_id, department_id, first_name, last_name, birth_date, hire_date from employee";
		SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
		List<Employee> employees = new ArrayList<Employee>();
		while(rows.next()){
			Employee employee = mapRowToEmployee(rows);
			employees.add(employee);
		}
		return employees;
	}

	@Override
	public List<Employee> searchEmployeesByName(String firstNameSearch, String lastNameSearch) {
		String sql = "SELECT employee_id, department_id, first_name, last_name, birth_date, hire_date From employee WHERE first_name = ? AND last_name = ?";
		SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, firstNameSearch, lastNameSearch);
		List<Employee> employees = new ArrayList<Employee>();

		while(rows.next()){
			Employee employee = mapRowToEmployee(rows);
			employees.add(employee);
		}
		return employees;
	}

	@Override
	public List<Employee> getEmployeesByDepartmentId(long id) {
		String sql = "SELECT employee_id, department_id, first_name, last_name, birth_date, hire_date From employee WHERE department_id = ?";
		SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
		List<Employee> employees = new ArrayList<Employee>();

		while(rows.next()){
			Employee employee = mapRowToEmployee(rows);
			employees.add(employee);
		}
		return employees;
	}

	@Override
	public List<Employee> getEmployeesWithoutProjects() {
		String sql = "SELECT employee.employee_id, department_id, first_name, last_name, birth_date, hire_date, project_id FROM employee LEFT JOIN project_employee ON employee.employee_id = project_employee.employee_id";
		SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
		List<Employee> employees = new ArrayList<Employee>();

		while(rows.next()){
			Employee employee = mapRowToEmployee(rows);
			if ( rows.getLong("project_id") == 0){
				employees.add(employee);
			}
		}
		return employees;
	}

	@Override
	public List<Employee> getEmployeesByProjectId(Long projectId) {
		String sql = "SELECT employee.employee_id, department_id, first_name, last_name, birth_date, hire_date, project_id FROM employee " +
				"LEFT JOIN project_employee ON employee.employee_id = project_employee.employee_id";
		SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
		List<Employee> employees = new ArrayList<Employee>();

		while(rows.next()){
			Employee employee = mapRowToEmployee(rows);
			if ( rows.getLong("project_id") != 0){
				employees.add(employee);
			}
		}
		return employees;
	}

	@Override
	public void changeEmployeeDepartment(Long employeeId, Long departmentId) {
		String sql = "UPDATE employee SET department_id = ? WHERE employee_id = ?";
		jdbcTemplate.update(sql, departmentId, employeeId);
	}

	private Employee mapRowToEmployee(SqlRowSet row){
		Employee employee = new Employee();

		employee.setId( row.getLong("employee_id"));
		employee.setHireDate( row.getDate("hire_date").toLocalDate());
		employee.setFirstName(row.getString("first_name"));
		employee.setLastName(row.getString("last_name"));
		employee.setBirthDay(row.getDate("birth_date").toLocalDate());

		if (row.getLong("department_id") != 0) {
			employee.setDepartmentId(row.getLong("department_id"));
		}

		return employee;
	}

}
