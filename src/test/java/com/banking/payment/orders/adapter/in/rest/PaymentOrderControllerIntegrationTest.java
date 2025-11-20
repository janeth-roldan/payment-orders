package com.banking.payment.orders.adapter.in.rest;

import com.banking.payment.orders.adapter.in.rest.model.InitiatePaymentOrderResponse;
import com.banking.payment.orders.adapter.in.rest.model.PaymentOrderResponse;
import com.banking.payment.orders.adapter.in.rest.model.PaymentOrderStatusResponse;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PaymentOrderControllerIntegrationTest - Tests de integración para endpoints REST.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class PaymentOrderControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .withInitScript("schema.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () ->
                "r2dbc:postgresql://" + postgres.getHost() + ":" + postgres.getFirstMappedPort()
                        + "/" + postgres.getDatabaseName());
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
    }

    @Autowired
    private WebTestClient webTestClient;

    private String validRequestJson;

    @BeforeEach
    void setUp() {
        validRequestJson = """
                {
                  "paymentOrderProcedure": {
                    "paymentTransactionInitiatorReference": "TXN-12345",
                    "payer": {
                      "payerReference": "John Doe",
                      "payerBankReference": "BANK001",
                      "payerProductInstanceReference": "ES9121000418450200051332"
                    },
                    "payee": {
                      "payeeReference": "Jane Smith",
                      "payeeBankReference": "BANK002",
                      "payeeProductInstanceReference": "ES9121000418450200051333"
                    },
                    "paymentDetails": {
                      "amount": 150.00,
                      "currency": "EUR",
                      "paymentMechanismType": "CreditTransfer"
                    },
                    "dateInformation": {
                      "dateType": "RequestedExecutionDate",
                      "date": "2025-11-20"
                    },
                    "remittanceInformation": "Payment for invoice INV-001"
                  }
                }
                """;
    }

    @Test
    @DisplayName("POST /payment-initiation/payment-orders - Debe crear orden de pago exitosamente")
    void testInitiatePaymentOrder_Success() {
        // When
        InitiatePaymentOrderResponse response = webTestClient.post()
                .uri("/payment-initiation/payment-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequestJson)
                .exchange()
                // Then
                .expectStatus().isCreated()
                .expectHeader().exists("Location")
                .expectBody(InitiatePaymentOrderResponse.class)
                .returnResult()
                .getResponseBody();

        // Verify
        assertThat(response).isNotNull();
        assertThat(response.getPaymentOrderProcedure()).isNotNull();
        assertThat(response.getPaymentOrderProcedure().getPaymentOrderProcedureInstanceReference())
                .isNotNull();
        assertThat(response.getPaymentOrderProcedure().getPaymentOrderProcedureInstanceStatus())
                .isNotNull();
        assertThat(response.getMetadata()).isNotNull();
        assertThat(response.getMetadata().getCreatedDateTime()).isNotNull();
        assertThat(response.getLinks()).isNotNull();
        assertThat(response.getLinks().getSelf()).isNotNull();
        assertThat(response.getLinks().getStatus()).isNotNull();
    }

    @Test
    @DisplayName("GET /payment-initiation/payment-orders/{id} - Debe recuperar orden existente")
    void testRetrievePaymentOrder_Success() {
        // Given - Crear orden primero
        InitiatePaymentOrderResponse createdOrder = webTestClient.post()
                .uri("/payment-initiation/payment-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequestJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(InitiatePaymentOrderResponse.class)
                .returnResult()
                .getResponseBody();

        UUID orderId = createdOrder.getPaymentOrderProcedure()
                .getPaymentOrderProcedureInstanceReference();

        // When
        PaymentOrderResponse response = webTestClient.get()
                .uri("/payment-initiation/payment-orders/{id}", orderId)
                .exchange()
                // Then
                .expectStatus().isOk()
                .expectBody(PaymentOrderResponse.class)
                .returnResult()
                .getResponseBody();

        // Verify
        assertThat(response).isNotNull();
        assertThat(response.getPaymentOrderProcedure()).isNotNull();
        assertThat(response.getPaymentOrderProcedure().getPaymentOrderProcedureInstanceReference())
                .isEqualTo(orderId);
        assertThat(response.getPaymentOrderProcedure().getPayer()).isNotNull();
        assertThat(response.getPaymentOrderProcedure().getPayee()).isNotNull();
        assertThat(response.getPaymentOrderProcedure().getPaymentDetails()).isNotNull();
        assertThat(response.getPaymentOrderProcedure().getDateInformation()).isNotNull();
        assertThat(response.getMetadata()).isNotNull();
        assertThat(response.getLinks()).isNotNull();
    }

    @Test
    @DisplayName("GET /payment-initiation/payment-orders/{id} - Debe retornar 404 si no existe")
    void testRetrievePaymentOrder_NotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When/Then
        webTestClient.get()
                .uri("/payment-initiation/payment-orders/{id}", nonExistentId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("GET /payment-initiation/payment-orders/{id}/status - Debe recuperar estado")
    void testRetrievePaymentOrderStatus_Success() {
        // Given - Crear orden primero
        InitiatePaymentOrderResponse createdOrder = webTestClient.post()
                .uri("/payment-initiation/payment-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequestJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(InitiatePaymentOrderResponse.class)
                .returnResult()
                .getResponseBody();

        UUID orderId = createdOrder.getPaymentOrderProcedure()
                .getPaymentOrderProcedureInstanceReference();

        // When
        PaymentOrderStatusResponse response = webTestClient.get()
                .uri("/payment-initiation/payment-orders/{id}/status", orderId)
                .exchange()
                // Then
                .expectStatus().isOk()
                .expectBody(PaymentOrderStatusResponse.class)
                .returnResult()
                .getResponseBody();

        // Verify
        assertThat(response).isNotNull();
        assertThat(response.getPaymentOrderProcedure()).isNotNull();
        assertThat(response.getPaymentOrderProcedure().getPaymentOrderProcedureInstanceReference())
                .isEqualTo(orderId);
        assertThat(response.getPaymentOrderProcedure().getPaymentOrderProcedureInstanceStatus())
                .isNotNull();
        assertThat(response.getMetadata()).isNotNull();
        assertThat(response.getMetadata().getLastUpdateDateTime()).isNotNull();
    }

    @Test
    @DisplayName("GET /payment-initiation/payment-orders/{id}/status - Debe retornar 404 si no existe")
    void testRetrievePaymentOrderStatus_NotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When/Then
        webTestClient.get()
                .uri("/payment-initiation/payment-orders/{id}/status", nonExistentId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("POST /payment-initiation/payment-orders - Debe validar campos requeridos")
    void testInitiatePaymentOrder_ValidationError() {
        // Given - Request inválido (sin campos requeridos)
        String invalidRequestJson = "{\"paymentOrderProcedure\":{}}";

        // When/Then
        webTestClient.post()
                .uri("/payment-initiation/payment-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequestJson)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
