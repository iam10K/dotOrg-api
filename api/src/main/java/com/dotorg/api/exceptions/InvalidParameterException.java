package com.dotorg.api.exceptions;

import com.google.api.server.spi.ServiceException;

/**
 * dotOrg-api
 * Date Created: 6/15/2016
 * |
 * Original Package: com.dotorg.api.exceptions
 * |
 * COPYRIGHT 2016
 */
public class InvalidParameterException extends ServiceException {
    public InvalidParameterException(int statusCode, String statusMessage) {
        super(statusCode, statusMessage);
    }

    public InvalidParameterException(String statusMessage) {
        super(400, statusMessage);
    }
}
