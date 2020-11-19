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
public class RecordAlreadyExistsException extends RuntimeException {

  private static final long serialVersionUID = -2112387518349651893L;

  /**
   * Exception triggered when an entity already exists, typically an entity with the same reference
   * that an existing one, when the reference makes the entity unique
   * 
   * @param message text which will be displayed in the response body as the message attribute
   */
  public RecordAlreadyExistsException(String message) {
    super(message);
  }

  /**
   * Exception triggered when an entity already exists, typically an entity with the same reference
   * that an existing one, when the reference makes the entity unique
   * 
   * @param entity entity used to build the message
   */
  public RecordAlreadyExistsException(PersistEntity entity) {
    super("The " + entity.getEntityName() + " " + entity.getReference() + " already exists");
  }
}

