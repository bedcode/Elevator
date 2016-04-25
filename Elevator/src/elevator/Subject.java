package elevator;

import java.util.ArrayList;


/**
 * Base class for the "Observer" pattern.
 * 
 * @author Claudio Cusano
 */
public class Subject {
    ArrayList<ElevatorObserver> observers;

    /** Constructor. */
    public Subject() {
        this.observers = new ArrayList<>();
    }
    
    /** Add a new observer. */
    public void addObserver(ElevatorObserver observer) {
        observers.add(observer);
    }
    
    /** Remove the observer. */
    public void removeObserver(ElevatorObserver observer) {
        observers.remove(observer);
    }
    
    /** Notify all the observers .*/
    public void notifyObservers() {
        for (ElevatorObserver o : observers)
            o.elevatorMoved();
    }
}
