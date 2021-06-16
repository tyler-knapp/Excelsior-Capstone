package com.techelevator.projects.view;

import com.techelevator.projects.model.DepartmentDAO;
import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.ProjectDAO;
import com.techelevator.projects.model.jdbc.JDBCProjectDAO;
import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class JDBCProjectDAOIntegrationTest {

    //this variable must be static because we will have to instantiate
    private static SingleConnectionDataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private ProjectDAO projectDAO;
    private long testEmployeeId;

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
    public void setup() {
        projectDAO = new JDBCProjectDAO(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
        String sql = "INSERT INTO employee (first_name, last_name, birth_date, hire_date) VALUES ('testFirst', 'testLast', '20210615', '20210615') RETURNING employee_id";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
        row.next();
        testEmployeeId = row.getLong("employee_id");
    }

    @Test
    public void retrieve_all_active_projects() {
        List<Project> originalList = projectDAO.getAllActiveProjects();
        Project projectOne = getProject(1, "tesName");
        Project projectTwo = getProject(2, "testName2");
        createNewTestProject(projectOne);
        createNewTestProject(projectTwo);

        List<Project> projectsFromDatabase = projectDAO.getAllActiveProjects();

        Assert.assertEquals(originalList.size() + 2, projectsFromDatabase.size());

    }

    @Test
    public void delete_employee_from_project() {
        Project newProject = getProject(1, "testName");
        createNewTestProject(newProject);

        projectDAO.removeEmployeeFromProject(newProject.getId(), (long) 1);

        Assert.assertNotNull(retrieveProjectById(newProject.getId()));
    }

    @Test
    public void add_employee_to_project() {
        Project newProject = getProject(1, "testName");

      projectDAO.addEmployeeToProject(newProject.getId(), (long) 1);

        Assert.assertTrue(newProject.getId() > 0);
        Project projectFromDatabase = retrieveProjectById(newProject.getId());
        Assert.assertEquals(newProject, projectFromDatabase);
    }

    private Project retrieveProjectById(long project_id) {
        Project project = null;
        String sql = "SELECT project_id, name, from_date, to_date FROM project " +
                "WHERE project_id = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, project_id);

        if(row.next()) {
            project = new Project();
            project.setId(row.getLong("project_id"));
            project.setName(row.getString("name"));

            if (row.getDate("from_date") != null) {
                project.setStartDate(row.getDate("from_date").toLocalDate());
            }
            if (row.getDate("to_date") != null) {
                project.setEndDate(row.getDate("to_date").toLocalDate());
            }
        }
        return project;
    }

    private void createNewTestProject(Project newProject) {
        String sql = "INSERT INTO project (project_id, name) VALUES (DEFAULT, ?) RETURNING project_id";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, newProject.getName());
        row.next();
        newProject.setId(row.getLong("project_id"));
    }

    private Project getProject(long projectId, String projectName) {
        Project project = new Project();
        project.setId(projectId);
        project.setName(projectName);
        project.setStartDate(LocalDate.now());
        project.setEndDate(LocalDate.now());
        return project;
    }


}
