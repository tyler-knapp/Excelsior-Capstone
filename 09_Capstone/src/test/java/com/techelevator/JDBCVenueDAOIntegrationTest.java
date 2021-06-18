package com.techelevator;

import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class JDBCVenueDAOIntegrationTest extends DAOIntegrationTest {

    //this variable must be static because we will have to instantiate
    private static SingleConnectionDataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private VenueDAO venueDAO;

//    //Runs once before all tests are run
//    //Setups and configures the dataSource
//    @BeforeClass
//    public static void setupDataSource() {
//        //Instantiate and configure the dataSource
//        dataSource = new SingleConnectionDataSource();
//        dataSource.setUrl("jdbc:postgresql://localhost:5432/excelsior_venues");
//        dataSource.setUsername("postgres");
//        dataSource.setPassword("postgres1");
//
//        //Set autoCommit to false to create the transaction scope
//        dataSource.setAutoCommit(false);
//    }
//
//    //Runs once after all tests are run
//    //Destroys the dataSource, which disconnects it from the database
//    @AfterClass
//    public static void destroyDataSource() {
//        dataSource.destroy();
//    }
//
//    //Runs after each individual test method is run
//    //Rollback transaction
//    @After
//    //Exception will be thrown to Junit
//    public void rollbackTransaction() throws SQLException {
//        dataSource.getConnection().rollback();
//    }

    @Before
    public void setupBeforeTest() {
        venueDAO = new JDBCVenueDAO(getDataSource());
        jdbcTemplate = new JdbcTemplate(getDataSource());
    }

    @Test
    public void retrieve_all_venues(){
        List<Venue> originalList = venueDAO.getAllVenues();
        Venue venueOne = new Venue();
        //createNewTestVenue(venueOne);

        originalList.add(venueOne);

        //TEST
        List<Venue> venueFromDataBase = venueDAO.getAllVenues();
        //ASSERT
        Assert.assertEquals(originalList.size() , venueFromDataBase.size());

    }

    private void createNewTestVenue(Venue venue){
    String sql = "INSERT INTO venue (name, city_id, description)" +
            "VALUES(?,?,?) RETURNING venue.id";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, venue.getName(), venue.getCity_id(), venue.getDescription());
        rowSet.next();
        venue.setId(rowSet.getLong("id"));
    }

}

