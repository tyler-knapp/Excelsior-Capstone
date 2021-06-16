package com.techelevator.projects.view;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.DepartmentDAO;
import com.techelevator.projects.model.jdbc.JDBCDepartmentDAO;
import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.SQLException;
import java.util.List;

public class JDBCDepartmentDAOIntegrationTest {

    //this variable must be static because we will have to instantiate
    private static SingleConnectionDataSource dataSource;
    private JdbcTemplate jdbcTemplate;
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
    public void steupBeforeTest() {
        departmentDAO = new JDBCDepartmentDAO(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    public void retrieve_all_departments() {
        List<Department> originalList = departmentDAO.getAllDepartments();
        Department departmentOne = getDepartment(1, "test");
        Department departmentTwo = getDepartment(2, "test2");
        departmentDAO.createDepartment(departmentOne);
        departmentDAO.createDepartment(departmentTwo);

        List<Department> departmentsFromDatabase = departmentDAO.getAllDepartments();

        Assert.assertEquals(originalList.size() + 2, departmentsFromDatabase.size());
    }

    @Test
    public void retrieve_departments_by_name() {
        String testDepartmentName = "test";
        Department newDepartment = new Department();
        newDepartment.setName(testDepartmentName);
        departmentDAO.createDepartment(newDepartment);

        List<Department> departmentsByName = departmentDAO.searchDepartmentsByName(testDepartmentName);

        Assert.assertNotNull("Department list was null", departmentsByName.get(0));
        Assert.assertEquals("Department not equal", newDepartment.getName(), departmentsByName.get(0).getName());

    }

    @Test
    public void save_department() {
        Department newDepartment = getDepartment(1, "test");
        insertNewTestDepartment(newDepartment);

        newDepartment.setId((long) 2);
        newDepartment.setName("updatedTest");
        departmentDAO.saveDepartment(newDepartment);

        Department departmentFromDatabase = retrieveDepartmentById(newDepartment.getId());

        Assert.assertNotNull("Department not found", departmentFromDatabase);
        Assert.assertEquals("Department not equal", newDepartment, departmentFromDatabase);
    }

    @Test
    public void insert_department() {
        Department newDepartment = getDepartment(1, "testName");

        departmentDAO.createDepartment(newDepartment);

        Assert.assertTrue(newDepartment.getId() > 0);
        Department departmentFromDatabase = departmentDAO.getDepartmentById(newDepartment.getId());
        Assert.assertEquals(newDepartment, departmentFromDatabase);

    }

    @Test
    public void retrieve_department_by_id() {
        Department department = getDepartment(1, "testName");
        departmentDAO.createDepartment(department);

        Department departmentFromDatabase = departmentDAO.getDepartmentById(department.getId());

        Assert.assertNotNull("Department was null", departmentFromDatabase);
        Assert.assertEquals("Department not equal", department, departmentFromDatabase);
    }

    private Department retrieveDepartmentById(long department_id) {
        Department department = null;
        String sql = "SELECT department_id, name FROM department WHERE department_id = ?";
        SqlRowSet row  = jdbcTemplate.queryForRowSet(sql, department_id);

        if (row.next()) {
            department = new Department();
            department.setId(row.getLong("department_id"));
            department.setName(row.getString("name"));
        }
        return department;
    }

    private void insertNewTestDepartment(Department newDepartment) {
        String sql = "INSERT INTO department (department_id, name) VALUES (DEFAULT, ?) RETURNING department_id";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, newDepartment.getName());
        row.next();
        newDepartment.setId(row.getLong("department_id"));
    }

    private Department getDepartment(long departmentId, String departmentName) {
        Department department = new Department();
        department.setId(departmentId);
        department.setName(departmentName);
        return department;
    }
}
