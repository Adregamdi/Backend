package com.adregamdi.place.application;

import com.adregamdi.place.domain.Place;
import com.adregamdi.place.dto.PlaceDTO;
import com.adregamdi.place.dto.request.CreatePlaceRequest;
import com.adregamdi.place.dto.response.GetPlaceResponse;
import com.adregamdi.place.dto.response.GetPlacesResponse;
import com.adregamdi.place.exception.PlaceException.PlaceExistException;
import com.adregamdi.place.exception.PlaceException.PlaceNotFoundException;
import com.adregamdi.place.infrastructure.PlaceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;

import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static com.adregamdi.core.constant.Constant.NORMAL_PAGE_SIZE;
import static com.adregamdi.core.utils.PageUtil.generatePageAsc;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlaceServiceImpl implements PlaceService {
    private final WebClient webClient;
    private final PlaceRepository placeRepository;
    @Value("${api-key.visit-jeju}")
    private String apiKey;

    @Override
    @Transactional(readOnly = true)
    public GetPlaceResponse get(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException(placeId));
        return GetPlaceResponse.from(place);
    }

    @Override
    @Transactional(readOnly = true)
    public GetPlacesResponse getPlaces(int pageNo, String name) {
        Slice<Place> places = placeRepository.findByNameStartingWith(generatePageAsc(pageNo, NORMAL_PAGE_SIZE, "name"), name)
                .orElseThrow(() -> new PlaceNotFoundException(name));
        return GetPlacesResponse.from(
                places.getContent()
                        .stream()
                        .map(PlaceDTO::from)
                        .toList()
        );
    }

    @Override
    @Transactional
    public void create(CreatePlaceRequest request) {
        if (placeRepository.findByTitleAndContentsLabel(request.title(), request.contentsLabel()).isPresent()) {
            throw new PlaceExistException(request);
        }
        placeRepository.save(new Place(request));
    }

    @Override
    @Transactional
    public void createByAPI() {
        IntStream.rangeClosed(1, 55).forEach(i -> {
            String url = "https://api.visitjeju.net/vsjApi/contents/searchList?locale=kr&apiKey=" + apiKey + "&page=" + i;

            webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .publishOn(Schedulers.boundedElastic())
                    .map(response -> {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode jsonNode = objectMapper.readTree(response);
                            JsonNode items = jsonNode.path("items");

                            StreamSupport.stream(items.spliterator(), false)
                                    .map(item -> {
                                        String title = item.path("title").asText();
                                        String contentsLabel = item.path("contentscd").path("label").asText();
                                        String region1Value = item.path("region1cd").path("value").asText();
                                        String region2Value = item.path("region2cd").path("value").asText();
                                        String region2Label = item.path("region2cd").path("label").asText();
                                        String address = item.path("address").asText();
                                        String roadAddress = item.path("roadaddress").asText();
                                        String tag = formatTags(item.path("tag").asText());
                                        String introduction = item.path("introduction").asText();
                                        double latitude = item.path("latitude").asDouble();
                                        double longitude = item.path("longitude").asDouble();
                                        String phoneNo = item.path("phoneno").asText();
                                        String imgPath = item.path("repPhoto").path("photoid").path("imgpath").asText();
                                        String thumbnailPath = item.path("repPhoto").path("photoid").path("thumbnailpath").asText();

                                        return new Place(title, contentsLabel, region1Value, region2Value, region2Label, address, roadAddress, tag, introduction, latitude, longitude, phoneNo, imgPath, thumbnailPath);
                                    })
                                    .forEach(placeRepository::save);

                            return "Data saved successfully";
                        } catch (Exception e) {
                            return "Error processing response: " + e.getMessage();
                        }
                    })
                    .block();
        });
    }

    private String formatTags(String tag) {
        String[] tags = tag.split(",");
        StringBuilder formattedTags = new StringBuilder();
        for (String t : tags) {
            formattedTags.append("#").append(t).append(" ");
        }
        return formattedTags.toString().trim();
    }
}
