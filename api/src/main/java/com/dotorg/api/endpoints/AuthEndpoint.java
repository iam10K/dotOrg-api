package com.dotorg.api.endpoints;

import com.dotorg.api.objects.Token;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.NotFoundException;
import com.google.firebase.auth.FirebaseAuth;

import java.util.logging.Logger;

import javax.inject.Named;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "auth",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "objects.api.dotorg.com",
                ownerName = "objects.api.dotorg.com",
                packagePath = ""
        )
)
public class AuthEndpoint {

    private static final Logger logger = Logger.getLogger(AuthEndpoint.class.getName());

    /**
     * Returns the {@Token token} from the corresponding ID.
     *
     * @param userId the user to generate token for
     * @return the token for the corresponding ID
     * @throws NotFoundException if there is no {@code User} with the provided ID.
     */
    @ApiMethod(
            name = "auth",
            path = "auth/{userId}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Token get(@Named("userId") String userId) throws NotFoundException {
        logger.info("Generating token for: " + userId);
        String token = "";
        try {
            token = FirebaseAuth.getInstance().createCustomToken(userId);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.getStackTrace();
            throw new NotFoundException("Invalid user has been supplied.");
        }

        if (token.equals("")) {
            throw new NotFoundException("Invalid user has been supplied.");
        }
        return new Token(token);
    }

}