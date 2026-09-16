# FireResQ – Smart Fire & Emergency Response Management System

## 1. Problem Statement

In the event of a fire emergency, a rapid and organized response is critical to minimizing damage and saving lives. When multiple emergencies occur simultaneously, manual processing of incident information, evaluation of urgency, and assignment of available firefighting resources can lead to critical delays and suboptimal resource allocation. Dispatchers often face challenges in quickly matching the most urgent emergencies with the nearest or most available resources.

An effective emergency management system must seamlessly track fire stations, fire trucks, and firefighters to ensure that available resources are assigned correctly. Furthermore, emergencies vary significantly in urgency and must be systematically prioritized using factors such as the severity of the fire, the number of people affected, and specific hazards like chemical fires.

The system must be capable of identifying the highest-priority reported emergency and verifying that a suitable fire truck and firefighter are both available from the exact same fire station before dispatching them. Tracking the ongoing status of an incident through defined lifecycles (such as when resources arrive on the scene or when the emergency is resolved) is necessary to ensure resources are eventually freed for subsequent incidents. Additionally, maintaining comprehensive incident records through local file storage and a persistent MySQL database is vital for post-incident reporting. Finally, real-world emergency dispatching is inherently concurrent; therefore, the system must demonstrate safe handling of simultaneous emergency processing through multithreading and strict synchronization.

## 2. Scope of the Project

This project focuses on the development of a Java console-based application designed to digitize and manage the core workflows of fire emergency response. The scope covers several distinct operational areas:

### Emergency Management

- Register new fire emergencies into the system.
- Store incident details including the emergency location, fire type, fire severity, and the number of people affected.
- Automatically generate unique Emergency IDs.
- Dynamically calculate a priority score based on the incident's characteristics.

### Resource Management

- Add and view registered Fire Stations.
- Add and view Fire Trucks.
- Add and view Firefighters.
- Track the real-time availability status of individual trucks and firefighters.
- Strictly associate trucks and firefighters with specific fire stations.

### Emergency Dispatch

- Automatically identify the highest-priority reported emergency from the active queue.
- Find an available fire truck and an available firefighter belonging to the same fire station.
- Assign these paired resources to the emergency for dispatch.
- Automatically mark dispatched resources as unavailable for other incidents.
- Release assigned resources back into the available pool once the emergency is successfully resolved.

### Emergency Status Tracking

The system strictly enforces and tracks the emergency lifecycle through predefined, validated statuses:
`REPORTED → ASSIGNED → DISPATCHED → ON_SCENE → RESOLVED`

### Incident Records

- View the active incident history in memory.
- Save the current incident history to a local text file (`incident_history.txt`).
- Read and display previously saved incident history from the file.

### Database Management

- Establish a connection between the Java application and a MySQL database using JDBC.
- Save specific emergency records into the database.
- View all emergency records currently stored in the MySQL database.
- Update an emergency's status directly within the database.

### Multithreading

- Simulate the arrival of multiple concurrent emergency calls.
- Process emergency dispatching logic using multiple independent threads.
- Utilize thread synchronization to safely manage concurrent dispatch operations, preventing race conditions during resource allocation.

### Validation and Error Handling

The system includes robust input validation and custom exception handling to manage invalid emergencies, gracefully handle scenarios with unavailable resources, reject invalid user inputs, prevent duplicate IDs during resource creation, and block invalid emergency status transitions.

_(Note: This is an academic Java console/command-line application. It does not include mobile applications, web interfaces, GPS tracking, live maps, AI prediction, or integration with external live government infrastructure)._

## 3. Target Users

The intended audience and realistic users who could interact with or benefit from a system of this design include:

### Emergency Dispatchers

Personnel responsible for rapidly registering incoming fire emergencies, monitoring their dynamically calculated priority, and executing the dispatch of available station resources.

### Fire Station Personnel

Users who need to manage logistical information regarding fire stations, register new fire trucks and firefighters, and continuously monitor their availability statuses.

### Emergency Response Coordinators

Administrative users who need to oversee the progression of emergency statuses, audit resource assignments, and review active incident records.

### System Administrators / Database Operators

Technical users responsible for maintaining persistent emergency records in the MySQL database, ensuring data integrity, and reviewing stored historical information.

### Students / Academic Evaluators

Because this is an academic software project, it is explicitly targeted toward students and evaluators who wish to demonstrate, review, and evaluate the practical implementation of core Java concepts, including Object-Oriented Programming (OOP), custom exception handling, Java collections, File I/O, JDBC database integration, and safe multithreading.

## 4. High-Level Features

1. **Emergency Registration**
   - Register fire emergencies specifying the location, fire type, severity, and people affected.
   - Automatically generate and assign unique Emergency IDs.

2. **Priority-Based Emergency Assessment**
   - Calculate emergency priority based on the severity level, the number of people affected, and specific modifiers like chemical fire types.
   - Identify higher-priority emergencies to ensure they are processed first for dispatch.

3. **Fire Station Management**
   - Add and view operational fire stations.

4. **Fire Truck Management**
   - Add and view fire trucks.
   - Track individual truck availability.

5. **Firefighter Management**
   - Add and view firefighters.
   - Track individual firefighter availability.

6. **Priority-Based Resource Dispatch**
   - Automatically assign an available fire truck and firefighter from the same station to the highest-priority dispatchable emergency.

7. **Emergency Status Management**
   - Track emergency progression from `REPORTED` to `RESOLVED`.
   - Enforce validated sequential status transitions.
   - Automatically release dispatched resources back to availability when an emergency is marked as resolved.

8. **Incident History**
   - View the running incident history.
   - Save and read incident history leveraging local text file storage.

9. **MySQL Database Integration**
   - Save, view, and update emergency records persistently using JDBC and MySQL.

10. **System Reporting**
    - Display aggregated metrics including total emergencies registered, total resolved emergencies, and the total counts of fire stations, fire trucks, and firefighters.

11. **Multithreading Simulation**
    - Simulate simultaneous multiple emergency calls using separate Java threads.
    - Use synchronized methods to prevent unsafe concurrent resource allocation during simultaneous dispatches.

12. **Exception Handling and Input Validation**
    - Handle programmatic and user errors including invalid input formats, invalid dispatch attempts, unavailable resource scenarios, duplicate entity IDs, and invalid status lifecycle transitions.
