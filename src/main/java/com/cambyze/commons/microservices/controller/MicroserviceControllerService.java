package com.cambyze.commons.microservices.controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import com.cambyze.commons.microservices.model.MicroserviceResponseBody;

/**
 * Service to be used to build standard Cambyze microservice controllers
 * 
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 *
 */
public interface MicroserviceControllerService {

  /**
   * Analyse exception in order to build a response body for the microservices with errors
   * 
   * @param path URI to be displayed in the response body as the path attribute
   * @param exception exception which will be analysed in order to build other response body
   *        attributes
   * @return the response body to be returned by microservices in case of errors
   */
  public ResponseEntity<Object> buildResponseException(URI path, Exception exception);

  /**
   * Recursively analyse the exception and cause in order to fill the microservices response body in
   * case of error
   * 
   * @param microserviceResponseBody response body which will be updated recursively by the
   *        exception analysis
   * @param throwable exception or its causes to be analysed
   * @param parent exception or cause used in the recursive calls. Not to be filled during the first
   *        call of this procedure
   */
  public void updateMicroserviceResponseBodyWithException(
      MicroserviceResponseBody microserviceResponseBody, Throwable throwable, Throwable parent);

}
