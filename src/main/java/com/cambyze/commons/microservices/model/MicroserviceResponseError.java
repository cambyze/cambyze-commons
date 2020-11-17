package com.cambyze.commons.microservices.model;

/**
 * Error information in case of microservices errors
 * 
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 * 
 */
public class MicroserviceResponseError {
  String message;
  String exception;

  public MicroserviceResponseError() {
    super();
  }

  public MicroserviceResponseError(String message, String exception) {
    super();
    this.message = message;
    this.exception = exception;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getException() {
    return exception;
  }

  public void setException(String exception) {
    this.exception = exception;
  }

}
