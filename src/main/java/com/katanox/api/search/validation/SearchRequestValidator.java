package com.katanox.api.search.validation;

import com.katanox.api.search.exception.DatesNotValidException;
import com.katanox.api.search.payload.SearchRequest;
import org.springframework.stereotype.Component;

@Component
public class SearchRequestValidator {

    public void validate(SearchRequest searchRequest) throws DatesNotValidException {
        if (searchRequest.getCheckin().isAfter(searchRequest.getCheckout())) {
            throw new DatesNotValidException("Check in date cannot be earlier than check out!");
        }
    }
}
