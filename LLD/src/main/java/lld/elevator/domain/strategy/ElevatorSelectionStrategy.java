package lld.elevator.domain.strategy;

import lld.elevator.domain.Elevator;
import lld.elevator.domain.ExternalRequest;

import java.util.List;

public interface ElevatorSelectionStrategy {
    Elevator selectElevator(ExternalRequest request, List<Elevator> availableElevators);
}