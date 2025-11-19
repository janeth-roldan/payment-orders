package com.banking.payment.orders.adapter.in.rest;

import com.banking.payment.orders.adapter.in.rest.api.PaymentOrdersApi;
import com.banking.payment.orders.adapter.in.rest.mapper.PaymentOrderRestMapper;
import com.banking.payment.orders.adapter.in.rest.model.InitiatePaymentOrderRequest;
import com.banking.payment.orders.adapter.in.rest.model.InitiatePaymentOrderResponse;
import com.banking.payment.orders.adapter.in.rest.model.PaymentOrderResponse;
import com.banking.payment.orders.adapter.in.rest.model.PaymentOrderStatusResponse;
import com.banking.payment.orders.domain.port.in.InitiatePaymentOrderUseCase;
import com.banking.payment.orders.domain.port.in.RetrievePaymentOrderStatusUseCase;
import com.banking.payment.orders.domain.port.in.RetrievePaymentOrderUseCase;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * PaymentOrderController - Controlador REST que implementa la API generada por OpenAPI.
 */
@RestController
public class PaymentOrderController implements PaymentOrdersApi {

  private final InitiatePaymentOrderUseCase initiatePaymentOrderUseCase;
  private final RetrievePaymentOrderUseCase retrievePaymentOrderUseCase;
  private final RetrievePaymentOrderStatusUseCase retrievePaymentOrderStatusUseCase;
  private final PaymentOrderRestMapper mapper;

  /**
   * Constructor con inyección de dependencias.
   */
  public PaymentOrderController(
      InitiatePaymentOrderUseCase initiatePaymentOrderUseCase,
      RetrievePaymentOrderUseCase retrievePaymentOrderUseCase,
      RetrievePaymentOrderStatusUseCase retrievePaymentOrderStatusUseCase,
      PaymentOrderRestMapper mapper) {
    this.initiatePaymentOrderUseCase = initiatePaymentOrderUseCase;
    this.retrievePaymentOrderUseCase = retrievePaymentOrderUseCase;
    this.retrievePaymentOrderStatusUseCase = retrievePaymentOrderStatusUseCase;
    this.mapper = mapper;
  }

  @Override
  public Mono<ResponseEntity<InitiatePaymentOrderResponse>> initiatePaymentOrder(
      Mono<InitiatePaymentOrderRequest> initiatePaymentOrderRequest,
      ServerWebExchange exchange) {

    return initiatePaymentOrderRequest
        .map(mapper::toCommand)
        .flatMap(initiatePaymentOrderUseCase::initiate)
        .map(mapper::toInitiateResponse)
        .map(response -> ResponseEntity
            .status(HttpStatus.CREATED)
            .header("Location", "/payment-initiation/payment-orders/"
                + response.getPaymentOrderProcedure().getPaymentOrderProcedureInstanceReference())
            .body(response));
  }

  @Override
  public Mono<ResponseEntity<PaymentOrderResponse>> retrievePaymentOrder(
      UUID paymentOrderId,
      ServerWebExchange exchange) {

    return Mono.just(paymentOrderId)
        .flatMap(retrievePaymentOrderUseCase::retrieve)
        .map(mapper::toPaymentOrderResponse)
        .map(ResponseEntity::ok);
  }

  @Override
  public Mono<ResponseEntity<PaymentOrderStatusResponse>> retrievePaymentOrderStatus(
      UUID paymentOrderId,
      ServerWebExchange exchange) {

    return Mono.just(paymentOrderId)
        .flatMap(retrievePaymentOrderStatusUseCase::retrieveStatus)
        .map(mapper::toStatusResponse)
        .map(ResponseEntity::ok);
  }
}
