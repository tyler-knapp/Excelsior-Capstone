package com.techelevator.projects.view;

import com.techelevator.projects.model.*;
import com.techelevator.projects.model.jdbc.JDBCDepartmentDAO;
import com.techelevator.projects.model.jdbc.JDBCEmployeeDAO;
import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class JDBCEmployeeDAOIntegrationTest {
    private static SingleConnectionDataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private EmployeeDAO employeeDAO;
    private DepartmentDAO departmentDAO;

    //Runs once before all tests are run
    //Setups and configures the dataSource
    @BeforeClass
    public static void setupDataSource() {
        //Instantiate and configure the dataSource
        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/projects");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");

        //Set autoCommit to false to create the transaction scope
        dataSource.setAutoCommit(false);
    }

    //Runs once after all tests are run
    //Destroys the dataSource, which disconnects it from the database
    @AfterClass
    public static void destroyDataSource() {
        dataSource.destroy();
    }

    //Runs after each individual test method is run
    //Rollback transaction
    @After
    //Exception will be thrown to Junit
    public void rollbackTransaction() throws SQLException {
        dataSource.getConnection().rollback();
    }

    @Before
    public void setup(){
        employeeDAO = new JDBCEmployeeDAO(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
        departmentDAO = new JDBCDepartmentDAO(dataSource);
    }

    @Test
    public void retrieve_all_employees(){
        //Because we added department_id to getEmployee, we now need to set the department_id, by first creating
        //it in our Department
        Department department = getDepartment("TestDepartment");
        createNewTestDepartment(department);

        List<Employee> originalList = employeeDAO.getAllEmployees();
        Employee employeeOne = getEmployee("testFirst", "testLast");
        //Setting department_id for employeeOne
        employeeOne.setDepartmentId(department.getId());
        Employee employeeTwo = getEmployee("testFirstTwo", "tesLastTwo");
        //Setting department_id for employeeTwo
        employeeTwo.setDepartmentId(department.getId());
        createNewTestEmployee(employeeOne);
        createNewTestEmployee(employeeTwo);

        List<Employee> employeesFromDatabase = employeeDAO.getAllEmployees();

        Assert.assertEquals(originalList.size() +2, employeesFromDatabase.size());
    }

    @Test
    public void retrieve_employees_by_name(){
        String testEmployeeFirstName = "testFirst";
        String testEmployeeLastName = "testLast";
        //Because we added department_id to getEmployee, we now need to set the department_id, by first creating
        //it in our Department
        Department department = getDepartment("TestDepartment");
        createNewTestDepartment(department);

        Employee newEmployee = new Employee();
        //Setting department_id for newEmployee
        newEmployee.setDepartmentId(department.getId());
        newEmployee.setFirstName(testEmployeeFirstName);
        newEmployee.setLastName(testEmployeeLastName);
        newEmployee.setBirthDay(LocalDate.of(2021,1,1));
        newEmployee.setHireDate(LocalDate.of(2021,1,1));

        createNewTestEmployee(newEmployee);

        List<Employee> employeeByName = employeeDAO.searchEmployeesByName(testEmployeeFirstName, testEmployeeLastName);

        Assert.assertNotNull("Employee  first name was null", employeeByName.get(0));
        Assert.assertNotNull("Employee last name was null", employeeByName.get(0));
        Assert.assertEquals("First name not equal", newEmployee.getFirstName(), employeeByName.get(0).getFirstName());
        Assert.assertEquals("Last name not equal", newEmployee.getLastName(), employeeByName.get(0).getLastName());
    }

    @Test
    public void retrieve_employee_by_department_id(){
        //Employee employee = getEmployee("testFirst", "testLast");
        //createNewTestEmployee(employee);
        Department department = getDepartment("TestDepartment");
        createNewTestDepartment(department);

        employeeDAO.getEmployeesByDepartmentId(department.getId());

        Employee employee = getEmployee("testFirst", "testLast");
        //Need to set the department_id in Employee
        employee.setDepartmentId(department.getId());
        createNewTestEmployee(employee);

        List<Employee> employeesByDepartment = employeeDAO.getEmployeesByDepartmentId(employee.getDepartmentId());

        Assert.assertNotNull("Employee by department was null", employeesByDepartment);
        //Need to utilize .get() on employeesByDepartment to return the employee and not the list itself
        Assert.assertEquals("Employee by department not equal", employee, employeesByDepartment.get(0));
    }

    private Employee getEmployee(String firstName, String lastName) {
        Employee employee = new Employee();
        //employee.setId(employeeId);
        employee.setDepartmentId(-1L);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setBirthDay(LocalDate.of(2021,1, 1));
        employee.setHireDate(LocalDate.of(2021, 1, 1));
        return employee;
    }

    private void createNewTestEmployee(Employee newEmployee) {
        String sql = "INSERT INTO employee (department_id, first_name, last_name, birth_date, hire_date) VALUES (?, ?, ?, ?, ?) RETURNING employee_id";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, newEmployee.getDepartmentId(), newEmployee.getFirstName(), newEmployee.getLastName(), newEmployee.getBirthDay(), newEmployee.getHireDate());
        row.next();
        newEmployee.setId(row.getLong("employee_id"));
    }

    private Department getDepartment(String departmentName) {
        Department department = new Department();
        department.setName(departmentName);
        return department;
    }

    private void createNewTestDepartment(Department newDepartment) {
        String sql = "INSERT INTO department (department_id, name) VALUES (DEFAULT, ?) RETURNING department_id";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, newDepartment.getName());
        row.next();
        newDepartment.setId(row.getLong("department_id"));
    }

}

