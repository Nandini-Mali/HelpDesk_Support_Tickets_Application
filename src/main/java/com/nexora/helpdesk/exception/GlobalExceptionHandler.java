package com.nexora.helpdesk.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

// Resource Not Found - 404
  @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
     
	  return buildResponse(HttpStatus.NOT_FOUND,ex.getMessage());
    }
 
//Duplicate Resource - 409
  @ExceptionHandler(DuplicateResourceException.class)
  public ResponseEntity<?> handleDuplicateResource(DuplicateResourceException ex) {

      return buildResponse(HttpStatus.CONFLICT,ex.getMessage());
  }
//Invalid Status Transition - 400
  @ExceptionHandler(InvalidStatusTransitionException.class)
  public ResponseEntity<?> handleInvalidStatusTransition(InvalidStatusTransitionException ex) {

      return buildResponse(HttpStatus.BAD_REQUEST,ex.getMessage());
  }

//Access Denied - 403
  @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
  public ResponseEntity<?> handleAccessDenied(Exception ex) {

      return buildResponse(HttpStatus.FORBIDDEN,"You do not have permission to perform this action.");
  }

//Invalid Credentials - 401 
@ExceptionHandler(InvalidCredentialsException.class)
public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException ex) {

   return buildResponse(HttpStatus.UNAUTHORIZED,ex.getMessage());
}
  
//Illegal Argument - 400
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {

      return buildResponse(HttpStatus.BAD_REQUEST,ex.getMessage());
  }

//Validation Errors - 400
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?>handleValidationErrors(MethodArgumentNotValidException ex) {

      Map<String, String> errors = new HashMap<>();

      ex.getBindingResult().getFieldErrors().forEach(error ->
              errors.put(error.getField(),error.getDefaultMessage()));

      Map<String, Object> response = new HashMap<>();
      response.put("status", 400);
      response.put("error", "Validation Failed");
      response.put("timestamp", LocalDateTime.now());
      response.put("messages", errors);

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }
//Common Response Builder
  private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status,String message) {

      Map<String, Object> response = new HashMap<>();

      response.put("timestamp", LocalDateTime.now());
      response.put("status", status.value());
      response.put("error", status.getReasonPhrase());
      response.put("message", message);

      return ResponseEntity.status(status).body(response);
  }
//Missing Request Parameter - 400
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<?> handleMissingParameter(
          MissingServletRequestParameterException ex) {

      return buildResponse(
              HttpStatus.BAD_REQUEST,
              ex.getParameterName() + " parameter is required");
  }
//Invalid Enum or Parameter Type - 400
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<?> handleTypeMismatch(
          MethodArgumentTypeMismatchException ex) {

      return buildResponse(
              HttpStatus.BAD_REQUEST,
              "Invalid value for parameter: " +ex.getName());
  }
  // Invalid JSON Request - 400
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> handleInvalidJson(
          HttpMessageNotReadableException ex) {

      return buildResponse(
              HttpStatus.BAD_REQUEST,
              "Invalid request body or JSON value");
  }

 // Generic Exception - 500
  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleGenericException(Exception ex) {

      return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,"An unexpected error occurred.");
  }
}
