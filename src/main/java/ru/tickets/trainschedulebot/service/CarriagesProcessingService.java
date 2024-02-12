package ru.tickets.trainschedulebot.service;

import org.springframework.stereotype.Service;
import ru.tickets.trainschedulebot.model.RailwayCarriage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Elgun Dilanchiev
 */
@Service
public class CarriagesProcessingService {
    public List<RailwayCarriage> filterCarriagesWithMinPrice(List<RailwayCarriage> carriages) {
        return new ArrayList<>(carriages.stream()
                .collect(Collectors.toMap(RailwayCarriage::getCarType, Function.identity(),
                        BinaryOperator.minBy(Comparator.comparing(RailwayCarriage::getMinimalPrice)))).values());
    }
}
