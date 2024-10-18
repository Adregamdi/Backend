package com.adregamdi.place.application;

import com.adregamdi.block.domain.Block;
import com.adregamdi.block.exception.BlockException;
import com.adregamdi.block.infrastructure.BlockRepository;
import com.adregamdi.core.constant.ContentType;
import com.adregamdi.like.application.LikesService;
import com.adregamdi.media.application.ImageService;
import com.adregamdi.member.domain.Member;
import com.adregamdi.member.exception.MemberException;
import com.adregamdi.member.infrastructure.MemberRepository;
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
import com.adregamdi.place.exception.PlaceException.PlaceReviewExistException;
import com.adregamdi.place.exception.PlaceException.PlaceReviewNotFoundException;
import com.adregamdi.place.infrastructure.PlaceRepository;
import com.adregamdi.place.infrastructure.PlaceReviewImageRepository;
import com.adregamdi.place.infrastructure.PlaceReviewRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final LikesService likesService;
    private final PlaceRedisService placeRedisService;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;
    private final PlaceReviewRepository placeReviewRepository;
    private final PlaceReviewImageRepository placeReviewImageRepository;
    private final BlockRepository blockRepository;

    @Value("${api-key.visit-jeju}")
    private String visitJejuKey;
    @Value("${api-key.kor-service}")
    private String korServiceKey;

    @Async
    @PostConstruct
    public void asyncInit() {
        updatePopularPlacesCache();
    }

    /*
     * [장소 등록]
     * */
    @Override
    @Transactional
    public void create(final CreatePlaceRequest request) {
        if (placeRepository.findByTitleAndContentsLabel(request.title(), request.contentsLabel()).isPresent()) {
            throw new PlaceExistException(request);
        }
        placeRepository.save(Place.builder()
                .title(request.title())
                .contentsId(request.contentsId())
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

    /*
     * [장소 등록 By 외부 API]
     * */
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

                            List<Place> places = StreamSupport.stream(items.spliterator(), false)
                                    .map(item -> {
                                        if (item.path("latitude").asDouble() == 0 || item.path("longitude").asDouble() == 0) {
                                            return null;
                                        }
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
                                    .filter(Objects::nonNull)
                                    .toList();

                            placeRepository.saveAll(places);

                            return "Data saved successfully";
                        } catch (Exception e) {
                            return "Error processing response: " + e.getMessage();
                        }
                    })
                    .block();
        });
    }

    /*
     * [장소 리뷰 등록]
     * */
    @Override
    @Transactional
    public CreatePlaceReviewResponse createReview(final String memberId, final CreatePlaceReviewRequest request) {
        placeReviewRepository.findByMemberIdAndPlaceId(memberId, request.placeId())
                .ifPresent(data -> {
                    throw new PlaceReviewExistException(data.getPlaceReviewId());
                });

        PlaceReview savePlaceReview = placeReviewRepository.save(PlaceReview.builder()
                .memberId(memberId)
                .placeId(request.placeId())
                .content(request.content())
                .build());

        List<CreatePlaceReviewRequest.PlaceReviewImageInfo> imageList = (request.placeReviewImageList() != null) ? request.placeReviewImageList() : Collections.emptyList();

        List<PlaceReviewImage> placeReviewImages = imageList.stream()
                .map(img -> PlaceReviewImage.builder()
                        .placeReviewId(savePlaceReview.getPlaceReviewId())
                        .url(img.url())
                        .build())
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

    /*
     * [장소 추가 카운트 증감]
     * */
    @Override
    @Transactional
    public void addCount(final Long placeId, final boolean choice) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException(placeId));

        place.updateAddCount(choice ? place.getAddCount() + 1 : place.getAddCount() - 1);
    }

    /*
     * [특정 장소 조회]
     * */
    @Override
    @Transactional(readOnly = true)
    public GetPlaceResponse get(final String memberId, final Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException(placeId));
        boolean isLiked = likesService.checkIsLiked(memberId, ContentType.PLACE, placeId);
        return GetPlaceResponse.of(isLiked, place);
    }

    /*
     * [장소 리스트 조회]
     * */
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

    /*
     * [선택 기반 추천 장소 리스트 조회]
     * */
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

    /*
     * [최적 거리 정렬]
     * */
    @Override
    @Transactional
    public List<GetSortingPlacesResponse> getSortingPlaces(final List<GetSortingPlacesRequest> requests) {
        return requests.stream()
                .map(this::sortPlacesForDay)
                .collect(Collectors.toList());
    }

    /*
     * [일정에 많이 추가된 장소 리스트 조회]
     * */
    @Override
    @Transactional(readOnly = true)
    public GetPopularPlacesResponse getPopularPlaces(final Long lastId, final Integer lastAddCount) {
        List<PopularPlaceDTO> popularPlaces = placeRedisService.getPopularPlaces();

        if (popularPlaces == null || popularPlaces.isEmpty()) {
            // 캐시 미스 시 DB에서 조회
            popularPlaces = placeRepository.findInOrderOfPopularAddCount(lastId, lastAddCount);
            placeRedisService.savePopularPlaces(popularPlaces);
        }

        // 페이지네이션 로직
        boolean hasNext = popularPlaces.size() > 10;
        List<PopularPlaceDTO> content = hasNext ? popularPlaces.subList(0, 10) : popularPlaces;

        // DTO 변환 및 응답 생성 로직
        List<GetPopularPlacesResponse.PopularPlaceInfo> placeInfos = content.stream()
                .map(GetPopularPlacesResponse.PopularPlaceInfo::from)
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

    @Scheduled(fixedRate = 3600000) // 매 시간마다 실행
    protected void updatePopularPlacesCache() {
        log.info("★ 인기 장소 캐시 업데이트 ⭐");
        try {
            List<PopularPlaceDTO> popularPlaces = placeRepository.findInOrderOfPopularAddCount(null, null);
            placeRedisService.savePopularPlaces(popularPlaces);
        } catch (RuntimeException e) {
            log.error("인기 장소 캐시 업데이트 중 오류 발생", e);
        }
    }

    /*
     * [내 리뷰 조회]
     * */
    @Override
    @Transactional(readOnly = true)
    public GetMyPlaceReviewResponse getMyReview(final String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException.MemberNotFoundException(memberId));
        List<PlaceReview> placeReviews = placeReviewRepository.findAllByMemberIdOrderByPlaceReviewIdDesc(memberId);
        List<MyPlaceReviewDTO> myPlaceReviews = new ArrayList<>();

        for (PlaceReview placeReview : placeReviews) {
            int imageReviewCount = placeRepository.countPlaceReviewsWithImagesForPlace(placeReview.getPlaceId());
            int shortsReviewCount = placeRepository.countShortsReviewsForPlace(placeReview.getPlaceId());
            List<PlaceReviewImage> placeReviewImages = placeReviewImageRepository.findByPlaceReviewIdOrderByPlaceReviewImageIdDesc(placeReview.getPlaceReviewId());
            Place place = placeRepository.findById(placeReview.getPlaceId())
                    .orElseThrow(() -> new PlaceNotFoundException(placeReview.getPlaceId()));

            myPlaceReviews.add(MyPlaceReviewDTO.of(
                            place.getPlaceId(),
                            placeReview.getPlaceReviewId(),
                            place.getTitle(),
                            place.getContentsLabel(),
                            place.getRegionLabel(),
                            imageReviewCount,
                            shortsReviewCount,
                            formatToKoreanString(placeReview.getVisitDate()),
                            placeReview.getContent(),
                            placeReviewImages,
                            LocalDate.from(placeReview.getCreatedAt()),
                            member.getName(),
                            member.getProfile(),
                            member.getHandle()
                    )
            );
        }
        return GetMyPlaceReviewResponse.from(myPlaceReviews);
    }

    /*
     * [특정 리뷰 조회]
     * */
    @Override
    @Transactional(readOnly = true)
    public PlaceReviewDTO getReview(final String memberId, final Long placeReviewId) {
        PlaceReview placeReview = placeReviewRepository.findById(placeReviewId)
                .orElseThrow(() -> new PlaceReviewNotFoundException(placeReviewId));
        Place place = placeRepository.findById(placeReview.getPlaceId())
                .orElseThrow(() -> new PlaceNotFoundException(placeReview.getPlaceId()));
        Member member = memberRepository.findById(placeReview.getMemberId())
                .orElseThrow(() -> new MemberException.MemberNotFoundException(placeReview.getMemberId()));

        blockRepository.findByBlockedMemberIdAndBlockingMemberId(placeReview.getMemberId(), memberId)
                .ifPresent(BlockException.BlockExistException::new);

        List<PlaceReviewImage> placeReviewImages = placeReviewImageRepository.findByPlaceReviewIdOrderByPlaceReviewImageIdDesc(placeReview.getPlaceReviewId());

        return PlaceReviewDTO.of(
                place.getPlaceId(),
                placeReview.getPlaceReviewId(),
                place.getTitle(),
                place.getContentsLabel(),
                place.getRegionLabel(),
                formatToKoreanString(placeReview.getVisitDate()),
                placeReview.getContent(),
                placeReviewImages,
                LocalDate.from(placeReview.getCreatedAt()),
                member.getMemberId(),
                member.getName(),
                member.getProfile(),
                member.getHandle()
        );
    }

    /*
     * [특정 장소의 전체 리뷰 조회]
     * */
    @Override
    @Transactional(readOnly = true)
    public GetPlaceReviewsResponse getReviews(final String memberId, final Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException(placeId));
        List<PlaceReview> placeReviews = placeReviewRepository.findAllByPlaceIdOrderByPlaceReviewIdDesc(placeId);

        List<PlaceReviewDTO> placeReviewDTOS = new ArrayList<>();
        for (PlaceReview placeReview : placeReviews) {
            Optional<Member> member = memberRepository.findById(placeReview.getMemberId());
            if (member.isEmpty()) {
                continue;
            }

            Optional<Block> block = blockRepository.findByBlockedMemberIdAndBlockingMemberId(placeReview.getMemberId(), memberId);
            if (block.isPresent()) {
                continue;
            }

            List<PlaceReviewImage> placeReviewImages = placeReviewImageRepository.findByPlaceReviewIdOrderByPlaceReviewImageIdDesc(placeReview.getPlaceReviewId());

            placeReviewDTOS.add(PlaceReviewDTO.of(
                            place.getPlaceId(),
                            placeReview.getPlaceReviewId(),
                            place.getTitle(),
                            place.getContentsLabel(),
                            place.getRegionLabel(),
                            formatToKoreanString(placeReview.getVisitDate()),
                            placeReview.getContent(),
                            placeReviewImages,
                            LocalDate.from(placeReview.getCreatedAt()),
                            member.get().getMemberId(),
                            member.get().getName(),
                            member.get().getProfile(),
                            member.get().getHandle()
                    )
            );
        }
        return GetPlaceReviewsResponse.from(placeReviewDTOS);
    }

    /*
     * [특정 장소의 전체 사진 조회]
     * */
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

    /*
     * [특정 회원의 모든 리뷰 삭제]
     * */
    @Override
    @Transactional
    public void deleteMyReview(final String memberId) {
        List<PlaceReview> placeReviews = placeReviewRepository.findAllByMemberIdOrderByPlaceReviewIdDesc(memberId);
        if (placeReviews.isEmpty()) {
            return;
        }

        for (PlaceReview placeReview : placeReviews) {
            List<PlaceReviewImage> placeReviewImages = placeReviewImageRepository.findByPlaceReviewIdOrderByPlaceReviewImageIdDesc(placeReview.getPlaceReviewId());

            placeReviewImageRepository.deleteAll(placeReviewImages);
            placeReviewRepository.delete(placeReview);
        }
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
        if (date != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 방문", Locale.KOREAN);
            return date.format(formatter);
        }
        return null;
    }
}
