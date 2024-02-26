package ru.tickets.trainschedulebot.service;

import org.springframework.stereotype.Service;
import ru.tickets.trainschedulebot.model.Car;

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
public class CarsProcessingService {
    public List<Car> filterCarriagesWithMinPrice(List<Car> cars) {
        return new ArrayList<>(cars.stream()
                .collect(Collectors.toMap(Car::getCarType, Function.identity(),
                        BinaryOperator.minBy(Comparator.comparing(Car::getMinimalPrice)))).values());
    }
}