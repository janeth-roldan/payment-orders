package com.banking.payment.orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Payment Orders Application - Main entry point.
 * Migración de servicio SOAP a REST API alineada con BIAN Payment Initiation.
 */
@SpringBootApplication
public class PaymentOrdersApplication {

  /**
   * Main method to start the Spring Boot application.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(PaymentOrdersApplication.class, args);
  }

}
