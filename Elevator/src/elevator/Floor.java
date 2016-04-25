package elevator;

/**
 * Model a floor of the building, including the buttons for requesting the stop
 * and for calling the elevator.
 * 
 * @author Claudio Cusano
 */
public class Floor {
   String name;
   Floor next;
   Floor prev;
   
   boolean calledForUP;
   boolean calledForDOWN;
   boolean stopReq;
   
   /** Create a new floor with the given name. */
   public Floor(String name) {
       this.name = name;
   }
   
   /** Name of the floor. */
   public String getName() {
       return name;
   }
   
   /** Next floor in the given direction, or null. */
   public Floor neighbor(Direction d) {
       if (d == Direction.UP)
           return next;
       else
           return prev;
   }
   
   /** Ask the elevator to move and stop at the floor. */
   void requestStop() {
       stopReq = true;
   }
   
   /** Cancel a previous request of stop. */
   void cancelRequest() {          
       stopReq = false;
   }
   
   /** Tell if the stop has been requested for this floor. */
   public boolean stopRequested() {
       return stopReq;
   }
   
   /** Call the elevator at the floor for a given direction. */
   void call(Direction d) {
       if (d == Direction.UP)
           calledForUP = true;
       else
           calledForDOWN = true;
   }

   /** Cancel the call for the direction. */
   void cancelCall(Direction d) {
       if (d == Direction.UP)
           calledForUP = false;
       else
           calledForDOWN = false;
   }
      
   /** Tell if the elevator has been called for the direction. */
   public boolean isCalled(Direction d) {
       if (d == Direction.UP)
           return calledForUP;
       else
           return calledForDOWN;
   }
   
   /** Connect to another floor. */
   void linkUp(Floor f) {
       this.next = f;
       f.prev = this;
   }
   
   /** Verify if the floor is above the given one. */
   public boolean isAbove(Floor f) {
       if (f == this)
           return false;
       while (f != null) {
           f = f.next;
           if (f == this)
               return true;
       }
       return false;
   }

   /** Verify if the floor is below the given one. */
   public boolean isBelow(Floor f) {
       return f.isAbove(this);
   }   
}
