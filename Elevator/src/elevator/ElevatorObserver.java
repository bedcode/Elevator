package elevator;

/**
 *Abstract interface for the "Observer" pattern.
 * 
 * @author cusano
 */
public interface ElevatorObserver {
    /**
     * Called when something change in the observed elevator.
     */
    void elevatorMoved();
}
