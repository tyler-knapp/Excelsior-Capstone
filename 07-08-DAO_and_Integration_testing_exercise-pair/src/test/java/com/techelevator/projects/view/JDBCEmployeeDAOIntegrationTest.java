package com.techelevator.projects.view;

import com.techelevator.projects.model.*;
import com.techelevator.projects.model.jdbc.JDBCDepartmentDAO;
import com.techelevator.projects.model.jdbc.JDBCEmployeeDAO;
import com.techelevator.projects.model.jdbc.JDBCProjectDAO;
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
    private ProjectDAO projectDAO;

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
        projectDAO = new JDBCProjectDAO(dataSource);
    }

    @Test
    public void retrieve_all_employees(){
        //Because we added department_id to getEmployee, we now need to set the department_id, by first creating
        //it in our Department
        Department department = getDepartment("TestDepartment");
        createNewTestDepartment(department);

        List<Employee> originalList = employeeDAO.getAllEmployees();
        Employee employeeOne = getEmployeeByDepartment("testFirst", "testLast");
        //Setting department_id for employeeOne
        employeeOne.setDepartmentId(department.getId());
        Employee employeeTwo = getEmployeeByDepartment("testFirstTwo", "tesLastTwo");
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

        //Employee employee = getEmployee("testFirst", "testLast");

        //employeeDAO.getEmployeesByDepartmentId(department.getId());

        Employee employee = getEmployeeByDepartment("testFirst", "testLast");


        employeeDAO.getEmployeesByDepartmentId(department.getId());

        //employeeDAO.getEmployeesByDepartmentId(department.getId());
        
        //Need to set the department_id in Employee
        employee.setDepartmentId(department.getId());
        createNewTestEmployee(employee);

        //Test
        List<Employee> employeesByDepartment = employeeDAO.getEmployeesByDepartmentId(employee.getDepartmentId());

        Assert.assertNotNull("Employee by department was null", employeesByDepartment);
        //Need to utilize .get() on employeesByDepartment to return the employee and not the list itself
        Assert.assertEquals("Employee by department not equal", employee, employeesByDepartment.get(0));
    }

    @Test
    public void retrieve_employees_without_projects() {
        Employee employee = getEmployeeByProject("testFirst" , "testLast");
        createNewTestEmployeeForProject(employee);

        List<Employee> employeesWithoutProjects = employeeDAO.getEmployeesWithoutProjects();

        Assert.assertTrue(isEmployeeInList(employee, employeesWithoutProjects));
    }

    @Test
    public void retrieve_employee_by_project_id(){
        Employee employee = getEmployeeByProject("testFirst" , "testLast");
        createNewTestEmployeeForProject(employee);

        Project project = getProject("TestProject");
        createNewTestProject(project);
        projectDAO.addEmployeeToProject(project.getId(), employee.getId());
        //Test
        List<Employee> employeesOnProject = employeeDAO.getEmployeesByProjectId(project.getId());

        Assert.assertTrue(isEmployeeInList(employee, employeesOnProject));
    }

    @Test
    public void update_employee_department(){
//        Department newDepartment = getDepartment("TestDepartmentOld");
//        createNewTestDepartment(newDepartment);
//
//        newDepartment.setId(-1L);

        Employee employee = getEmployeeByDepartmentTWO("testFirst", "testLast");
        //Need to set the department_id in Employee
       // employee.setDepartmentId(newDepartment.getId());
        createNewTestEmployeeForProject(employee);
        employee.setId(-1L);
        employee.setDepartmentId(1L);
        employeeDAO.getEmployeesByDepartmentId(employee.getId());

        //TEST
        employeeDAO.changeEmployeeDepartment(employee.getId(), employee.getDepartmentId());

        //Assert.assertNotEquals();
        Assert.assertEquals("Department did not change", 1,  employee.getDepartmentId());

    }

    private Boolean isEmployeeInList(Employee employee, List<Employee> employees) {
        for(Employee employeeToCheck: employees) {
            if (employeeToCheck.equals(employee)) {
                return true;
            }
        }
        return false;
    }

    private Employee getEmployeeByDepartment(String firstName, String lastName) {
        Employee employee = new Employee();
        //employee.setId(employeeId);
        employee.setDepartmentId(-1L);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setBirthDay(LocalDate.of(2021,1, 1));
        employee.setHireDate(LocalDate.of(2021, 1, 1));
        return employee;
    }

    private Employee getEmployeeByDepartmentTWO(String firstName, String lastName) {
        Employee employee = new Employee();
        //employee.setId(employeeId);
        //employee.setDepartmentId(departmentId);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setBirthDay(LocalDate.of(2021,1, 1));
        employee.setHireDate(LocalDate.of(2021, 1, 1));
        return employee;
    }

    private Employee getEmployeeByProject(String firstName, String lastName) {
        Employee employee = new Employee();
        //employee.setId(employeeId);
        //employee.setDepartmentId(-1L);
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

    private void createNewTestEmployeeForProject(Employee newEmployee) {
        String sql = "INSERT INTO employee ( first_name, last_name, birth_date, hire_date) VALUES ( ?, ?, ?, ?) RETURNING employee_id";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, newEmployee.getFirstName(), newEmployee.getLastName(), newEmployee.getBirthDay(), newEmployee.getHireDate());
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

    private void createNewTestProject(Project newProject) {
        String sql = "INSERT INTO project (project_id, name) VALUES (DEFAULT, ?) RETURNING project_id";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, newProject.getName());
        row.next();
        newProject.setId(row.getLong("project_id"));
    }

    private Project getProject(String projectName) {
        Project project = new Project();
        //project.setId(projectId);
        project.setName(projectName);
        project.setStartDate(LocalDate.now());
        project.setEndDate(LocalDate.now());
        return project;
    }
}

