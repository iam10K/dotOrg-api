package com.dotorg.api.endpoints.v1;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;

/**
 * dotOrg-api
 * Date Created: 6/18/2016
 * |
 * Original Package: com.dotorg.api.endpoints
 * |
 * COPYRIGHT 2016
 */
@Api(
        name = "api",
        version = "v1",
        description = "Primary API methods",
        title = "dotOrg API",
        namespace = @ApiNamespace(
                ownerDomain = "objects.api.dotorg.com",
                ownerName = "objects.api.dotorg.com",
                packagePath = ""
        )
)
public class BaseEndpointV1 {
}
