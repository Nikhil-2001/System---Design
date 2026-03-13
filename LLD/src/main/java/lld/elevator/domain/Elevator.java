package lld.elevator.domain;

import lld.elevator.domain.state.ElevatorStateHandler;
import lld.elevator.domain.state.PreMaintenanceState;
import lld.elevator.domain.state.StoppedState;

import java.util.UUID;

public class Elevator {
    private String id;
    private String buildingId;
    private int currentFloor;
    private Direction direction;
    private ElevatorState state;
    private int capacity;
    private int currentLoad;
    private boolean isActive;
    private ElevatorStateHandler stateHandler;
    private Integer currentTargetFloor; // Current floor the elevator is heading to

    public Elevator(String buildingId, int capacity) {
        this.id = UUID.randomUUID().toString();
        this.buildingId = buildingId;
        this.currentFloor = 1; // Start at ground floor
        this.direction = Direction.IDLE;
        this.state = ElevatorState.STOPPED;
        this.capacity = capacity;
        this.currentLoad = 0;
        this.isActive = true;
        this.stateHandler = new StoppedState(); // Initialize with stopped state
    }

    // Getters
    public String getId() { return id; }
    public String getBuildingId() { return buildingId; }
    public synchronized int getCurrentFloor() { return currentFloor; }
    public synchronized Direction getDirection() { return direction; }
    public synchronized ElevatorState getState() { return state; }
    public int getCapacity() { return capacity; }
    public int getCurrentLoad() { return currentLoad; }
    public boolean isActive() { return isActive; }
    public synchronized ElevatorStateHandler getStateHandler() { return stateHandler; }
    public synchronized Integer getCurrentTargetFloor() { return currentTargetFloor; }
    public synchronized void setCurrentTargetFloor(Integer currentTargetFloor) { this.currentTargetFloor = currentTargetFloor; }

    // Setters
    public synchronized void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public synchronized void setDirection(Direction direction) {
        this.direction = direction;
    }

    public synchronized void setState(ElevatorState state) {
        this.state = state;
    }

    public void setCurrentLoad(int currentLoad) {
        this.currentLoad = currentLoad;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public synchronized void setStateHandler(ElevatorStateHandler stateHandler) {
        this.stateHandler = stateHandler;
    }

    public boolean isAvailable() {
        return stateHandler.canAcceptExternalRequests(this);
    }

    public boolean canAcceptInternalRequests() {
        return stateHandler.canAcceptInternalRequests(this);
    }

    public boolean isFull() {
        return currentLoad >= capacity;
    }

    // State pattern delegation methods

    public synchronized void openDoors() {
        stateHandler.openDoors(this);
    }

    public synchronized void closeDoors() {
        stateHandler.closeDoors(this);
    }

    public synchronized void enterMaintenance() {
        stateHandler.enterMaintenance(this);
    }

    public synchronized void exitMaintenance() {
        stateHandler.exitMaintenance(this);
    }

    public boolean isPreparingForMaintenance() {
        return stateHandler instanceof PreMaintenanceState;
    }
}
