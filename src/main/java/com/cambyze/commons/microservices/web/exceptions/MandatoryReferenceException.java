package com.cambyze.commons.microservices.web.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.cambyze.commons.microservices.model.PersistEntity;

/**
 * Standard exception for microservices
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MandatoryReferenceException extends RuntimeException {

  private static final long serialVersionUID = 1207691880895021472L;

  /**
   * Exception triggered when the reference of the entity is missing, which makes the entity unique
   * 
   * @param message text which will be displayed in the response body as the message attribute
   */
  public MandatoryReferenceException(String message) {
    super(message);
  }

  /**
   * Exception triggered when the reference of the entity is missing, which makes the entity unique
   * 
   * @param entity entity used to build the message
   */
  public MandatoryReferenceException(PersistEntity entity) {
    super("The reference of the " + entity.getEntityName() + " is missing");
  }

}

