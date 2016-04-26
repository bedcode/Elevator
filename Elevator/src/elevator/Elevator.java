package elevator;

import static elevator.Direction.DOWN;
import static elevator.Direction.UP;
import java.util.ArrayList;
import java.util.List;


/**
 * The elevator, including the cabin and the doors.
 * 
 * @author Claudio Cusano
 */
public class Elevator extends Subject implements Runnable {

    static int DOORS_TIME = 1000;  // Milliseconds
    static int TRAVELING_TIME = 1500;  // Milliseconds
    
    ElevatorState state;
    Floor position;
    boolean open;
    boolean terminated;
    int capacity;
    List<Person> passengers;
    
    /**
     * Create the elevator and place it at the given floor.
     */
    public Elevator(Floor startingFloor, int capacity) {
        state = new WaitingState();
        position = startingFloor;
        open = true;
        this.capacity = capacity;
        passengers = new ArrayList<>();
    }

    @Override
    public void run() {
        // Behavior of the elevator.
        terminated = false;
        
        /*=================================================================
         * 
         * --- TO BE COMPLETED ---
         * 
         * The elevator continues to operate until the thread it is not
         * asked to stop by calling the terminate method.
         * 
         * Each action of the elevator is made according to its sate
         * (state pattern).
         * 
         *=================================================================*/
    }
    
    /**
     * Ask the termination of the thread.
     */
    public synchronized void terminate()
    {
        terminated = true;
        notifyAll();
    }
    
    /**
     * For the "state" pattern.
     */
    void setState(ElevatorState state) {
        this.state = state;
    }

    /**
     * Return the current floor.
     * 
     * The elevator is always considered as being at one of the floors, even
     * when traveling between them.
     */
    public Floor getCurrentFloor() {
        return position;
    }
    
    /**
     * Status of the door.
     */
    public boolean isOpen() {
        return open;
    }
    
    /**
     * Return the passengers in the elevator.
     */
    synchronized List<Person> getPassengers() {
        // Return a copy to avoid synchronization issues.
        return new ArrayList<>(passengers);
    }
    
    /**
     * Make the person enter the elevator as soon as it arrives at the floor.
     */
    synchronized public void enter(Person p, Floor f, Direction dir) {
        while (!terminated && (!open || f != position || passengers.size() == capacity)) {
            f.call(dir);
            try {
                wait();
            } catch (InterruptedException ex) {
                System.err.println(ex);
            }
        }
        passengers.add(p);
        p.setLocation(null);
        notifyObservers();
    }

    /**
     * Make the person exit the elevator when it arrives at the floor.
     */
    synchronized public void exit(Person p, Floor f) {           
        /*=================================================================
         * 
         * --- TO BE COMPLETED ---
         * 
         * Called by the person to get off from the elevator at floor f.
         *
         * The person waits until the elevator reaches the desired floor with
         * the doors open.  If these conditions are not met then she pushes 
         * the suitable button of the elevator and waits.
         *
         * When the conditions hold true
         * - the person is removed from the list of passengers;
         * - its location is updated;
         * - the observers are notified.
         *
         * See also the similar "enter" method.
         *
         *=================================================================*/
        while (!terminated && (!open || f != position || (f.stopRequested() == false))) {
            f.requestStop();
            try {
                wait();
            } catch (InterruptedException ex) {
                System.err.println(ex);
            }
        }
        passengers.remove(p);
        p.setLocation(f);
        notifyObservers();
    }
    
    /**
     * Return the number of calls or requests for floors in the given direction.
     */
    int countRequests(Direction direction) {
        Floor f = position.neighbor(direction);
        int count = 0;
        while (f != null) {
            if (f.isCalled(UP) || f.isCalled(DOWN) || f.stopRequested())
                count++;
            f = f.neighbor(direction);
        }
        return count;
    }
    
    /**
     * Tell if the elevator is supposed to stop at the floor, given that it is
     * moving in that direction.
     */
    boolean mustStop(Floor floor, Direction direction) {
        return (floor.isCalled(direction) || floor.stopRequested());
    }
    
    /**
     * Move at the given floor.
     */
    void gotoFloor(Floor floor) {
        synchronized(this) {
            this.position = floor;
            notifyObservers();
            notifyAll();
        }
        try {
            Thread.sleep(TRAVELING_TIME);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
    }
    
    /**
     * Open the doors.
     */
    void openDoors() {
        /*=================================================================
         * 
         * --- TO BE COMPLETED ---
         * 
         * Called by the elevator thread when the doors need to be opened.
         *
         * After setting the "open" attribute, all the waiting threads, and all
         * the observers are notified.
         * 
         * Moreover, the methods "cancelRequest" and "cancelCall" (for both the
         * directions) of the class "Floor" must be called to reset the
         * selections of the floors.
         *
         * At the end the elevator must wait a few seconds to simulate the time
         * taken to close the doors.
         *
         * See also the similar "closeDoors" method.
         *
         *=================================================================*/
    }
    
    /**
     * Close the doors.
     */
    void closeDoors() {
        synchronized(this) {
            open = false;
            notifyObservers();
            notifyAll();
        }
        try {
            Thread.sleep(DOORS_TIME);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
    }
}


/**
 * Interface for the "State" pattern.
 * 
 * @author Claudio Cusano
 */
interface ElevatorState
{
    /// Make a "step" for the simulated elevator.
    void move(Elevator e);
    
    /// Tell if the stop can be requested according to the current state.
    boolean canSetDestination(Elevator e, Floor floor);
}


/**
 * Behavior of the elevator when it is waiting at a floor.
 * 
 * @author Claudio Cusano
 */
class WaitingState implements ElevatorState
{
    @Override
    public void move(Elevator e)
    {
        int neededUp = e.countRequests(UP);
        int neededDown = e.countRequests(DOWN);
                
        if (neededUp > neededDown) {
            e.closeDoors();
            e.setState(new MovingState(UP));            
        } else if (neededDown > 0) {
            e.closeDoors();
            e.setState(new MovingState(DOWN));                        
        } else {
            try {
                Thread.sleep(100);  // !!!
            } catch (InterruptedException ex) {
                System.err.println(ex);
            }
        }
    }
    
    @Override
    public boolean canSetDestination(Elevator e, Floor floor)
    {
        return true;
    }
}

/**
 * Behavior of the elevator when it is moving up or down.
 * 
 * @author Claudio Cusano
 */
class MovingState implements ElevatorState
{
    Direction direction;
    
    MovingState(Direction direction) {
        this.direction = direction;
    }
    
    @Override
    public void move(Elevator e) {
        e.gotoFloor(e.getCurrentFloor().neighbor(direction));
        boolean stop = e.mustStop(e.getCurrentFloor(), direction);
        
        if (stop)
            e.openDoors();

        if (e.countRequests(direction) == 0) {
            e.setState(new WaitingState());
            e.openDoors();
        } else if (stop)
            e.closeDoors();
    }
    
    @Override
    public boolean canSetDestination(Elevator e, Floor floor) {
        return ((direction == UP && floor.isAbove(e.getCurrentFloor())) || 
                (direction == DOWN && floor.isBelow(e.getCurrentFloor())));
    }
}
