package com.katanox.api.integrationtest.search;

import com.katanox.api.Application;
import com.katanox.api.common.error.ErrorResponse;
import com.katanox.api.integrationtest.helper.HeaderGenerationHelper;
import com.katanox.api.integrationtest.helper.PayloadGenerationHelper;
import com.katanox.api.search.payload.SearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
@Slf4j
class SearchIntegrationTest {

    private static final String SEARCH_URL = "/search/";

    @Autowired
    protected TestRestTemplate restTemplate;

    private static final PostgreSQLContainer POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(
            "postgres").withDatabaseName("katanox_test")
            .withUsername("katanox-user")
            .withPassword("123456")
            .withExposedPorts(5432);

    @DynamicPropertySource
    static void registerTestContainerAddressesToProperties(DynamicPropertyRegistry registry) {
        String dbUrl = "jdbc:postgresql://" + POSTGRESQL_CONTAINER.getHost() + ":" +
                POSTGRESQL_CONTAINER.getFirstMappedPort() + "/" + POSTGRESQL_CONTAINER.getDatabaseName();
        registry.add("spring.datasource.url", () -> dbUrl);

    }

    @BeforeAll
    static void setup() {
        POSTGRESQL_CONTAINER.start();
    }

    @Test
    void givenHotelIdAndDates_whenSearchIsMade_thenAvailableRoomsAreReturned() {
        HttpHeaders headers = HeaderGenerationHelper.getHeaders();

        String searchRequest = PayloadGenerationHelper.generatePayloadString("request/search/SearchRequest.json");

        ResponseEntity<SearchResponse> searchResponseEntity = restTemplate.exchange(SEARCH_URL, HttpMethod.POST,
                new HttpEntity<>(searchRequest, headers), SearchResponse.class);

        Assertions.assertEquals(HttpStatus.OK, searchResponseEntity.getStatusCode());

        var room1 = searchResponseEntity.getBody().getRoomPriceResponseList().get(0);
        Assertions.assertEquals(1, room1.getHotelId());
        Assertions.assertEquals(1, room1.getRoomId());
        Assertions.assertEquals("EUR", room1.getCurrency());

        var room2 = searchResponseEntity.getBody().getRoomPriceResponseList().get(1);
        Assertions.assertEquals(1, room2.getHotelId());
        Assertions.assertEquals(2, room2.getRoomId());
        Assertions.assertEquals("EUR", room2.getCurrency());
    }

    @Test
    void givenSearchRequest_whenDatesAreWrong_thenErrorIsThrown() {
        HttpHeaders headers = HeaderGenerationHelper.getHeaders();

        String searchRequest = PayloadGenerationHelper.generatePayloadString(
                "request/search/SearchRequestWrongDates.json");

        ResponseEntity<ErrorResponse> errorResponseEntity = restTemplate.exchange(SEARCH_URL, HttpMethod.POST,
                new HttpEntity<>(searchRequest, headers), ErrorResponse.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResponseEntity.getStatusCode());
        Assertions.assertEquals("Check in date cannot be earlier than check out!",
                errorResponseEntity.getBody().getError());
    }

    @Test
    void givenSearchRequest_whenThereIsNoAvailableRoom_thenEmptyResponseReturns() {
        HttpHeaders headers = HeaderGenerationHelper.getHeaders();

        String searchRequest = PayloadGenerationHelper.generatePayloadString(
                "request/search/SearchRequestNotAvailableDates.json");

        ResponseEntity<SearchResponse> searchResponseEntity = restTemplate.exchange(SEARCH_URL, HttpMethod.POST,
                new HttpEntity<>(searchRequest, headers), SearchResponse.class);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, searchResponseEntity.getStatusCode());
        Assertions.assertNull(searchResponseEntity.getBody());
    }


}
