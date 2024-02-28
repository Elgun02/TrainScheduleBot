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
 * Service class for processing and filtering train carriages.
 * This class contains methods to perform operations on a list of {@link Car} objects.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-28
 */
@Service
public class CarsProcessingService {

    /**
     * Filters a list of carriages to retain only those with the minimum price for each car type.
     *
     * @param cars The list of carriages to be processed.
     * @return A list containing one carriage per car type with the minimum price.
     */
    public List<Car> filterCarriagesWithMinPrice(List<Car> cars) {
        return new ArrayList<>(cars.stream()
                .collect(Collectors.toMap(Car::getCarType, Function.identity(),
                        BinaryOperator.minBy(Comparator.comparing(Car::getMinimalPrice)))).values());
    }
}