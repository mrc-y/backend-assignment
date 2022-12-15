package com.katanox.api.search.controller;

import com.katanox.api.search.exception.DatesNotValidException;
import com.katanox.api.search.payload.SearchRequest;
import com.katanox.api.search.payload.SearchResponse;
import com.katanox.api.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("search")
@Slf4j
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping(path = "/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<SearchResponse> search(@RequestBody SearchRequest request) throws DatesNotValidException {
        log.info("Search request has arrived for hotel id: {} for dates between {}-{}", request.getHotelId(),
                request.getCheckin(), request.getCheckout());

        SearchResponse searchResponse = searchService.search(request);
        searchResponse.getRoomPriceResponseList()
                .forEach(roomPriceResponse -> log.info("result: " + roomPriceResponse.toString()));

        return new ResponseEntity<>(searchResponse,
                searchResponse.getRoomPriceResponseList().isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK);
    }

}
