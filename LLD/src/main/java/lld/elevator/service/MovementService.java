package lld.elevator.service;

import lld.elevator.domain.*;
import lld.elevator.domain.state.PreMaintenanceState;
import lld.elevator.domain.strategy.MovementStrategy;
import lld.elevator.domain.strategy.ScanStrategy;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MovementService {
    private final ElevatorService elevatorService;
    private final RequestService requestService;
    private final BuildingService buildingService;
    private final DispatcherService dispatcherService;
    private MovementStrategy movementStrategy;
    private ScheduledExecutorService scheduler;
    private volatile boolean systemRunning = false;

    public MovementService(ElevatorService elevatorService, RequestService requestService,
                           BuildingService buildingService, DispatcherService dispatcherService) {
        this.elevatorService = elevatorService;
        this.requestService = requestService;
        this.buildingService = buildingService;
        this.dispatcherService = dispatcherService;
        this.movementStrategy = new ScanStrategy(); // Default strategy
    }

    public void startElevatorSystem(String buildingId) {
        systemRunning = true;
        buildingService.setBuildingSystemState(buildingId, SystemState.RUNNING);

        scheduler = Executors.newScheduledThreadPool(2);

        // Schedule request processing
        scheduler.scheduleAtFixedRate(() -> {
            if (systemRunning) {
                // these external req's have not been assigned to an elevator yet
                dispatcherService.processPendingRequests(buildingId);
            }
        }, 0, 1, TimeUnit.SECONDS);

        // Schedule elevator movement processing
        scheduler.scheduleAtFixedRate(() -> {
            if (systemRunning) {
                processAllElevatorMovements(buildingId);
            }
        }, 0, 2, TimeUnit.SECONDS);

        System.out.println("Elevator system started for building: " + buildingId);
    }

    public void stopElevatorSystem(String buildingId) {
        buildingService.setBuildingSystemState(buildingId, SystemState.STOPPING);
        System.out.println("Stopping elevator system gracefully...");

        // Continue processing until all requests are completed
        while (hasPendingRequests(buildingId)) {
            processAllElevatorMovements(buildingId);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        systemRunning = false;
        if (scheduler != null) {
            scheduler.shutdown();
        }
        buildingService.setBuildingSystemState(buildingId, SystemState.STOPPED);
        System.out.println("Elevator system stopped gracefully");
    }

    public void processAllElevatorMovements(String buildingId) {
        List<Elevator> elevators = elevatorService.getAllElevators(buildingId);

        for (Elevator elevator : elevators) {
            if (elevator.isActive()) {
                processElevatorMovement(elevator.getId(), elevator);
            }
        }
    }

    public void processElevatorMovement(String elevatorId, Elevator elevator) {
        List<InternalRequest> pendingRequests = requestService.getPendingRequestsForElevator(elevatorId);

        if (pendingRequests.isEmpty()) {
            // No requests - check if in pre-maintenance mode
            if (elevator.getStateHandler() instanceof PreMaintenanceState preMaintenanceState) {
                // Transition to full maintenance
                preMaintenanceState.checkForMaintenanceTransition(elevator);
                elevatorService.updateElevatorState(elevatorId, elevator.getState());
                return;
            }

            // Normal idle state
            if (elevator.getDirection() != Direction.IDLE) {
                elevator.setDirection(Direction.IDLE);
                elevator.setState(ElevatorState.STOPPED);
                elevatorService.updateElevatorState(elevatorId, ElevatorState.STOPPED);
            }
            return;
        }

        List<Integer> path = movementStrategy.calculatePath(elevator, pendingRequests);

        if (!path.isEmpty()) {
            int nextFloor = path.get(0);

            if (elevator.getState() == ElevatorState.STOPPED) {
                // Start moving towards next floor
                Direction direction = nextFloor > elevator.getCurrentFloor() ? Direction.UP : Direction.DOWN;
                elevator.setDirection(direction);
                elevator.setState(ElevatorState.MOVING);
                System.out.println("Elevator " + elevatorId + " starting to move " + direction + " to floor " + nextFloor);
            } else if (elevator.getState() == ElevatorState.MOVING) {
                // Continue moving one floor
                Direction direction = elevator.getDirection();
                int currentFloor = elevator.getCurrentFloor();
                int newFloor = direction == Direction.UP ? currentFloor + 1 : currentFloor - 1;
                elevator.setCurrentFloor(newFloor);
                System.out.println("Elevator " + elevatorId + " moved to floor " + newFloor);

                // Check if reached target floor
                if (newFloor == nextFloor) {
                    // Arrived at destination
                    elevator.setState(ElevatorState.STOPPED);
                    elevator.setDirection(Direction.IDLE);
                    elevator.openDoors();

                    // Complete internal requests for this floor
                    List<InternalRequest> requestsForFloor = pendingRequests.stream()
                            .filter(r -> r.getDestinationFloor() == nextFloor)
                            .collect(Collectors.toList());

                    for (InternalRequest request : requestsForFloor) {
                        requestService.completeInternalRequest(request.getId());
                        System.out.println("Completed internal request " + request.getId() + " for floor " + nextFloor);
                    }

                    System.out.println("Elevator " + elevatorId + " arrived at floor " + nextFloor);
                }
            }
        }
    }


    private boolean hasPendingRequests(String buildingId) {
        List<Elevator> elevators = elevatorService.getAllElevators(buildingId);

        for (Elevator elevator : elevators) {
            List<InternalRequest> pendingRequests = requestService.getPendingRequestsForElevator(elevator.getId());
            if (!pendingRequests.isEmpty()) {
                return true;
            }
        }

        return dispatcherService.getQueueSize() > 0;
    }

    public void setMovementStrategy(MovementStrategy strategy) {
        this.movementStrategy = strategy;
    }
}
