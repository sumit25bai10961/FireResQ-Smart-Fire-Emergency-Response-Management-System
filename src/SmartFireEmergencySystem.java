import java.util.ArrayList;
import java.util.Scanner;
import java.util.Date;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;

//JDBC
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

//custom exceptions
class InvalidEmergencyException extends Exception {
    public InvalidEmergencyException(String message) { super(message); }
}
class NoResourceAvailableException extends Exception {
    public NoResourceAvailableException(String message) { super(message); }
}
class InvalidInputException extends Exception {
    public InvalidInputException(String message) { super(message); }
}

//enums
enum EmergencyStatus { REPORTED, ASSIGNED, DISPATCHED, ON_SCENE, RESOLVED }
enum FireSeverity { LOW, MEDIUM, HIGH, CRITICAL }

//abstraction and encapsulation
abstract class Emergency {
    private String emergencyId, location, fireType;
    private FireSeverity severity;
    private int peopleAffected;
    private EmergencyStatus status;
    private Date reportedTime;

    public Emergency(String emergencyId, String location, String fireType, FireSeverity severity, int peopleAffected) {
        this.emergencyId = emergencyId; this.location = location; this.fireType = fireType;
        this.severity = severity; this.peopleAffected = peopleAffected;
        this.status = EmergencyStatus.REPORTED; this.reportedTime = new Date();
    }

    public int calculatePriorityScore() {
        int score = 0;
        if (severity == FireSeverity.CRITICAL) score += 40;
        else if (severity == FireSeverity.HIGH) score += 30;
        else if (severity == FireSeverity.MEDIUM) score += 20;
        else if (severity == FireSeverity.LOW) score += 10;
        
        if (peopleAffected >= 50) score += 30;
        else if (peopleAffected >= 10) score += 20;
        else if (peopleAffected > 0) score += 10;
        
        if (fireType.equalsIgnoreCase("Chemical")) score += 20;
        return score;
    }

    public abstract void displayEmergencyDetails();

    public String getEmergencyId() { return emergencyId; }
    public String getLocation() { return location; }
    public String getFireType() { return fireType; }
    public FireSeverity getSeverity() { return severity; }
    public int getPeopleAffected() { return peopleAffected; }
    public EmergencyStatus getStatus() { return status; }
    public void setStatus(EmergencyStatus status) { this.status = status; }
    public Date getReportedTime() { return reportedTime; }
}

class FireEmergency extends Emergency {
    public FireEmergency(String emergencyId, String location, String fireType, FireSeverity severity, int peopleAffected) {
        super(emergencyId, location, fireType, severity, peopleAffected);
    }
    @Override
    public void displayEmergencyDetails() {
        System.out.println("ID: " + getEmergencyId() + " | Loc: " + getLocation() + " | Type: " + getFireType());
        System.out.println("Severity: " + getSeverity() + " | Priority Score: " + calculatePriorityScore());
        System.out.println("Status: " + getStatus());
    }
}

//other oop
class FireStation {
    private String stationId, stationName, location;
    public FireStation(String stationId, String stationName, String location) {
        this.stationId = stationId; this.stationName = stationName; this.location = location;
    }
    public String getStationId() { return stationId; }
    public String getStationName() { return stationName; }
    public String getLocation() { return location; }
}

class FireTruck {
    private String truckId, truckType, stationId;
    private boolean available;
    public FireTruck(String truckId, String truckType, String stationId) {
        this.truckId = truckId; this.truckType = truckType; this.stationId = stationId; this.available = true;
    }
    public String getTruckId() { return truckId; }
    public String getTruckType() { return truckType; }
    public String getStationId() { return stationId; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}

class Firefighter {
    private String firefighterId, name, stationId;
    private boolean available;
    public Firefighter(String firefighterId, String name, String stationId) {
        this.firefighterId = firefighterId; this.name = name; this.stationId = stationId; this.available = true;
    }
    public String getFirefighterId() { return firefighterId; }
    public String getName() { return name; }
    public String getStationId() { return stationId; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}

class Dispatch {
    private String dispatchId, emergencyId, truckId, firefighterId, stationId;
    public Dispatch(String dispatchId, String emergencyId, String truckId, String firefighterId, String stationId) {
        this.dispatchId = dispatchId; this.emergencyId = emergencyId; this.truckId = truckId;
        this.firefighterId = firefighterId; this.stationId = stationId;
    }
    public String getDispatchId() { return dispatchId; }
    public String getEmergencyId() { return emergencyId; }
    public String getTruckId() { return truckId; }
    public String getFirefighterId() { return firefighterId; }
    public String getStationId() { return stationId; }
}

//multithreading
class DispatchManager {
    private int dispatchCounter = 1;
    
    public synchronized void dispatchEmergency(ArrayList<Emergency> emergencies, 
                                         ArrayList<FireStation> fireStations, ArrayList<FireTruck> fireTrucks, 
                                         ArrayList<Firefighter> firefighters, ArrayList<Dispatch> dispatches) 
                                         throws InvalidEmergencyException, NoResourceAvailableException {
        
        Emergency bestEmergency = null;
        FireStation bestStation = null;
        FireTruck bestTruck = null;
        Firefighter bestFF = null;

        for (Emergency emg : emergencies) {
            if (emg.getStatus() == EmergencyStatus.REPORTED) {
                
                FireStation tempAssignedStation = null;
                FireTruck tempAssignedTruck = null;
                Firefighter tempAssignedFF = null;

                for (FireStation station : fireStations) {
                    FireTruck tempTruck = null;
                    Firefighter tempFF = null;
                    for (FireTruck truck : fireTrucks) if (truck.getStationId().equalsIgnoreCase(station.getStationId()) && truck.isAvailable()) { tempTruck = truck; break; }
                    for (Firefighter ff : firefighters) if (ff.getStationId().equalsIgnoreCase(station.getStationId()) && ff.isAvailable()) { tempFF = ff; break; }
                    
                    if (tempTruck != null && tempFF != null) {
                        tempAssignedStation = station; tempAssignedTruck = tempTruck; tempAssignedFF = tempFF; break; 
                    }
                }
                
                if (tempAssignedStation != null && tempAssignedTruck != null && tempAssignedFF != null) {
                    if (bestEmergency == null || emg.calculatePriorityScore() > bestEmergency.calculatePriorityScore()) {
                        bestEmergency = emg;
                        bestStation = tempAssignedStation;
                        bestTruck = tempAssignedTruck;
                        bestFF = tempAssignedFF;
                    }
                }
            }
        }
        
        if (bestEmergency == null) {
            boolean hasReported = false;
            for (Emergency e : emergencies) if (e.getStatus() == EmergencyStatus.REPORTED) hasReported = true;
            if (hasReported) throw new NoResourceAvailableException("No suitable fire resources are available.");
            else throw new InvalidEmergencyException("No dispatchable REPORTED emergencies found.");
        }

        bestEmergency.setStatus(EmergencyStatus.ASSIGNED);
        System.out.println(">> Emergency " + bestEmergency.getEmergencyId() + " status updated to ASSIGNED.");
        
        bestTruck.setAvailable(false); bestFF.setAvailable(false);
        
        bestEmergency.setStatus(EmergencyStatus.DISPATCHED);
        System.out.println(">> Resources allocated. Emergency " + bestEmergency.getEmergencyId() + " status updated to DISPATCHED.");
        
        dispatches.add(new Dispatch("DSP-" + dispatchCounter++, bestEmergency.getEmergencyId(), bestTruck.getTruckId(), bestFF.getFirefighterId(), bestStation.getStationId()));
        
        System.out.println("\n========================================");
        System.out.println("EMERGENCY DISPATCH SUCCESSFUL (" + Thread.currentThread().getName() + ")");
        System.out.println("Emergency ID: " + bestEmergency.getEmergencyId());
        System.out.println("Priority Score: " + bestEmergency.calculatePriorityScore());
        System.out.println("Assigned Station: " + bestStation.getStationName());
        System.out.println("Fire Truck: " + bestTruck.getTruckId() + " | Firefighter: " + bestFF.getName());
        System.out.println("========================================\n");
    }
}

class EmergencyProcessingThread extends Thread {
    private DispatchManager dispatchManager;
    private ArrayList<Emergency> emergencies;
    private ArrayList<FireStation> fireStations;
    private ArrayList<FireTruck> fireTrucks;
    private ArrayList<Firefighter> firefighters;
    private ArrayList<Dispatch> dispatches;

    public EmergencyProcessingThread(DispatchManager dm, ArrayList<Emergency> e, ArrayList<FireStation> fs, ArrayList<FireTruck> ft, ArrayList<Firefighter> ff, ArrayList<Dispatch> d) {
        this.dispatchManager = dm; this.emergencies = e; this.fireStations = fs; this.fireTrucks = ft; this.firefighters = ff; this.dispatches = d;
    }

    @Override
    public void run() {
        System.out.println("Simulation thread " + Thread.currentThread().getName() + " started.");
        try {
            Thread.sleep(500);
            dispatchManager.dispatchEmergency(emergencies, fireStations, fireTrucks, firefighters, dispatches);
        } catch (Exception e) {
            System.out.println(Thread.currentThread().getName() + " ERROR: " + e.getMessage());
        }
    }
}

class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/fire_emergency_db";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    public DatabaseManager() { createTableIfNotExists(); }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS emergencies (id VARCHAR(50) PRIMARY KEY, location VARCHAR(100), fire_type VARCHAR(50), severity VARCHAR(20), status VARCHAR(20))";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("connected to database");
        } catch (SQLException e) {
            System.out.println(">> Database initialization failed.");
            System.out.println(">> Error: " + e.getMessage());
        }
    }

    public void saveEmergencyToDatabase(Emergency emg) {
        String sql = "INSERT INTO emergencies (id, location, fire_type, severity, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, emg.getEmergencyId()); pstmt.setString(2, emg.getLocation());
            pstmt.setString(3, emg.getFireType()); pstmt.setString(4, emg.getSeverity().toString());
            pstmt.setString(5, emg.getStatus().toString());
            pstmt.executeUpdate();
            System.out.println(">> Database: Emergency " + emg.getEmergencyId() + " saved successfully.");
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) System.out.println(">> Database Error: Emergency " + emg.getEmergencyId() + " already exists in the database.");
            else System.out.println(">> DB Error: Database might not be running. Details: " + e.getMessage());
        }
    }

    public void viewEmergenciesFromDatabase() {
        String sql = "SELECT * FROM emergencies";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- DATABASE INCIDENTS ---");
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                System.out.println("DB Record -> ID: " + rs.getString("id") + " | Loc: " + rs.getString("location") + " | Severity: " + rs.getString("severity") + " | Status: " + rs.getString("status"));
            }
            if (!hasData) System.out.println("No records found in database.");
            System.out.println("--------------------------");
        } catch (SQLException e) { System.out.println(">> DB Error: Database might not be running."); }
    }

    public void updateEmergencyStatusInDatabase(String id, String newStatus) {
        String sql = "UPDATE emergencies SET status = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setString(1, newStatus); pstmt.setString(2, id);
             int rows = pstmt.executeUpdate();
             if (rows > 0) System.out.println(">> Database: Status updated successfully. (Note: Local memory remains unchanged)");
             else System.out.println(">> Database: No matching Emergency ID found.");
        } catch (SQLException e) { System.out.println(">> DB Error: Database might not be running."); }
    }
}


//main code
public class SmartFireEmergencySystem {
    private static int emergencyCounter = 1;

    public static String readNonEmptyString(Scanner scanner, String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) break;
            System.out.println(">> Input cannot be empty. Please try again.");
        }
        return input;
    }



    public static int readNonNegativeInt(Scanner scanner, String prompt) throws InvalidInputException {
        System.out.print(prompt);
        if (!scanner.hasNextInt()) { scanner.next(); throw new InvalidInputException("Expected a numeric value, but received text."); }
        int val = scanner.nextInt(); scanner.nextLine(); 
        if (val < 0) throw new InvalidInputException("Number cannot be negative.");
        return val;
    }

    public static String readFireType(Scanner scanner) {
        while (true) {
            System.out.println("\nSelect Fire Type:");
            System.out.println("1. Electrical");
            System.out.println("2. Chemical");
            System.out.println("3. Residential");
            System.out.println("4. Industrial");
            System.out.println("5. Vehicle");
            System.out.println("6. Other");
            System.out.print("\nEnter choice: ");
            
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                switch (choice) {
                    case 1: return "Electrical";
                    case 2: return "Chemical";
                    case 3: return "Residential";
                    case 4: return "Industrial";
                    case 5: return "Vehicle";
                    case 6: return "Other";
                    default: System.out.println("Invalid choice. Please select a number from 1 to 6.");
                }
            } else {
                scanner.nextLine(); // consume invalid input
                System.out.println("Invalid choice. Please select a number from 1 to 6.");
            }
        }
    }

    public static FireSeverity readFireSeverity(Scanner scanner) {
        while (true) {
            System.out.println("\nSelect Fire Severity:");
            System.out.println("1. Low");
            System.out.println("2. Medium");
            System.out.println("3. High");
            System.out.println("4. Critical");
            System.out.print("\nEnter choice: ");
            
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                switch (choice) {
                    case 1: return FireSeverity.LOW;
                    case 2: return FireSeverity.MEDIUM;
                    case 3: return FireSeverity.HIGH;
                    case 4: return FireSeverity.CRITICAL;
                    default: System.out.println("Invalid choice. Please select a number from 1 to 4.");
                }
            } else {
                scanner.nextLine(); // consume invalid input
                System.out.println("Invalid choice. Please select a number from 1 to 4.");
            }
        }
    }
    public static boolean stationExists(ArrayList<FireStation> fireStations, String id) {
        for (FireStation s : fireStations) if (s.getStationId().equalsIgnoreCase(id)) return true;
        return false;
    }

    public static boolean truckExists(ArrayList<FireTruck> trucks, String id) {
        for (FireTruck t : trucks) if (t.getTruckId().equalsIgnoreCase(id)) return true;
        return false;
    }

    public static boolean firefighterExists(ArrayList<Firefighter> ffs, String id) {
        for (Firefighter f : ffs) if (f.getFirefighterId().equalsIgnoreCase(id)) return true;
        return false;
    }
    //file I/O
    public static void saveIncidentToFile(ArrayList<Emergency> emergencies) {
        File file = new File("incident_history.txt");
        try (FileWriter fw = new FileWriter(file, false);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("========================================\nEMERGENCY INCIDENT HISTORY\n========================================\n");
            if (emergencies.isEmpty()) bw.write("No incidents to save.\n");
            else for (Emergency emg : emergencies) bw.write("ID: " + emg.getEmergencyId() + " | Status: " + emg.getStatus() + " | Loc: " + emg.getLocation() + " | Severity: " + emg.getSeverity() + "\n");
            System.out.println(">> Success: Incident history saved to 'incident_history.txt'");
        } catch (IOException e) { System.out.println(">> FILE ERROR: Failed to save file. " + e.getMessage()); }
    }

    public static void readIncidentHistory() {
        File file = new File("incident_history.txt");
        if (!file.exists()) { System.out.println(">> No saved incident history found. Please 'Save' first."); return; }
        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) {
            System.out.println("\n--- READING FROM incident_history.txt ---");
            String line;
            while ((line = br.readLine()) != null) System.out.println(line);
            System.out.println("-----------------------------------------");
        } catch (IOException e) { System.out.println(">> FILE ERROR: Failed to read file. " + e.getMessage()); }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        ArrayList<Emergency> emergencies = new ArrayList<>();
        ArrayList<FireStation> fireStations = new ArrayList<>();
        ArrayList<FireTruck> fireTrucks = new ArrayList<>();
        ArrayList<Firefighter> firefighters = new ArrayList<>();
        ArrayList<Dispatch> dispatches = new ArrayList<>();
        
        DispatchManager dispatchManager = new DispatchManager();
        DatabaseManager dbManager = new DatabaseManager();
        
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n========================================");
            System.out.println("SMART FIRE & EMERGENCY RESPONSE SYSTEM");
            System.out.println("========================================");
            System.out.println("1. Register Emergency");
            System.out.println("2. View Emergencies");
            System.out.println("3. Manage Fire Stations");
            System.out.println("4. Manage Fire Trucks");
            System.out.println("5. Manage Firefighters");
            System.out.println("6. Dispatch Highest Priority Emergency");
            System.out.println("7. Update Emergency Status");
            System.out.println("8. View Incident History");
            System.out.println("9. Generate System Report");
            System.out.println("10. Save Incident History");
            System.out.println("11. View Saved Incident History");
            System.out.println("12. Database Operations");
            System.out.println("13. Simulate Multiple Emergency Calls");
            System.out.println("14. Exit");
            
            try {
                int choice = readNonNegativeInt(scanner, "\nEnter your choice: ");
                System.out.println();

                switch (choice) {
                    case 1:
                        System.out.println("--- Register Emergency ---");
                        String loc = readNonEmptyString(scanner, "\nEnter Emergency Location: ");
                        String type = readFireType(scanner);
                        int ppl = readNonNegativeInt(scanner, "\nEnter Number of People Affected: ");
                        FireSeverity sev = readFireSeverity(scanner);
                        
                        String emgId = "EMG-" + emergencyCounter++;
                        emergencies.add(new FireEmergency(emgId, loc, type, sev, ppl)); 
                        System.out.println("\nEmergency registered successfully.");
                        System.out.println("Emergency ID: " + emgId);
                        break;
                        
                    case 2:
                        System.out.println("--- View All Emergencies ---");
                        if (emergencies.isEmpty()) System.out.println("No emergencies reported yet.");
                        else for (Emergency emg : emergencies) { System.out.println("--------------------"); emg.displayEmergencyDetails(); }
                        break;
                        
                    case 3: 
                        System.out.println("--- Manage Fire Stations ---");
                        System.out.println("1. Add Fire Station\n2. View Fire Stations");
                        int stChoice = readNonNegativeInt(scanner, "Choice: ");
                        if (stChoice == 1) {
                            String id = readNonEmptyString(scanner, "Station ID: ");
                            if (stationExists(fireStations, id)) {
                                System.out.println(">> Station ID " + id + " already exists. Please use a different ID.");
                            } else {
                                String n = readNonEmptyString(scanner, "Name: ");
                                String l = readNonEmptyString(scanner, "Location: ");
                                fireStations.add(new FireStation(id, n, l)); 
                                System.out.println("Station added!");
                            }
                        } else if (stChoice == 2) {
                            if (fireStations.isEmpty()) System.out.println("No fire stations available. Please add a fire station first.");
                            else for (FireStation fs : fireStations) System.out.println("ID: " + fs.getStationId() + " | Name: " + fs.getStationName() + " | Loc: " + fs.getLocation());
                        } else throw new InvalidInputException("Invalid choice.");
                        break;

                    case 4: 
                        System.out.println("--- Manage Fire Trucks ---");
                        System.out.println("1. Add Fire Truck\n2. View Fire Trucks");
                        int trChoice = readNonNegativeInt(scanner, "Choice: ");
                        if (trChoice == 1) {
                            String id = readNonEmptyString(scanner, "Truck ID: ");
                            if (truckExists(fireTrucks, id)) {
                                System.out.println(">> Truck ID " + id + " already exists.");
                            } else {
                                String sid = readNonEmptyString(scanner, "Station ID: ");
                                if (!stationExists(fireStations, sid)) {
                                    System.out.println(">> Station " + sid + " does not exist. Please add the station first.");
                                } else {
                                    String t = readNonEmptyString(scanner, "Type: ");
                                    fireTrucks.add(new FireTruck(id, t, sid)); 
                                    System.out.println("Truck added!");
                                }
                            }
                        } else if (trChoice == 2) {
                            if (fireTrucks.isEmpty()) System.out.println("No fire trucks available. Please add a fire truck first.");
                            else for (FireTruck ft : fireTrucks) System.out.println("ID: " + ft.getTruckId() + " | Station: " + ft.getStationId() + " | Avail: " + ft.isAvailable());
                        } else throw new InvalidInputException("Invalid choice.");
                        break;

                    case 5: 
                        System.out.println("--- Manage Firefighters ---");
                        System.out.println("1. Add Firefighter\n2. View Firefighters");
                        int ffChoice = readNonNegativeInt(scanner, "Choice: ");
                        if (ffChoice == 1) {
                            String id = readNonEmptyString(scanner, "Firefighter ID: ");
                            if (firefighterExists(firefighters, id)) {
                                System.out.println(">> Firefighter ID " + id + " already exists.");
                            } else {
                                String sid = readNonEmptyString(scanner, "Station ID: ");
                                if (!stationExists(fireStations, sid)) {
                                    System.out.println(">> Station " + sid + " does not exist. Please add the station first.");
                                } else {
                                    String n = readNonEmptyString(scanner, "Name: ");
                                    firefighters.add(new Firefighter(id, n, sid)); 
                                    System.out.println("Firefighter added!");
                                }
                            }
                        } else if (ffChoice == 2) {
                            if (firefighters.isEmpty()) System.out.println("No firefighters available. Please add a firefighter first.");
                            else for (Firefighter ff : firefighters) System.out.println("ID: " + ff.getFirefighterId() + " | Name: " + ff.getName() + " | Station: " + ff.getStationId() + " | Avail: " + ff.isAvailable());
                        } else throw new InvalidInputException("Invalid choice.");
                        break;
                        
                    case 6:
                        System.out.println("--- Dispatch Highest Priority Emergency ---");
                        dispatchManager.dispatchEmergency(emergencies, fireStations, fireTrucks, firefighters, dispatches);
                        break;
                        
                    case 7:
                        System.out.println("--- Update Emergency Status ---");
                        String upId = readNonEmptyString(scanner, "Enter Emergency ID to update: ");
                        Emergency emgToUp = null;
                        for(Emergency e : emergencies) if(e.getEmergencyId().equalsIgnoreCase(upId)) { emgToUp = e; break; }
                        
                        if(emgToUp == null) throw new InvalidEmergencyException("Emergency not found.");
                        
                        EmergencyStatus curr = emgToUp.getStatus();
                        System.out.println("Current Status: " + curr);
                        
                        EmergencyStatus allowedNext = null;
                        if (curr == EmergencyStatus.REPORTED) allowedNext = EmergencyStatus.ASSIGNED;
                        else if (curr == EmergencyStatus.ASSIGNED) allowedNext = EmergencyStatus.DISPATCHED;
                        else if (curr == EmergencyStatus.DISPATCHED) allowedNext = EmergencyStatus.ON_SCENE;
                        else if (curr == EmergencyStatus.ON_SCENE) allowedNext = EmergencyStatus.RESOLVED;

                        System.out.println("Select New Status:\n1. REPORTED\n2. ASSIGNED\n3. DISPATCHED\n4. ON_SCENE\n5. RESOLVED");
                        int sChoice = readNonNegativeInt(scanner, "Choice: ");
                        
                        EmergencyStatus newStatus = null;
                        if(sChoice == 1) newStatus = EmergencyStatus.REPORTED;
                        else if(sChoice == 2) newStatus = EmergencyStatus.ASSIGNED;
                        else if(sChoice == 3) newStatus = EmergencyStatus.DISPATCHED;
                        else if(sChoice == 4) newStatus = EmergencyStatus.ON_SCENE;
                        else if(sChoice == 5) newStatus = EmergencyStatus.RESOLVED;
                        else throw new InvalidInputException("Invalid status choice.");
                        
                        if (newStatus != allowedNext) {
                            if (allowedNext != null) throw new InvalidInputException("Invalid status transition.\nAllowed next status: " + allowedNext);
                            else throw new InvalidInputException("Invalid status transition. Emergency is already RESOLVED.");
                        }
                        
                        emgToUp.setStatus(newStatus);
                        System.out.println("Status successfully updated to: " + emgToUp.getStatus());
                        
                        if (newStatus == EmergencyStatus.RESOLVED) {
                            for (Dispatch d : dispatches) {
                                if (d.getEmergencyId().equals(emgToUp.getEmergencyId())) {
                                    for (FireTruck ft : fireTrucks) if (ft.getTruckId().equals(d.getTruckId())) ft.setAvailable(true);
                                    for (Firefighter f : firefighters) if (f.getFirefighterId().equals(d.getFirefighterId())) f.setAvailable(true);
                                    System.out.println(">> Resources (Truck " + d.getTruckId() + ", FF " + d.getFirefighterId() + ") have been freed.");
                                    break;
                                }
                            }
                        }
                        break;

                    case 8:
                        System.out.println("--- Incident History ---");
                        if (emergencies.isEmpty()) {
                            System.out.println("No incidents on record.");
                        } else {
                            for (Emergency e : emergencies) System.out.println(e.getReportedTime() + " | ID: " + e.getEmergencyId() + " | Loc: " + e.getLocation() + " | Status: " + e.getStatus());
                        }
                        break;

                    case 9:
                        System.out.println("--- System Report ---");
                        System.out.println("Total Emergencies Registered: " + emergencies.size());
                        int resolved = 0;
                        for (Emergency e : emergencies) if (e.getStatus() == EmergencyStatus.RESOLVED) resolved++;
                        System.out.println("Total Emergencies Resolved: " + resolved);
                        System.out.println("Total Fire Stations: " + fireStations.size());
                        System.out.println("Total Fire Trucks: " + fireTrucks.size());
                        System.out.println("Total Firefighters: " + firefighters.size());
                        break;
                        
                    case 10:
                        System.out.println("--- Saving Incident History ---");
                        saveIncidentToFile(emergencies);
                        break;
                        
                    case 11:
                        readIncidentHistory();
                        break;
                        
                    case 12:
                        System.out.println("--- Database Operations ---");
                        System.out.println("1. Save Emergency to Database\n2. View Emergencies from Database\n3. Update Emergency Status in Database\n4. Back");
                        int dbChoice = readNonNegativeInt(scanner, "Choice: ");
                        if (dbChoice == 1) {
                            String targetId = readNonEmptyString(scanner, "Enter Emergency ID to save to DB: ");
                            Emergency targetE = null;
                            for(Emergency e : emergencies) if(e.getEmergencyId().equalsIgnoreCase(targetId)) { targetE = e; break; }
                            
                            if(targetE == null) System.out.println("Emergency not found in local memory.");
                            else dbManager.saveEmergencyToDatabase(targetE);
                        } else if (dbChoice == 2) {
                            dbManager.viewEmergenciesFromDatabase();
                        } else if (dbChoice == 3) {
                            String uId = readNonEmptyString(scanner, "Enter Emergency ID to update in DB: ");
                            String nStatus = readNonEmptyString(scanner, "Enter new status (e.g., RESOLVED): ").toUpperCase();
                            if (!nStatus.equals("REPORTED") && !nStatus.equals("ASSIGNED") && !nStatus.equals("DISPATCHED") && !nStatus.equals("ON_SCENE") && !nStatus.equals("RESOLVED")) {
                                System.out.println(">> Invalid status. Please enter one of:\nREPORTED, ASSIGNED, DISPATCHED, ON_SCENE, RESOLVED");
                            } else {
                                dbManager.updateEmergencyStatusInDatabase(uId, nStatus);
                            }
                        } else if (dbChoice == 4) {
                            System.out.println("Returning to main menu...");
                        } else {
                            throw new InvalidInputException("Invalid Database Operation choice.");
                        }
                        break;

                    case 13:
                        System.out.println("--- Simulate Multiple Emergency Calls ---");
                        if (fireStations.isEmpty()) {
                            System.out.println("Auto-generating simulation resources...");
                            fireStations.add(new FireStation("ST-SIM", "Sim Station", "Sim City"));
                            fireTrucks.add(new FireTruck("TRK-SIM1", "Pumper", "ST-SIM"));
                            fireTrucks.add(new FireTruck("TRK-SIM2", "Ladder", "ST-SIM"));
                            firefighters.add(new Firefighter("FF-SIM1", "Alice", "ST-SIM"));
                            firefighters.add(new Firefighter("FF-SIM2", "Bob", "ST-SIM"));
                        }
                        String simId1 = "EMG-" + emergencyCounter++; String simId2 = "EMG-" + emergencyCounter++;
                        emergencies.add(new FireEmergency(simId1, "Sector A", "Electrical", FireSeverity.HIGH, 15));
                        emergencies.add(new FireEmergency(simId2, "Sector B", "Chemical", FireSeverity.CRITICAL, 50));
                        EmergencyProcessingThread t1 = new EmergencyProcessingThread(dispatchManager, emergencies, fireStations, fireTrucks, firefighters, dispatches);
                        EmergencyProcessingThread t2 = new EmergencyProcessingThread(dispatchManager, emergencies, fireStations, fireTrucks, firefighters, dispatches);
                        t1.setName("Thread-1"); t2.setName("Thread-2");
                        t1.start(); t2.start();
                        try { t1.join(); t2.join(); } catch (InterruptedException e) { }
                        break;
                        
                    case 14:
                        System.out.println("Exiting the Smart Fire & Emergency Response System. Goodbye!");
                        isRunning = false;
                        break;
                        
                    default:
                        throw new InvalidInputException("Menu choice must be between 1 and 14.");
                }
            } catch (InvalidInputException e) { System.out.println(">> INPUT ERROR: " + e.getMessage());
            } catch (InvalidEmergencyException e) { System.out.println(">> DISPATCH ERROR: " + e.getMessage());
            } catch (NoResourceAvailableException e) { System.out.println(">> RESOURCE ERROR: " + e.getMessage());
            } catch (Exception e) { System.out.println(">> ERROR: " + e.getMessage()); }
        }
        scanner.close();
    }
}
