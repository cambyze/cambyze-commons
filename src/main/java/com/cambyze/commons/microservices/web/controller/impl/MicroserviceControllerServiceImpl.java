package com.cambyze.commons.microservices.web.controller.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.cambyze.commons.microservices.model.MicroserviceResponseBody;
import com.cambyze.commons.microservices.model.MicroserviceResponseError;
import com.cambyze.commons.microservices.model.PersistEntity;
import com.cambyze.commons.microservices.web.controller.MicroserviceControllerService;
import com.cambyze.commons.microservices.web.exceptions.EntityReferenceNotFoundException;
import com.cambyze.commons.microservices.web.exceptions.MandatoryAttributeException;
import com.cambyze.commons.microservices.web.exceptions.MandatoryReferenceException;
import com.cambyze.commons.microservices.web.exceptions.RecordAlreadyExistsException;
import com.cambyze.commons.microservices.web.exceptions.RecordNotFoundException;
import com.cambyze.commons.microservices.web.exceptions.RequestEntityNullException;

/**
 * Implementation of the service MicroserviceControllerService
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@Service
public class MicroserviceControllerServiceImpl implements MicroserviceControllerService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MicroserviceControllerServiceImpl.class);

  public MicroserviceControllerServiceImpl() {
    super();
  }

  public ResponseEntity<Object> buildResponseException(URI uri, Exception exception) {

    // Initialisation
    MicroserviceResponseBody microserviceResponseBody = new MicroserviceResponseBody();
    microserviceResponseBody.setUri(uri);
    // Default status
    microserviceResponseBody.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    ArrayList<MicroserviceResponseError> errors = new ArrayList<MicroserviceResponseError>();
    microserviceResponseBody.setErrors(errors);

    // Exception analysis
    updateMicroserviceResponseBodyWithException(microserviceResponseBody, exception, null);

    // Determine the error message according to the HTTP status
    switch (microserviceResponseBody.getStatus()) {
      case HttpServletResponse.SC_NOT_FOUND:
        microserviceResponseBody.setError(HttpStatus.NOT_FOUND.toString());
        break;
      case HttpServletResponse.SC_BAD_REQUEST:
        microserviceResponseBody.setError(HttpStatus.BAD_REQUEST.toString());
        break;
      case HttpServletResponse.SC_NO_CONTENT:
        microserviceResponseBody.setError(HttpStatus.NO_CONTENT.toString());
        break;
      default:
        microserviceResponseBody.setError(HttpStatus.INTERNAL_SERVER_ERROR.toString());
    }
    return ResponseEntity.status(microserviceResponseBody.getStatus())
        .body(microserviceResponseBody);
  }

  public void updateMicroserviceResponseBodyWithException(
      MicroserviceResponseBody microserviceResponseBody, Throwable throwable, Throwable parent) {
    if (throwable != null) {
      LOGGER.error(throwable.getMessage());

      // Force status according to Cambyze exceptions
      if (throwable instanceof EntityReferenceNotFoundException
          || throwable instanceof RecordNotFoundException) {
        microserviceResponseBody.setStatus(HttpServletResponse.SC_NOT_FOUND);
      } else {
        if (throwable instanceof MandatoryAttributeException
            || throwable instanceof RecordAlreadyExistsException
            || throwable instanceof MandatoryReferenceException
            || throwable instanceof RequestEntityNullException) {
          microserviceResponseBody.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
      }

      // First call
      if (parent == null) {
        microserviceResponseBody.setMessage(throwable.getMessage());
        microserviceResponseBody.setException(throwable.getClass().getName());
        microserviceResponseBody.getErrors().add(
            new MicroserviceResponseError(throwable.getMessage(), throwable.getClass().getName()));
      }
      // Recursive calls with exception different that the parent to prevent duplicated values
      else if (throwable.getClass() != null && throwable.getMessage() != null
          && parent.getClass() != null
          && !throwable.getClass().getName().equalsIgnoreCase(parent.getClass().getName())) {
        // Add new error
        microserviceResponseBody.getErrors().add(
            new MicroserviceResponseError(throwable.getMessage(), throwable.getClass().getName()));

        // Management of the Hibernate constraint violation
        if (throwable instanceof ConstraintViolationException) {
          ConstraintViolationException constraintViolationException =
              (ConstraintViolationException) throwable;
          Set<ConstraintViolation<?>> constraintViolations =
              constraintViolationException.getConstraintViolations();
          if (constraintViolations != null && !constraintViolations.isEmpty()) {
            for (ConstraintViolation<?> constraintViolation : constraintViolations) {
              if (constraintViolation != null) {
                LOGGER.error(constraintViolation.getMessage());

                // Data validation error = BAD_REQUEST
                // Construction of the final message
                if (constraintViolation.getPropertyPath() != null
                    && constraintViolation.getMessage() != null)
                  microserviceResponseBody.setMessage("'" + constraintViolation.getPropertyPath()
                      + "': " + constraintViolation.getMessage().toString());
                microserviceResponseBody.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                microserviceResponseBody.setException(throwable.getClass().getName());
              }
            }
          }
        }
      }

      // Recursive call
      updateMicroserviceResponseBodyWithException(microserviceResponseBody, throwable.getCause(),
          throwable);
    }
  }

  public URI formatUriWithCorrectReference(URI uri, String path, String reference) {
    String newPath = uri.getPath();
    String searchString = path + "/";
    int i = newPath.indexOf(searchString);
    if (i >= 0) {
      newPath = newPath.substring(0, i + searchString.length()) + reference;
      try {
        return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), newPath,
            uri.getQuery(), uri.getFragment());
      } catch (URISyntaxException e) {
        LOGGER.error(e.getMessage());
        return uri;
      }
    } else {
      return uri;
    }
  }

  public void prepareSearchingEntity(PersistEntity searchEntity) throws RuntimeException {
    if (searchEntity != null) {
      String reference = searchEntity.getReference();
      if (reference != null && !reference.isBlank()) {
        searchEntity.setReference(reference.toUpperCase().trim());
      } else {
        throw new MandatoryReferenceException(searchEntity);
      }
    } else {
      throw new RequestEntityNullException();
    }
  }

  public void prepareSendingEntity(PersistEntity entity, PersistEntity searchEntity)
      throws RuntimeException {
    if (entity == null) {
      throw new EntityReferenceNotFoundException(searchEntity);
    } else {
      LOGGER.debug(entity.getEntityName() + " reference:" + entity.getReference() + " = " + entity);
    }
  }

  public URI initURI() {
    // Temporary URI
    return ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
  }

  public URI createTargetURI(PersistEntity requestEntity, String path) {
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{reference}")
        .buildAndExpand(requestEntity.getReference()).toUri();
    uri = formatUriWithCorrectReference(uri, path, requestEntity.getReference());
    return uri;
  }


  public ResponseEntity<Object> prepareRequestEntityToPersist(String reference,
      PersistEntity requestEntity, int operation) {
    try {
      // verification of the request body, except in case of suppression
      if (requestEntity != null || operation == OPERATION_SUPPRESSION) {

        // In case of creation the reference is within the request entity else it is within the path
        if (operation == OPERATION_CREATION) {
          reference = requestEntity.getReference();
        }

        // verification of the reference
        if (reference != null && !reference.isBlank()) {
          // the entity reference is an uppercase code
          requestEntity.setReference(reference.toUpperCase().trim());
          // no error then returns null
          return null;
        } else {
          throw new MandatoryReferenceException(requestEntity);
        }
      } else {
        throw new RequestEntityNullException();
      }
    } catch (Exception ex) {
      return buildResponseException(initURI(), ex);
    }
  }

  public ResponseEntity<Object> prepareEntityForUpdate(PersistEntity requestEntity,
      PersistEntity existingEntity, URI uri, int operation) {
    try {
      if (operation != OPERATION_CREATION || existingEntity == null) {
        if ((operation == OPERATION_CREATION || existingEntity != null) && requestEntity != null) {

          if (operation != OPERATION_CREATION) {
            requestEntity.setId(existingEntity.getId());
            requestEntity.setReference(existingEntity.getReference());
          }

          String message;
          switch (operation) {
            case OPERATION_CREATION:
              message = "Creation of the " + requestEntity.getEntityName() + " "
                  + requestEntity.getReference() + " with values = " + requestEntity;
              break;
            case OPERATION_SUPPRESSION:
              message = "Suppression of the " + existingEntity.getEntityName() + " "
                  + existingEntity.getReference();
              break;
            case OPERATION_FULL_UPDATE:
              message = "Full update of the " + requestEntity.getEntityName() + " "
                  + requestEntity.getReference() + " with values = " + requestEntity;
              break;
            case OPERATION_PARTIAL_UPDATE:
              message = "Partial update of the " + existingEntity.getEntityName() + " "
                  + existingEntity.getReference() + " with values = " + requestEntity;
              break;
            default:
              message = "Operation on the " + existingEntity.getEntityName() + " "
                  + existingEntity.getReference() + " with values = " + requestEntity;
          }
          LOGGER.info(message);
          // No error then returns null
          return null;
        } else {
          throw new EntityReferenceNotFoundException(requestEntity);
        }
      } else {
        throw new RecordAlreadyExistsException(existingEntity);
      }
    } catch (Exception ex) {
      return buildResponseException(uri, ex);
    }
  }

  public ResponseEntity<Object> createResponseBodyForUpdateSuccessful(PersistEntity entity, URI uri,
      int operation) {
    String message;
    switch (operation) {
      case OPERATION_CREATION:
        message = "Creation of the " + entity.getEntityName() + " " + entity.getReference()
            + " successful";
        break;
      case OPERATION_SUPPRESSION:
        message = "Suppression of the " + entity.getEntityName() + " " + entity.getReference()
            + " successful";
        break;
      case OPERATION_FULL_UPDATE:
        message = "Full update of the " + entity.getEntityName() + " " + entity.getReference()
            + " successful";
        break;
      case OPERATION_PARTIAL_UPDATE:
        message = "Partial update of the " + entity.getEntityName() + " " + entity.getReference()
            + " successful";
        break;
      default:
        message = "Operation on the " + entity.getEntityName() + " " + entity.getReference()
            + " successful";
    }
    MicroserviceResponseBody body =
        new MicroserviceResponseBody(HttpServletResponse.SC_OK, message, uri, null, null, null);
    return ResponseEntity.ok().body(body);
  }

}
