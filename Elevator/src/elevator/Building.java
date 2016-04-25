/**
 * Simulation of building served by an elevator used by multiple people to move
 * among the floors.
 */

package elevator;

import java.util.ArrayList;
import java.util.List;


/**
 * The whole building, including the elevator, the floors and the persons.
 * 
 * @author Claudio Cusano
 */
public class Building {

    Elevator elevator;
    List<Floor> floors;
    List<Person> persons;
    Thread elevatorThread;
    
    /**
     * Create the building.
     * 
     * floorNames is a list of names for the floors (from bottom to top).
     */
    public Building(String[] floorNames) {
        persons = new ArrayList<>();
        
        // Create the floors.
        floors = new ArrayList<>();
        for (int i = 0; i < floorNames.length; i++) {
            floors.add(new Floor(floorNames[i]));
            if (i > 0)
                floors.get(i-1).linkUp(floors.get(i));
        }
        
        // Create and setup the elevator
        elevator = new Elevator(floors.get(0), 2);
        elevatorThread = new Thread(elevator, "Elevator-thread");
    }
    
    /**
     * Insert a new person in the building.
     */
    public void addNewPerson() {
        String name = "U" + (persons.size() + 1);
        Person person = new Person(name, elevator, floors.get(0));
        persons.add(person);
        person.start();
        elevator.notifyObservers();
    }
    
    /**
     * Return the name of the destination of a person.
     * 
     * If the person of the given name has no destination (or if such a person does not exist(
     * the empty string is returned.
     */
    public String getPersonDestination(String personName) {
        for (Person p: persons) {
            if (personName.equals(p.getPersonName())) {
                Floor f = p.destination();
                return (f == null ? "" : f.getName());
            }
        }
        return "";
    }
    
    /**
     * Get the list of names of the floors in the building.
     */
    public List<String> getFloorNames() {
        List<String> ret = new ArrayList<>();
        for (Floor f : floors)
            ret.add(f.getName());
        return ret;
    }
    
    /**
     * Get the name of the current floor of the elevator.
     */
    public String getElevatorFloor() {
        return elevator.getCurrentFloor().getName();
    }
    
    /**
     * Tell if the elevator is open.
     */
    public boolean isElevatorOpen() {
        return elevator.isOpen();
    }
    
    /**
     * Call the floor with the given name.
     */
    public void callFloor(String floorName, Direction dir) {
        for (Floor f : floors)
            if (floorName.equals(f.getName()))
                f.call(dir);
    }
    
    /**
     * Request the stop at the floor with the given name.
     */
    public void requestStop(String floorName) {
        for (Floor f : floors)
            if (floorName.equals(f.getName()))
                f.requestStop();
    }

    /**
     * Retrieve the list of persons in a given floor.
     */
    public List<String> getPersonAtFloorNames(String floorName) {
        List<String> ret = new ArrayList<>();
        for (Person p : persons) {
            Floor loc = p.getLocation();
            if (loc != null && floorName.equals(loc.getName()))
                ret.add(p.getPersonName());
        }
        return ret;
    }
    
    /**
     * Names of the passengers in the elevator.
     */
    public List<String> getPersonsInElevatorNames() {
        List<String> ret = new ArrayList<>();
        for (Person p : elevator.getPassengers())
            ret.add(p.getPersonName());
        return ret;
    }
    
    /**
     * Add a new observer to the elevator.
     */
    public void addElevatorObserver(ElevatorObserver observer) {
        elevator.addObserver(observer);
    }
    
    /**
     * Start the simulation.
     */
    public void startSimulation() {
        elevatorThread.start();
        for (Person p : persons)
            p.start();
    }
    
    /**
     * Stop the simulation.
     */
    public void stopSimulation() {
        for (Person p : persons)
            p.terminate();
        for (Person p : persons) {
            try {
                p.join();
            } catch (InterruptedException ex) {
                System.err.println(ex);
            }
        }
        elevator.terminate();
        try {
            elevatorThread.join();
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
    }
}
