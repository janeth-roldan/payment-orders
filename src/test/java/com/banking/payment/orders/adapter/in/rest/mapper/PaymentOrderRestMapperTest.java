package com.banking.payment.orders.adapter.in.rest.mapper;

import com.banking.payment.orders.adapter.in.rest.model.InitiatePaymentOrderRequest;
import com.banking.payment.orders.adapter.in.rest.model.InitiatePaymentOrderResponse;
import com.banking.payment.orders.adapter.in.rest.model.PaymentOrderResponse;
import com.banking.payment.orders.adapter.in.rest.model.PaymentOrderStatusResponse;
import com.banking.payment.orders.domain.model.DateInformation;
import com.banking.payment.orders.domain.model.Payee;
import com.banking.payment.orders.domain.model.Payer;
import com.banking.payment.orders.domain.model.PaymentDetails;
import com.banking.payment.orders.domain.model.PaymentOrderProcedure;
import com.banking.payment.orders.domain.port.in.InitiatePaymentOrderUseCase.InitiatePaymentOrderCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PaymentOrderRestMapperTest - Tests unitarios para PaymentOrderRestMapper.
 */
class PaymentOrderRestMapperTest {

  private PaymentOrderRestMapper mapper;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    mapper = new PaymentOrderRestMapper();
    objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
  }

  @Test
  @DisplayName("Debe mapear request JSON a command correctamente")
  void shouldMapRequestJsonToCommandWhenValidRequestProvided() throws Exception {
    // Given
    String requestJson = """
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

    InitiatePaymentOrderRequest request = objectMapper.readValue(
        requestJson, InitiatePaymentOrderRequest.class);

    // When
    InitiatePaymentOrderCommand command = mapper.toCommand(request);

    // Then
    assertThat(command).isNotNull();
    assertThat(command.paymentTransactionInitiatorReference()).isEqualTo("TXN-12345");
    assertThat(command.payerReference()).isEqualTo("John Doe");
    assertThat(command.payeeReference()).isEqualTo("Jane Smith");
    assertThat(command.amount()).isEqualByComparingTo(new BigDecimal("150.00"));
    assertThat(command.currency()).isEqualTo("EUR");
  }

  @Test
  @DisplayName("Debe mapear domain a InitiateResponse correctamente")
  void shouldMapDomainToInitiateResponseWhenDomainProvided() {
    // Given
    PaymentOrderProcedure domain = createDomainObject();

    // When
    InitiatePaymentOrderResponse response = mapper.toInitiateResponse(domain);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getPaymentOrderProcedure()).isNotNull();
    assertThat(response.getPaymentOrderProcedure().getPaymentOrderProcedureInstanceReference())
        .isEqualTo(domain.getPaymentOrderProcedureInstanceReference());
    assertThat(response.getMetadata()).isNotNull();
    assertThat(response.getMetadata().getCreatedDateTime()).isNotNull();
    assertThat(response.getLinks()).isNotNull();
    assertThat(response.getLinks().getSelf()).isNotNull();
    assertThat(response.getLinks().getStatus()).isNotNull();
  }

  @Test
  @DisplayName("Debe mapear domain a PaymentOrderResponse correctamente")
  void shouldMapDomainToPaymentOrderResponseWhenDomainProvided() {
    // Given
    PaymentOrderProcedure domain = createDomainObject();

    // When
    PaymentOrderResponse response = mapper.toPaymentOrderResponse(domain);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getPaymentOrderProcedure()).isNotNull();
    assertThat(response.getPaymentOrderProcedure().getPaymentOrderProcedureInstanceReference())
        .isEqualTo(domain.getPaymentOrderProcedureInstanceReference());
    assertThat(response.getPaymentOrderProcedure().getPayer()).isNotNull();
    assertThat(response.getPaymentOrderProcedure().getPayee()).isNotNull();
    assertThat(response.getPaymentOrderProcedure().getPaymentDetails()).isNotNull();
    assertThat(response.getPaymentOrderProcedure().getDateInformation()).isNotNull();
    assertThat(response.getMetadata()).isNotNull();
    assertThat(response.getLinks()).isNotNull();
  }

  @Test
  @DisplayName("Debe mapear domain a StatusResponse correctamente")
  void shouldMapDomainToStatusResponseWhenDomainProvided() {
    // Given
    PaymentOrderProcedure domain = createDomainObject();

    // When
    PaymentOrderStatusResponse response = mapper.toStatusResponse(domain);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getPaymentOrderProcedure()).isNotNull();
    assertThat(response.getPaymentOrderProcedure().getPaymentOrderProcedureInstanceReference())
        .isEqualTo(domain.getPaymentOrderProcedureInstanceReference());
    assertThat(response.getPaymentOrderProcedure().getPaymentOrderProcedureInstanceStatus())
        .isNotNull();
    assertThat(response.getMetadata()).isNotNull();
    assertThat(response.getMetadata().getLastUpdateDateTime()).isNotNull();
  }

  private PaymentOrderProcedure createDomainObject() {
    return PaymentOrderProcedure.initiate(
        "TXN-12345",
        new Payer("John Doe", "BANK001", "ES9121000418450200051332"),
        new Payee("Jane Smith", "BANK002", "ES9121000418450200051333"),
        new PaymentDetails(new BigDecimal("150.00"), "EUR", "CreditTransfer"),
        new DateInformation("RequestedExecutionDate", LocalDate.now().plusDays(1)),
        "Payment for invoice INV-001"
    );
  }
}
