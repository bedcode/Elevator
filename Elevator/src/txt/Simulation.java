package txt;

import elevator.Building;
import elevator.ElevatorObserver;
import java.io.PrintStream;
import java.util.List;

/**
 * Class that starts a simulation.
 * 
 * A the trace of the simulation is printed.
 * 
 * @author Claudio Cusano
 */
public class Simulation implements ElevatorObserver {
    
    Building building;
    PrintStream out;
    
    /**
     * Create the simulation.
     */
    Simulation(PrintStream out, String[] floorNames) {
        this.out = out;
        building = new Building(floorNames);
        building.addElevatorObserver(this);
    }
    
    /**
     * Run the simulation for a set duration with a given number of persons.
     * 
     * The duration is given in seconds.
     */
    void runSimulation(int secs, int nPersons) {
        // Start the simulation.
        building.startSimulation();
        out.println("Simulation started.");
        for (int i = 0; i < nPersons; i++)
            building.addNewPerson();
        
        // Wait for the duration of the simulation.        
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException ignored) {
            System.err.println(ignored);
        }
             
        // Stop the simulation.
        out.println("Closing...");
        building.stopSimulation();            
        out.println("Simulation finished.");
    }

    /**
     * Print the information about a list of persons.
     */
    void printPersons(String prefix, List<String> names) {
        if (names.size() == 0)
            return;
        out.print(prefix);
        boolean first = true;
        for (String name : names) {
            if (first)
                first = false;
            else
                out.print(", ");
            out.print(name);
            String dst = building.getPersonDestination(name);
            if (dst.length() > 0)
                out.print("->" + dst);
        }
        out.println();
    }
    
    @Override
    public void elevatorMoved() {
        out.print("Elevator at floor " + building.getElevatorFloor());
        out.println(" with doord " + (building.isElevatorOpen() ? "open" : "closed"));
        printPersons("  passengers: ", building.getPersonsInElevatorNames());
        for (String f : building.getFloorNames())
            printPersons("  " + f + ": ", building.getPersonAtFloorNames(f));
        out.println();
    }
        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final int SIMULATION_TIME = 60;  // Duration of the simulation in seconds
        final int N_PERSONS = 10;       // Number of persons in the building
        final String[] FLOOR_NAMES = { "T", "P1", "P2", "P3", "P4" };

        Simulation sim = new Simulation(System.out, FLOOR_NAMES);
        sim.runSimulation(SIMULATION_TIME, N_PERSONS);
    }
}
