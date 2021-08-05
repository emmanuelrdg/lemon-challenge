package com.lemon.challenge.external;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.stereotype.Service;

@Service
public class FoassRestClientImp implements FoassRestClient{


    private static final String BASE_URL = "https://www.foaas.com";
    private static final String IDEA_PATH  = "/idea/{name}";

    // TODO: 05/08/2021 change default name for user login name
    private static final String DEFAULT_NAME  = "Sr";
    @Override
    public FoaasMessageResponse getIdeaMessage() {

            HttpResponse<FoaasMessageResponse> result = Unirest.get(BASE_URL+IDEA_PATH)
                    .header("Accept", "application/json")
                    .routeParam("name", DEFAULT_NAME)
                    .asObject(FoaasMessageResponse.class);

            return result.getBody();

    }
}
