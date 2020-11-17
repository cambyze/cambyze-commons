package com.cambyze.commons.microservices.controller.impl;

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
import com.cambyze.commons.microservices.controller.MicroserviceControllerService;
import com.cambyze.commons.microservices.model.MicroserviceResponseBody;
import com.cambyze.commons.microservices.model.MicroserviceResponseError;
import com.cambyze.commons.microservices.web.exceptions.EntityAlreadyExistsException;
import com.cambyze.commons.microservices.web.exceptions.EntityMandatoryAttributeException;
import com.cambyze.commons.microservices.web.exceptions.EntityNotFoundException;

/**
 * Implementation of the service MicroserviceControllerService
 * 
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
      if (throwable instanceof EntityNotFoundException) {
        microserviceResponseBody.setStatus(HttpServletResponse.SC_NOT_FOUND);
      } else {
        if (throwable instanceof EntityMandatoryAttributeException
            || throwable instanceof EntityAlreadyExistsException) {
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

  public URI formatUriWithCorrectReference(URI uri, String endOfPath, String reference) {
    String newPath = uri.getPath();
    String searchString = endOfPath + "/";
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

}
