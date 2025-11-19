package com.banking.payment.orders.adapter.out.persistence.mapper;

import com.banking.payment.orders.adapter.out.persistence.entity.PaymentOrderEntity;
import com.banking.payment.orders.domain.model.DateInformation;
import com.banking.payment.orders.domain.model.Payee;
import com.banking.payment.orders.domain.model.Payer;
import com.banking.payment.orders.domain.model.PaymentDetails;
import com.banking.payment.orders.domain.model.PaymentOrderProcedure;
import com.banking.payment.orders.domain.model.PaymentOrderStatus;
import org.springframework.stereotype.Component;

/**
 * PaymentOrderPersistenceMapper - Mapper entre entidades de dominio y persistencia.
 */
@Component
public class PaymentOrderPersistenceMapper {

  /**
   * Convierte PaymentOrderProcedure de dominio a PaymentOrderEntity.
   *
   * @param domain entidad de dominio
   * @return entidad de persistencia
   */
  public PaymentOrderEntity toEntity(PaymentOrderProcedure domain) {
    return toEntity(domain, true);
  }

  /**
   * Convierte PaymentOrderProcedure de dominio a PaymentOrderEntity.
   *
   * @param domain entidad de dominio
   * @param isNew indica si es una nueva entidad (INSERT) o existente (UPDATE)
   * @return entidad de persistencia
   */
  public PaymentOrderEntity toEntity(PaymentOrderProcedure domain, boolean isNew) {
    PaymentOrderEntity entity = new PaymentOrderEntity();

    entity.setId(domain.getPaymentOrderProcedureInstanceReference());
    entity.setNew(isNew);
    entity.setStatus(domain.getPaymentOrderProcedureInstanceStatus().getValue());
    entity.setPaymentTransactionInitiatorReference(
        domain.getPaymentTransactionInitiatorReference());

    // Payer
    entity.setPayerReference(domain.getPayer().payerReference());
    entity.setPayerBankReference(domain.getPayer().payerBankReference());
    entity.setPayerProductInstanceReference(domain.getPayer().payerProductInstanceReference());

    // Payee
    entity.setPayeeReference(domain.getPayee().payeeReference());
    entity.setPayeeBankReference(domain.getPayee().payeeBankReference());
    entity.setPayeeProductInstanceReference(domain.getPayee().payeeProductInstanceReference());

    // Payment Details
    entity.setAmount(domain.getPaymentDetails().amount());
    entity.setCurrency(domain.getPaymentDetails().currency());
    entity.setPaymentMechanismType(domain.getPaymentDetails().paymentMechanismType());

    // Date Information
    entity.setDateType(domain.getDateInformation().dateType());
    entity.setDate(domain.getDateInformation().date());

    entity.setRemittanceInformation(domain.getRemittanceInformation());
    entity.setCreatedDateTime(domain.getCreatedDateTime());
    entity.setLastUpdateDateTime(domain.getLastUpdateDateTime());

    return entity;
  }

  /**
   * Convierte PaymentOrderEntity a PaymentOrderProcedure de dominio.
   *
   * @param entity entidad de persistencia
   * @return entidad de dominio
   */
  public PaymentOrderProcedure toDomain(PaymentOrderEntity entity) {
    Payer payer = new Payer(
        entity.getPayerReference(),
        entity.getPayerBankReference(),
        entity.getPayerProductInstanceReference()
    );

    Payee payee = new Payee(
        entity.getPayeeReference(),
        entity.getPayeeBankReference(),
        entity.getPayeeProductInstanceReference()
    );

    PaymentDetails paymentDetails = new PaymentDetails(
        entity.getAmount(),
        entity.getCurrency(),
        entity.getPaymentMechanismType()
    );

    DateInformation dateInformation = new DateInformation(
        entity.getDateType(),
        entity.getDate()
    );

    // Crear la instancia y luego usar reflexión para setear el ID correcto
    PaymentOrderProcedure procedure = PaymentOrderProcedure.initiate(
        entity.getPaymentTransactionInitiatorReference(),
        payer,
        payee,
        paymentDetails,
        dateInformation,
        entity.getRemittanceInformation()
    );

    // Usar reflexión para setear el ID de la base de datos
    try {
      var idField = PaymentOrderProcedure.class
          .getDeclaredField("paymentOrderProcedureInstanceReference");
      idField.setAccessible(true);
      idField.set(procedure, entity.getId());
      
      var createdField = PaymentOrderProcedure.class.getDeclaredField("createdDateTime");
      createdField.setAccessible(true);
      createdField.set(procedure, entity.getCreatedDateTime());
      
      var updatedField = PaymentOrderProcedure.class.getDeclaredField("lastUpdateDateTime");
      updatedField.setAccessible(true);
      updatedField.set(procedure, entity.getLastUpdateDateTime());
    } catch (Exception e) {
      throw new RuntimeException("Error setting ID from database", e);
    }

    // Actualizar el estado si es diferente de INITIATED
    PaymentOrderStatus status = PaymentOrderStatus.fromValue(entity.getStatus());
    if (status != PaymentOrderStatus.INITIATED) {
      procedure.updateStatus(status);
    }

    return procedure;
  }
}
