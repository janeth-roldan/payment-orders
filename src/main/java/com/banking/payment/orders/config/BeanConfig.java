package com.banking.payment.orders.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * BeanConfig - Configuración de beans de Spring.
 */
@Configuration
@ComponentScan(basePackages = "com.banking.payment.orders")
public class BeanConfig {
  // Los beans se registran automáticamente por @Component, @Service, etc.
}
