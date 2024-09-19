package com.adregamdi.place.application;

import com.adregamdi.media.application.ImageService;
import com.adregamdi.place.domain.Place;
import com.adregamdi.place.domain.PlaceReview;
import com.adregamdi.place.domain.PlaceReviewImage;
import com.adregamdi.place.domain.vo.PlaceNode;
import com.adregamdi.place.dto.*;
import com.adregamdi.place.dto.request.CreatePlaceRequest;
import com.adregamdi.place.dto.request.CreatePlaceReviewRequest;
import com.adregamdi.place.dto.request.GetSortingPlacesRequest;
import com.adregamdi.place.dto.response.*;
import com.adregamdi.place.exception.PlaceException.PlaceExistException;
import com.adregamdi.place.exception.PlaceException.PlaceNotFoundException;
import com.adregamdi.place.exception.PlaceException.PlaceReviewNotFoundException;
import com.adregamdi.place.infrastructure.PlaceRepository;
import com.adregamdi.place.infrastructure.PlaceReviewImageRepository;
import com.adregamdi.place.infrastructure.PlaceReviewRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static com.adregamdi.core.constant.Constant.NORMAL_PAGE_SIZE;
import static com.adregamdi.core.utils.PageUtil.generatePageAsc;
import static com.adregamdi.media.domain.ImageTarget.PLACEREVIEW;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlaceServiceImpl implements PlaceService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final ImageService imageService;
    private final PlaceRepository placeRepository;
    private final PlaceReviewRepository placeReviewRepository;
    private final PlaceReviewImageRepository placeReviewImageRepository;

    @Value("${api-key.visit-jeju}")
    private String visitJejuKey;
    @Value("${api-key.kor-service}")
    private String korServiceKey;

    @Override
    @Transactional
    public void create(final CreatePlaceRequest request) {
        if (placeRepository.findByTitleAndContentsLabel(request.title(), request.contentsLabel()).isPresent()) {
            throw new PlaceExistException(request);
        }
        placeRepository.save(Place.builder()
                .title(request.title())
                .contentsLabel(request.contentsLabel())
                .regionLabel(request.regionLabel())
                .region1Cd(request.region1Cd())
                .region2Cd(request.region2Cd())
                .address(request.address())
                .roadAddress(request.roadAddress())
                .tag(request.tag())
                .introduction(request.introduction())
                .information(request.information())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .phoneNo(request.phoneNo())
                .imgPath(request.imgPath())
                .thumbnailPath(request.thumbnailPath())
                .build()
        );
    }

    @Override
    @Transactional
    public void createByAPI() {
        IntStream.rangeClosed(1, 55).forEach(i -> {
            String url = "https://api.visitjeju.net/vsjApi/contents/searchList?locale=kr&apiKey=" + visitJejuKey + "&page=" + i;

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
                                        String contentsId = item.path("contentsid").asText();
                                        String contentsLabel = item.path("contentscd").path("label").asText();
                                        String region1Value = item.path("region1cd").path("value").asText();
                                        String region2Value = item.path("region2cd").path("value").asText();
                                        String region2Label = item.path("region2cd").path("label").asText();
                                        String address = item.path("address").asText().equals("null") ? "" : item.path("address").asText();
                                        String roadAddress = item.path("roadaddress").asText().equals("null") ? "" : item.path("roadaddress").asText();
                                        String tag = formatTags(item.path("tag").asText());
                                        String introduction = item.path("introduction").asText();
                                        double latitude = item.path("latitude").asDouble();
                                        double longitude = item.path("longitude").asDouble();
                                        String phoneNo = item.path("phoneno").asText().equals("null") ? "" : item.path("phoneno").asText();
                                        String imgPath = item.path("repPhoto").path("photoid").path("imgpath").asText();
                                        String thumbnailPath = item.path("repPhoto").path("photoid").path("thumbnailpath").asText();

                                        return Place.builder()
                                                .title(title)
                                                .contentsId(contentsId)
                                                .contentsLabel(contentsLabel)
                                                .regionLabel(region2Label)
                                                .region1Cd(region1Value)
                                                .region2Cd(region2Value)
                                                .address(address)
                                                .roadAddress(roadAddress)
                                                .tag(tag)
                                                .introduction(introduction)
                                                .latitude(latitude)
                                                .longitude(longitude)
                                                .phoneNo(phoneNo)
                                                .imgPath(imgPath)
                                                .thumbnailPath(thumbnailPath)
                                                .build();
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

    @Override
    @Transactional
    public CreatePlaceReviewResponse createReview(final CreatePlaceReviewRequest request, final String memberId) {
        placeReviewRepository.findByMemberIdAndPlaceIdAndVisitDate(UUID.fromString(memberId), request.placeId(), request.visitDate())
                .orElseThrow(PlaceExistException::new);

        PlaceReview savePlaceReview = placeReviewRepository.save(new PlaceReview(memberId, request.placeId(), request.visitDate(), request.content()));

        List<CreatePlaceReviewRequest.PlaceReviewImageInfo> imageList = (request.placeReviewImageList() != null) ? request.placeReviewImageList() : Collections.emptyList();

        List<PlaceReviewImage> placeReviewImages = imageList.stream()
                .map(img -> new PlaceReviewImage(savePlaceReview.getPlaceReviewId(), img.url()))
                .collect(Collectors.toList());
        placeReviewImageRepository.saveAll(placeReviewImages);

        List<String> urls = placeReviewImages.stream()
                .map(PlaceReviewImage::getUrl)
                .collect(Collectors.toList());
        if (!imageList.isEmpty()) {
            imageService.saveTargetId(urls, PLACEREVIEW, String.valueOf(savePlaceReview.getPlaceReviewId()));
        }
        return new CreatePlaceReviewResponse(savePlaceReview.getPlaceReviewId());
    }

    @Override
    @Transactional
    public void addCount(final Long placeId, final boolean choice) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException(placeId));

        place.updateAddCount(choice ? 1 : -1);
    }

    @Override
    @Transactional(readOnly = true)
    public GetPlaceResponse get(final Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException(placeId));
        return GetPlaceResponse.from(place);
    }

    @Override
    @Transactional(readOnly = true)
    public GetPlacesResponse getPlaces(final int pageNo, final String title) {
        Slice<Place> places = placeRepository.findByTitleStartingWith(title, generatePageAsc(pageNo, NORMAL_PAGE_SIZE, "title"))
                .orElseThrow(() -> new PlaceNotFoundException(title));
        return GetPlacesResponse.from(
                places.getContent()
                        .stream()
                        .map(PlaceDTO::from)
                        .toList()
        );
    }

    @Override
    @Transactional
    public List<GetSelectionBasedRecommendationPlacesResponse> getSelectionBasedRecommendationPlaces(final Double latitude, final Double longitude) throws URISyntaxException {
        String url = buildApiUrl(latitude, longitude);
        URI uri = new URI(url);

        try {
            String jsonResponse = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSubscribe(subscription -> log.info("Sending request to: {}", url))
                    .publishOn(Schedulers.boundedElastic())
                    .doOnNext(response -> log.info("Raw API Response: {}", response))
                    .block();

            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

            if (itemsNode.isMissingNode() || !itemsNode.isArray() || itemsNode.isEmpty()) {
                log.warn("No places found in the API response");
                return Collections.emptyList();
            }

            List<KorServicePlace> items = objectMapper.readValue(
                    itemsNode.toString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, KorServicePlace.class)
            );

            return items.stream()
                    .map(this::processPlace)
                    .collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON response: {}", e.getMessage(), e);
            throw new RuntimeException("Error parsing API response", e);
        } catch (WebClientResponseException e) {
            log.error("WebClient error: {} - Response body: {}", e.getMessage(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Error calling external API", e);
        } catch (Exception e) {
            log.error("Error calling API: {}", e.getMessage(), e);
            throw new RuntimeException("Error calling external API", e);
        }
    }

    @Override
    @Transactional
    public List<GetSortingPlacesResponse> getSortingPlaces(final List<GetSortingPlacesRequest> requests) {
        return requests.stream()
                .map(this::sortPlacesForDay)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GetPopularPlacesResponse getPopularPlaces(final Long lastId, final Integer lastAddCount) {
        List<PopularPlaceDTO> popularPlaces = placeRepository.findInOrderOfPopularAddCount(lastId, lastAddCount);

        boolean hasNext = popularPlaces.size() > 10;
        List<PopularPlaceDTO> content = hasNext ? popularPlaces.subList(0, 10) : popularPlaces;

        List<GetPopularPlacesResponse.PopularPlaceInfo> placeInfos = content.stream()
                .map(dto -> GetPopularPlacesResponse.PopularPlaceInfo.builder()
                        .placeId(dto.place().getPlaceId())
                        .title(dto.place().getTitle())
                        .contentsLabel(dto.place().getContentsLabel())
                        .regionLabel(dto.place().getRegionLabel())
                        .imageUrls(dto.imageUrls())
                        .addCount(dto.place().getAddCount())
                        .photoReviewCount(dto.photoReviewCount())
                        .shortsCount(dto.shortsCount())
                        .build())
                .collect(Collectors.toList());

        int pageSize = placeInfos.size();
        int currentPage = calculateCurrentPage(lastId, pageSize);
        long totalPlaces = placeRepository.countTotalPlaces();

        return GetPopularPlacesResponse.of(
                currentPage,
                pageSize,
                hasNext,
                totalPlaces,
                placeInfos
        );
    }

    @Override
    @Transactional(readOnly = true)
    public GetMyPlaceReviewResponse getMyReview(final String memberId) {
        List<PlaceReview> placeReviews = placeReviewRepository.findAllByMemberIdOrderByPlaceReviewIdDesc(UUID.fromString(memberId))
                .orElseThrow(() -> new PlaceReviewNotFoundException(memberId));
        List<MyPlaceReviewDTO> myPlaceReviews = new ArrayList<>();

        for (PlaceReview placeReview : placeReviews) {
            int imageReviewCount = placeRepository.countPlaceReviewsWithImagesForPlace(placeReview.getPlaceId());
            int shortsReviewCount = placeRepository.countShortsReviewsForPlace(placeReview.getPlaceId());
            List<PlaceReviewImage> placeReviewImages = placeReviewImageRepository.findByPlaceReviewIdOrderByPlaceReviewImageIdDesc(placeReview.getPlaceReviewId());
            Place place = placeRepository.findById(placeReview.getPlaceId())
                    .orElseThrow(() -> new PlaceNotFoundException(placeReview.getPlaceId()));

            myPlaceReviews.add(MyPlaceReviewDTO.of(
                            place.getPlaceId(),
                            place.getTitle(),
                            place.getContentsLabel(),
                            place.getRegionLabel(),
                            imageReviewCount,
                            shortsReviewCount,
                            formatToKoreanString(placeReview.getVisitDate()),
                            placeReview.getContent(),
                            placeReviewImages,
                            LocalDate.from(placeReview.getCreatedAt())
                    )
            );
        }
        return GetMyPlaceReviewResponse.from(myPlaceReviews);
    }

    @Override
    @Transactional(readOnly = true)
    public PlaceReviewDTO getReview(final Long placeReviewId) {
        PlaceReview placeReview = placeReviewRepository.findById(placeReviewId)
                .orElseThrow(() -> new PlaceReviewNotFoundException(placeReviewId));
        Place place = placeRepository.findById(placeReview.getPlaceId())
                .orElseThrow(() -> new PlaceNotFoundException(placeReview.getPlaceId()));
        List<PlaceReviewImage> placeReviewImages = placeReviewImageRepository.findByPlaceReviewIdOrderByPlaceReviewImageIdDesc(placeReview.getPlaceReviewId());

        return PlaceReviewDTO.of(
                place.getPlaceId(),
                place.getTitle(),
                place.getContentsLabel(),
                place.getRegionLabel(),
                formatToKoreanString(placeReview.getVisitDate()),
                placeReview.getContent(),
                placeReviewImages,
                LocalDate.from(placeReview.getCreatedAt())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public GetPlaceReviewsResponse getReviews(final Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException(placeId));
        List<PlaceReview> placeReviews = placeReviewRepository.findAllByPlaceIdOrderByPlaceReviewIdDesc(placeId);

        List<PlaceReviewDTO> placeReviewDTOS = new ArrayList<>();
        for (PlaceReview placeReview : placeReviews) {
            List<PlaceReviewImage> placeReviewImages = placeReviewImageRepository.findByPlaceReviewIdOrderByPlaceReviewImageIdDesc(placeReview.getPlaceReviewId());

            placeReviewDTOS.add(PlaceReviewDTO.of(
                            place.getPlaceId(),
                            place.getTitle(),
                            place.getContentsLabel(),
                            place.getRegionLabel(),
                            formatToKoreanString(placeReview.getVisitDate()),
                            placeReview.getContent(),
                            placeReviewImages,
                            LocalDate.from(placeReview.getCreatedAt())
                    )
            );
        }
        return GetPlaceReviewsResponse.from(placeReviewDTOS);
    }

    @Override
    @Transactional(readOnly = true)
    public GetPlaceImagesResponse getPlaceImages(final Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException(placeId));
        List<PlaceReview> placeReviews = placeReviewRepository.findAllByPlaceIdOrderByPlaceReviewIdDesc(placeId);

        List<PlaceImageDTO> placeImageDTOS = new ArrayList<>();
        for (PlaceReview placeReview : placeReviews) {
            List<PlaceReviewImage> placeReviewImages = placeReviewImageRepository.findByPlaceReviewIdOrderByPlaceReviewImageIdDesc(placeReview.getPlaceReviewId());

            for (PlaceReviewImage placeReviewImage : placeReviewImages) {
                placeImageDTOS.add(new PlaceImageDTO(LocalDate.from(placeReviewImage.getCreatedAt()), placeReviewImage.getUrl()));
            }
        }
        placeImageDTOS.add(new PlaceImageDTO(LocalDate.from(place.getCreatedAt()), place.getImgPath()));
        return GetPlaceImagesResponse.of(placeId, placeImageDTOS);
    }

    private String formatTags(final String tag) {
        String[] tags = tag.split(",");
        StringBuilder formattedTags = new StringBuilder();
        for (String t : tags) {
            formattedTags.append("#").append(t).append(" ");
        }
        return formattedTags.toString().trim();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String buildApiUrl(Double latitude, Double longitude) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://apis.data.go.kr/B551011/KorService1/locationBasedList1");

        builder.queryParam("numOfRows", encode("5"))
                .queryParam("MobileOS", encode("ETC"))
                .queryParam("MobileApp", encode("Adregamdi"))
                .queryParam("mapX", encode(longitude.toString()))
                .queryParam("mapY", encode(latitude.toString()))
                .queryParam("radius", encode("3000"))
                .queryParam("_type", encode("json"))
                .queryParam("arrange", encode("Q"));

        String encodedUrl = builder.toUriString();
        return encodedUrl + "&serviceKey=" + korServiceKey;
    }

    private GetSelectionBasedRecommendationPlacesResponse processPlace(final KorServicePlace item) {
        Place place = placeRepository.findByTitle(item.title())
                .orElseGet(() -> saveNewPlace(item));

        int reviewCount = placeReviewRepository.countByPlaceId(place.getPlaceId());

        return GetSelectionBasedRecommendationPlacesResponse.of(place, reviewCount);
    }

    private Place saveNewPlace(final KorServicePlace item) {
        Place newPlace = Place.builder()
                .title(item.title())
                .contentsLabel(getContentsLabel(item.contenttypeid()))
                .regionLabel(getRegionLabel(item.sigungucode()))
                .address(item.getFullAddress())
                .roadAddress(item.getFullAddress())
                .latitude(item.getLatitude())
                .longitude(item.getLongitude())
                .imgPath(item.firstimage())
                .thumbnailPath(item.firstimage2())
                .build();

        return placeRepository.save(newPlace);
    }

    private String getContentsLabel(String contenttypeid) {
        return switch (contenttypeid) {
            case "12" -> "관광지";
            case "14" -> "문화시설";
            case "15" -> "행사/공연/축제";
            case "25" -> "여행코스";
            case "28" -> "레포츠";
            case "32" -> "숙박";
            case "38" -> "쇼핑";
            case "39" -> "음식점";
            default -> "기타";
        };
    }

    private String getRegionLabel(String sigungucode) {
        return switch (sigungucode) {
            case "1" -> "남제주군";
            case "2" -> "북제주군";
            case "3" -> "서귀포시";
            case "4" -> "제주시";
            default -> "기타";
        };
    }

    private GetSortingPlacesResponse sortPlacesForDay(final GetSortingPlacesRequest request) {
        List<PlaceNode> allNodes = new ArrayList<>();
        allNodes.add(new PlaceNode(null, 0, request.startLatitude(), request.startLongitude()));
        allNodes.addAll(request.placeCoordinates().stream()
                .map(p -> new PlaceNode(p.placeId(), p.order(), p.latitude(), p.longitude()))
                .toList());
        allNodes.add(new PlaceNode(null, allNodes.size() - 1, request.endLatitude(), request.endLongitude()));

        List<PlaceNode> optimalPath = findOptimalPath(allNodes);

        List<PlaceCoordinate> sortedCoordinates = optimalPath.stream()
                .skip(1) // 시작점 제외
                .limit(optimalPath.size() - 2) // 끝점 제외
                .map(node -> new PlaceCoordinate(node.getPlaceId(), optimalPath.indexOf(node), node.getLatitude(), node.getLongitude()))
                .collect(Collectors.toList());

        return new GetSortingPlacesResponse(request.day(), sortedCoordinates);
    }

    private List<PlaceNode> findOptimalPath(List<PlaceNode> nodes) {
        List<PlaceNode> bestPath = null;
        double shortestDistance = Double.MAX_VALUE;
        List<PlaceNode> middleNodes = nodes.subList(1, nodes.size() - 1);

        for (List<PlaceNode> permutation : generatePermutations(middleNodes)) {
            List<PlaceNode> currentPath = new ArrayList<>();
            currentPath.add(nodes.get(0)); // 시작점
            currentPath.addAll(permutation);
            currentPath.add(nodes.get(nodes.size() - 1)); // 끝점

            double distance = calculateTotalDistance(currentPath);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                bestPath = new ArrayList<>(currentPath);
            }
        }

        return bestPath;
    }

    private List<List<PlaceNode>> generatePermutations(List<PlaceNode> nodes) {
        if (nodes.size() <= 1) {
            return Collections.singletonList(nodes);
        }

        List<List<PlaceNode>> result = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            PlaceNode current = nodes.get(i);
            List<PlaceNode> remaining = new ArrayList<>(nodes);
            remaining.remove(i);

            for (List<PlaceNode> permutation : generatePermutations(remaining)) {
                List<PlaceNode> newPermutation = new ArrayList<>();
                newPermutation.add(current);
                newPermutation.addAll(permutation);
                result.add(newPermutation);
            }
        }

        return result;
    }

    private double calculateTotalDistance(List<PlaceNode> path) {
        double totalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            totalDistance += calculateDistance(path.get(i).getLatitude(), path.get(i).getLongitude(),
                    path.get(i + 1).getLatitude(), path.get(i + 1).getLongitude());
        }
        return totalDistance;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine 공식
        double R = 6371; // 지구의 반지름 (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private int calculateCurrentPage(Long lastId, int pageSize) {
        if (lastId == null) {
            return 0;
        }
        return (lastId.intValue() / pageSize) + 1;
    }

    private String formatToKoreanString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 방문", Locale.KOREAN);
        return date.format(formatter);
    }
}
