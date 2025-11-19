package com.banking.payment.orders.adapter.in.rest.mapper;

import com.banking.payment.orders.adapter.in.rest.model.DateInformation.DateTypeEnum;
import com.banking.payment.orders.adapter.in.rest.model.InitiatePaymentOrderRequest;
import com.banking.payment.orders.adapter.in.rest.model.InitiatePaymentOrderResponse;
import com.banking.payment.orders.adapter.in.rest.model.Links;
import com.banking.payment.orders.adapter.in.rest.model.LinksSelf;
import com.banking.payment.orders.adapter.in.rest.model.LinksStatus;
import com.banking.payment.orders.adapter.in.rest.model.Metadata;
import com.banking.payment.orders.adapter.in.rest.model.MetadataFull;
import com.banking.payment.orders.adapter.in.rest.model.PaymentOrderProcedureFull;
import com.banking.payment.orders.adapter.in.rest.model.PaymentOrderProcedureResponse;
import com.banking.payment.orders.adapter.in.rest.model.PaymentOrderResponse;
import com.banking.payment.orders.adapter.in.rest.model.PaymentOrderStatus;
import com.banking.payment.orders.adapter.in.rest.model.PaymentOrderStatusResponse;
import com.banking.payment.orders.adapter.in.rest.model.PaymentOrderStatusResponseMetadata;
import com.banking.payment.orders.adapter.in.rest.model.PaymentOrderStatusResponsePaymentOrderProcedure;
import com.banking.payment.orders.domain.model.PaymentOrderProcedure;
import com.banking.payment.orders.domain.port.in.InitiatePaymentOrderUseCase.InitiatePaymentOrderCommand;
import java.math.BigDecimal;
import java.net.URI;
import org.springframework.stereotype.Component;

/**
 * PaymentOrderRestMapper - Mapper entre DTOs REST y entidades de dominio.
 */
@Component
public class PaymentOrderRestMapper {

  /**
   * Convierte InitiatePaymentOrderRequest a Command.
   */
  public InitiatePaymentOrderCommand toCommand(InitiatePaymentOrderRequest request) {
    var procedure = request.getPaymentOrderProcedure();
    var payer = procedure.getPayer();
    var payee = procedure.getPayee();
    var paymentDetails = procedure.getPaymentDetails();
    var dateInfo = procedure.getDateInformation();

    return new InitiatePaymentOrderCommand(
        procedure.getPaymentTransactionInitiatorReference(),
        payer.getPayerReference(),
        payer.getPayerBankReference(),
        payer.getPayerProductInstanceReference(),
        payee.getPayeeReference(),
        payee.getPayeeBankReference(),
        payee.getPayeeProductInstanceReference(),
        BigDecimal.valueOf(paymentDetails.getAmount()),
        paymentDetails.getCurrency().getValue(),
        paymentDetails.getPaymentMechanismType() != null
            ? paymentDetails.getPaymentMechanismType().getValue() : null,
        dateInfo.getDateType().getValue(),
        dateInfo.getDate(),
        procedure.getRemittanceInformation()
    );
  }

  /**
   * Convierte PaymentOrderProcedure a InitiatePaymentOrderResponse.
   */
  public InitiatePaymentOrderResponse toInitiateResponse(PaymentOrderProcedure domain) {
    InitiatePaymentOrderResponse response = new InitiatePaymentOrderResponse();

    PaymentOrderProcedureResponse procedureResponse = new PaymentOrderProcedureResponse();
    procedureResponse.setPaymentOrderProcedureInstanceReference(
        domain.getPaymentOrderProcedureInstanceReference());
    procedureResponse.setPaymentOrderProcedureInstanceStatus(
        PaymentOrderStatus.fromValue(domain.getPaymentOrderProcedureInstanceStatus().getValue()));

    Metadata metadata = new Metadata();
    metadata.setCreatedDateTime(domain.getCreatedDateTime());

    Links links = new Links();
    LinksSelf self = new LinksSelf();
    self.setHref(URI.create("/payment-initiation/payment-orders/"
        + domain.getPaymentOrderProcedureInstanceReference()));
    links.setSelf(self);

    LinksStatus status = new LinksStatus();
    status.setHref(URI.create("/payment-initiation/payment-orders/"
        + domain.getPaymentOrderProcedureInstanceReference() + "/status"));
    links.setStatus(status);

    response.setPaymentOrderProcedure(procedureResponse);
    response.setMetadata(metadata);
    response.setLinks(links);

    return response;
  }

  /**
   * Convierte PaymentOrderProcedure a PaymentOrderResponse.
   */
  public PaymentOrderResponse toPaymentOrderResponse(PaymentOrderProcedure domain) {
    PaymentOrderResponse response = new PaymentOrderResponse();

    PaymentOrderProcedureFull procedureFull = new PaymentOrderProcedureFull();
    procedureFull.setPaymentOrderProcedureInstanceReference(
        domain.getPaymentOrderProcedureInstanceReference());
    procedureFull.setPaymentTransactionInitiatorReference(
        domain.getPaymentTransactionInitiatorReference());

    // Payer
    com.banking.payment.orders.adapter.in.rest.model.Payer payerDto =
        new com.banking.payment.orders.adapter.in.rest.model.Payer();
    payerDto.setPayerReference(domain.getPayer().payerReference());
    payerDto.setPayerBankReference(domain.getPayer().payerBankReference());
    payerDto.setPayerProductInstanceReference(domain.getPayer().payerProductInstanceReference());
    procedureFull.setPayer(payerDto);

    // Payee
    com.banking.payment.orders.adapter.in.rest.model.Payee payeeDto =
        new com.banking.payment.orders.adapter.in.rest.model.Payee();
    payeeDto.setPayeeReference(domain.getPayee().payeeReference());
    payeeDto.setPayeeBankReference(domain.getPayee().payeeBankReference());
    payeeDto.setPayeeProductInstanceReference(domain.getPayee().payeeProductInstanceReference());
    procedureFull.setPayee(payeeDto);

    // Payment Details
    com.banking.payment.orders.adapter.in.rest.model.PaymentDetails paymentDetailsDto =
        new com.banking.payment.orders.adapter.in.rest.model.PaymentDetails();
    paymentDetailsDto.setAmount(domain.getPaymentDetails().amount().doubleValue());
    paymentDetailsDto.setCurrency(
        com.banking.payment.orders.adapter.in.rest.model.PaymentDetails.CurrencyEnum.fromValue(
            domain.getPaymentDetails().currency()));
    if (domain.getPaymentDetails().paymentMechanismType() != null) {
      paymentDetailsDto.setPaymentMechanismType(
          com.banking.payment.orders.adapter.in.rest.model.PaymentDetails
              .PaymentMechanismTypeEnum.fromValue(
                  domain.getPaymentDetails().paymentMechanismType()));
    }
    procedureFull.setPaymentDetails(paymentDetailsDto);

    // Date Information
    com.banking.payment.orders.adapter.in.rest.model.DateInformation dateInfoDto =
        new com.banking.payment.orders.adapter.in.rest.model.DateInformation();
    dateInfoDto.setDateType(DateTypeEnum.fromValue(domain.getDateInformation().dateType()));
    dateInfoDto.setDate(domain.getDateInformation().date());
    procedureFull.setDateInformation(dateInfoDto);

    procedureFull.setRemittanceInformation(domain.getRemittanceInformation());
    procedureFull.setPaymentOrderProcedureInstanceStatus(
        PaymentOrderStatus.fromValue(domain.getPaymentOrderProcedureInstanceStatus().getValue()));

    MetadataFull metadata = new MetadataFull();
    metadata.setCreatedDateTime(domain.getCreatedDateTime());
    metadata.setLastUpdateDateTime(domain.getLastUpdateDateTime());

    Links links = new Links();
    LinksSelf self = new LinksSelf();
    self.setHref(URI.create("/payment-initiation/payment-orders/"
        + domain.getPaymentOrderProcedureInstanceReference()));
    links.setSelf(self);

    LinksStatus status = new LinksStatus();
    status.setHref(URI.create("/payment-initiation/payment-orders/"
        + domain.getPaymentOrderProcedureInstanceReference() + "/status"));
    links.setStatus(status);

    response.setPaymentOrderProcedure(procedureFull);
    response.setMetadata(metadata);
    response.setLinks(links);

    return response;
  }

  /**
   * Convierte PaymentOrderProcedure a PaymentOrderStatusResponse.
   */
  public PaymentOrderStatusResponse toStatusResponse(PaymentOrderProcedure domain) {
    PaymentOrderStatusResponse response = new PaymentOrderStatusResponse();

    PaymentOrderStatusResponsePaymentOrderProcedure procedure =
        new PaymentOrderStatusResponsePaymentOrderProcedure();
    procedure.setPaymentOrderProcedureInstanceReference(
        domain.getPaymentOrderProcedureInstanceReference());
    procedure.setPaymentOrderProcedureInstanceStatus(
        PaymentOrderStatus.fromValue(domain.getPaymentOrderProcedureInstanceStatus().getValue()));

    PaymentOrderStatusResponseMetadata metadata = new PaymentOrderStatusResponseMetadata();
    metadata.setLastUpdateDateTime(domain.getLastUpdateDateTime());

    response.setPaymentOrderProcedure(procedure);
    response.setMetadata(metadata);

    return response;
  }
}
