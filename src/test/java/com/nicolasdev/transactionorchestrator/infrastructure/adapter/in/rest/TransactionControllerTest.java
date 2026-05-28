package com.nicolasdev.transactionorchestrator.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicolasdev.transactionorchestrator.domain.exception.TransactionNotFoundException;
import com.nicolasdev.transactionorchestrator.domain.model.Customer;
import com.nicolasdev.transactionorchestrator.domain.model.DocumentType;
import com.nicolasdev.transactionorchestrator.domain.model.Transaction;
import com.nicolasdev.transactionorchestrator.domain.model.TransactionStatus;
import com.nicolasdev.transactionorchestrator.domain.ports.in.CreateTransactionUseCase;
import com.nicolasdev.transactionorchestrator.domain.ports.in.GetTransactionUseCase;
import com.nicolasdev.transactionorchestrator.infrastructure.adapter.in.rest.dto.request.CustomerRequest;
import com.nicolasdev.transactionorchestrator.infrastructure.adapter.in.rest.dto.request.TransactionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateTransactionUseCase createTransactionUseCase;

    @MockitoBean
    private GetTransactionUseCase getTransactionUseCase;

    @Test
    void create_shouldReturn201_whenRequestIsValid() throws Exception {
        Transaction result = buildTransaction();
        when(createTransactionUseCase.create(any())).thenReturn(result);

        mockMvc.perform(post("/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.data.transaction_id").value(result.getId().toString()))
                .andExpect(jsonPath("$.data.currency").value("COP"));
    }

    @Test
    void create_shouldReturn400_whenRequiredFieldIsMissing() throws Exception {
        TransactionRequest request = buildRequest();
        request.setClientTransactionId(null);

        mockMvc.perform(post("/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("001"));
    }

    @Test
    void getById_shouldReturn200_whenTransactionExists() throws Exception {
        Transaction result = buildTransaction();
        when(getTransactionUseCase.getById(any())).thenReturn(result);

        mockMvc.perform(get("/v1/transactions/{id}", result.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.data.transaction_id").value(result.getId().toString()));
    }

    @Test
    void getById_shouldReturn404_whenTransactionNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(getTransactionUseCase.getById(any())).thenThrow(new TransactionNotFoundException(id));

        mockMvc.perform(get("/v1/transactions/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"));
    }

    private Transaction buildTransaction() {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .clientTransactionId("CLIENT-001")
                .amount(1_000_000L)
                .currency("COP")
                .country("CO")
                .paymentMethodId("PSE")
                .webhookUrl("https://example.com/webhook")
                .redirectUrl("https://example.com/redirect")
                .status(TransactionStatus.APPROVED)
                .processedAt(LocalDateTime.now())
                .customer(Customer.builder()
                        .documentType(DocumentType.CC)
                        .documentNumber("123456789")
                        .email("juan@example.com")
                        .firstName("Juan")
                        .lastName("Pérez")
                        .build())
                .build();
    }

    private TransactionRequest buildRequest() {
        CustomerRequest customer = new CustomerRequest();
        customer.setDocumentType(DocumentType.CC);
        customer.setDocumentNumber("123456789");
        customer.setCountryCode("+57");
        customer.setPhone("3001234567");
        customer.setEmail("juan@example.com");
        customer.setFirstName("Juan");
        customer.setLastName("Pérez");

        TransactionRequest request = new TransactionRequest();
        request.setClientTransactionId("CLIENT-001");
        request.setAmount(1_000_000L);
        request.setCurrency("COP");
        request.setCountry("CO");
        request.setPaymentMethodId("PSE");
        request.setWebhookUrl("https://example.com/webhook");
        request.setRedirectUrl("https://example.com/redirect");
        request.setCustomer(customer);
        return request;
    }
}
