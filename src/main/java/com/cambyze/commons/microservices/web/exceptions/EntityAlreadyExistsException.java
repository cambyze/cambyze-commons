package com.cambyze.commons.microservices.web.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Standard exception
 * 
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EntityAlreadyExistsException extends RuntimeException {

  private static final long serialVersionUID = -2112387518349651893L;

  /**
   * Exception triggered when an entity already exists, typically an entity with the same reference
   * that an existing one, when the reference makes the entity unique
   * 
   * @param message text which will be displayed in the response body as the message attribute
   */
  public EntityAlreadyExistsException(String message) {
    super(message);
  }

}

