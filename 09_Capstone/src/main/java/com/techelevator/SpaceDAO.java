package com.techelevator;

import java.util.List;

public interface SpaceDAO {

    public List<Space> getAllSpaces();

    public List<Space> getSpaceById(long id);

    public List<Space> getSpaceByName(String name);

    public List<Space> getSpaceAvailability(long maxOccupancy);
}
