package com.cambyze.commons.microservices.model;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

/**
 * Response body for microservices
 * 
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
public class MicroserviceResponseBody {

  private int status;
  private String timestamp;
  private String message;
  private String error;
  private String exception;
  private URI path;

  private List<MicroserviceResponseError> errors;

  public MicroserviceResponseBody() {
    super();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss*SSSZZZZ");
    this.timestamp = dateFormat.format(Calendar.getInstance().getTime());
    this.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
  }


  public MicroserviceResponseBody(int status, String message, URI path, String error,
      String exception, List<MicroserviceResponseError> errors) {
    super();
    this.status = status;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss*SSSZZZZ");
    this.timestamp = dateFormat.format(Calendar.getInstance().getTime());
    this.message = message;
    this.path = path;
    this.error = error;
    this.exception = exception;
    this.errors = errors;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public URI getPath() {
    return path;
  }

  public void setPath(URI path) {
    this.path = path;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getError() {
    return error;
  }


  public void setError(String error) {
    this.error = error;
  }


  public String getException() {
    return exception;
  }


  public void setException(String exception) {
    this.exception = exception;
  }


  public List<MicroserviceResponseError> getErrors() {
    return errors;
  }

  public void setErrors(List<MicroserviceResponseError> errors) {
    this.errors = errors;
  }
}


