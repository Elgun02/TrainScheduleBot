package ru.tickets.trainschedulebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.tickets.trainschedulebot.cache.StationsDataCache;
import ru.tickets.trainschedulebot.model.TrainStation;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StationCodeService {
    private final RestTemplate restTemplate;
    private final StationsDataCache stationsCache;

    @Value("${station.code.service.request.template}")
    private String stationCodeRequestTemplate;

    public int getStationCode(String stationName) {
        String stationNameParam = stationName.toUpperCase();

        Optional<Integer> stationCodeOptional = stationsCache.getStationCode(stationNameParam);
        if (stationCodeOptional.isPresent()) {
            return stationCodeOptional.get();
        } else if (processStationCodeRequest(stationNameParam).isEmpty()) {
            return -1;
        }

        return stationsCache.getStationCode(stationNameParam).orElse(-1);
    }

    private Optional<TrainStation[]> processStationCodeRequest(String stationNamePart) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<TrainStation[]> response = restTemplate.exchange(
                    stationCodeRequestTemplate,
                    HttpMethod.GET,
                    entity,
                    TrainStation[].class,
                    stationNamePart
            );

            TrainStation[] stations = response.getBody();
            System.out.println(Arrays.toString(stations));

            if (stations == null || stations.length == 0) {
                return Optional.empty();
            }

            for (TrainStation station : stations) {
                stationsCache.addStationToCache(station.getStationName(), station.getStationCode());
            }

            return Optional.of(stations);

        } catch (HttpClientErrorException e) {
            log.error("Ошибка HTTP: " + e.getStatusCode() + ", " + e.getStatusText());
            log.error("Тело ответа: " + e.getResponseBodyAsString());

            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new SecurityException("У вас нет разрешения на доступ к запрашиваемому ресурсу.", e);
            } else {
                throw new RuntimeException("Ошибка при запросе кода станции", e);
            }

        } catch (Exception e) {
            log.error("Произошла ошибка: " + e.getMessage());
        }
        return Optional.empty();
    }
}
