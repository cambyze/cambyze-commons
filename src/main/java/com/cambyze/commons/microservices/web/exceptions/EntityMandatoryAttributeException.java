package com.cambyze.commons.microservices.web.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Standard exception
 * 
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EntityMandatoryAttributeException extends RuntimeException {

  private static final long serialVersionUID = 1207691880895021472L;

  /**
   * Exception triggered when a mandatory attribute is missing, typically the reference one which
   * makes the entity unique
   * 
   * @param message text which will be displayed in the response body as the message attribute
   */
  public EntityMandatoryAttributeException(String message) {
    super(message);
  }

}

