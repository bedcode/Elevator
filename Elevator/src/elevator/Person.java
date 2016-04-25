package elevator;

import static elevator.Direction.DOWN;
import static elevator.Direction.UP;
import java.util.Random;



/**
 * A person in the building.
 * 
 * @author Claudio Cusano
 */
public class Person extends Thread {
    
    static final int WAITING_TIME = 5000;  // In milliseconds.
    
    String name;
    Floor location;
    Floor destination = null;
    Elevator elevator;
    boolean terminated;
    Random random = new Random();
    
    /**
     * Create a user of the elevator who is now at the given floor.
     */
    Person(String name, Elevator elevator, Floor floor) {
        super(name + "-thread");
        this.name = name;
        this.location = floor;
        this.elevator = elevator;
    }

    /**
     * The name of the person.
     */
    public String getPersonName() {
        return name;
    }
    
    /**
     * Tell if the person is inside the elevator.
     */
    public boolean isInElevator() {
        return location == null;
    }
    
    /**
     * Return where the person is (null if he is in the elevator).
     */
    public Floor getLocation() {
        return location;
    }
    
    /**
     * Set the new position (null for the elevator).
     */
    public void setLocation(Floor f) {
        location = f;
    }
    
    /**
     * Return the destination floor (null is none).
     */
    public Floor destination() {
        return destination;
    }
    
    @Override
    public void run() {
        // Behavior of the person.
        terminated = false;
        try {
            while (!terminated) {
                Thread.sleep(random.nextInt(2*WAITING_TIME));
                destination = chooseDestination();
                movePerson();
                destination = null;
            }
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
    }

    /**
     * Ask the termination of the thread.
     */
    public void terminate() {
        terminated = true;
    }

    /**
     * Choose a random floor.
     */
    private Floor chooseDestination() {
        int n = random.nextInt(30) + 1;
        Direction dir = UP;
        Floor f = location;
        
        while (n > 0) {
            Floor neigh = f.neighbor(dir);
            if (neigh == null) {
                if (dir == UP)
                    dir = DOWN;
                else
                    dir = UP;
            } else {
                f = neigh;
                n--;
            }
        }
        return f;
    }
    
    /**
     * Move the person to its destination.
     */
    private void movePerson() throws InterruptedException {        
        if (destination == null || destination == location)
            return;
        
        /*=================================================================
         * 
         * --- TO BE COMPLETED ---
         * 
         * Called by the run "method" to move the person to his destination
         * floor.
         *
         * The person will
         * - call the elevator (for the right direction);
         * - enter the elevator, when it arrives at the floor (see the
         *   "elevator.enter" method);
         * - request the stop at the destination floor;
         * - exit from the elevator when he arrives to the floor (see the
         *   the "elevator.exit" method).
         *
         *=================================================================*/
    }
}
