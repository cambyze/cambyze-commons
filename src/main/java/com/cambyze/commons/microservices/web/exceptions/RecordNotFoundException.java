package com.cambyze.commons.microservices.web.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Standard exception
 * 
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecordNotFoundException extends RuntimeException {

  private static final long serialVersionUID = -2136526719851089161L;

  /**
   * Exception triggered when a microservice does not find the requested entity
   * 
   * @param message text which will be displayed in the response body as the message attribute
   */
  public RecordNotFoundException(String message) {
    super(message);
  }

}

