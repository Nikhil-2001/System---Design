package lld.elevator.domain.strategy;

import lld.elevator.domain.Elevator;
import lld.elevator.domain.InternalRequest;

import java.util.List;

public interface MovementStrategy {
    List<Integer> calculatePath(Elevator elevator, List<InternalRequest> requests);
}
