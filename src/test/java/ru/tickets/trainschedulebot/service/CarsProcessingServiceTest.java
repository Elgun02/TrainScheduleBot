package ru.tickets.trainschedulebot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tickets.trainschedulebot.model.Car;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CarsProcessingServiceTest {

    @InjectMocks
    private CarsProcessingService carsProcessingService;

    @Test
    void filterCarriagesWithMinPrice_shouldReturnListWithMinPrices() {

        List<Car> inputCars = Arrays.asList(
                new Car("Type1", 100, 5000),
                new Car("Type2", 150, 3000),
                new Car("Type1", 80, 5500),
                new Car("Type2", 120, 3200)
        );

        List<Car> expectedOutput = Arrays.asList(
                new Car("Type2", 150, 3000),
                new Car("Type1", 100, 5000)
        );

        List<Car> result = carsProcessingService.filterCarriagesWithMinPrice(inputCars);

        assertEquals(expectedOutput, result);

    }

    @Test
    void filterCarriagesWithMinPrice_shouldReturnEmptyListForEmptyInput() {

        List<Car> inputCars = List.of();

        List<Car> result = carsProcessingService.filterCarriagesWithMinPrice(inputCars);

        assertEquals(0, result.size());
    }
}