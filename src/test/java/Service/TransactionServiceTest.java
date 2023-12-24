package Service;

import com.example.L20paymentgatewaydemo.dto.PaymentInitResponse;
import com.example.L20paymentgatewaydemo.dto.PaymentPageRequest;
import com.example.L20paymentgatewaydemo.entity.Merchant;
import com.example.L20paymentgatewaydemo.entity.Transaction;
import com.example.L20paymentgatewaydemo.repo.MerchantRepo;
import com.example.L20paymentgatewaydemo.repo.TransactionRepo;
import com.example.L20paymentgatewaydemo.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepo transactionRepo;

    @Mock
    private MerchantRepo merchantRepo;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetStatus() {
        String txnId = UUID.randomUUID().toString();
        Transaction transaction = new Transaction();
        transaction.setTxnId(txnId);
        transaction.setStatus("SUCCESS");

        when(transactionRepo.findByTxnId(txnId)).thenReturn(transaction);

        String result = transactionService.getStatus(txnId);

        assertEquals("SUCCESS", result);
    }

    @Test
    void testGetTransaction() {
        String txnId = UUID.randomUUID().toString();
        Transaction transaction = new Transaction();
        transaction.setTxnId(txnId);

        when(transactionRepo.findByTxnId(txnId)).thenReturn(transaction);

        Transaction result = transactionService.getTransaction(txnId);

        assertEquals(transaction, result);
    }

    @Test
    void testDoPaymentAndRedirect() {
        String txnId = UUID.randomUUID().toString();
        Transaction transaction = new Transaction();
        transaction.setTxnId(txnId);
        transaction.setStatus("PENDING");
        transaction.setMerchantId(1L);

        when(transactionRepo.findByTxnId(txnId)).thenReturn(transaction);

        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setRedirectionUrl("http://example.com/redirect/");

        when(merchantRepo.findById(1L)).thenReturn(Optional.of(merchant));

        String result = transactionService.doPaymentAndRedirect(txnId);

        assertEquals("SUCCESS", transaction.getStatus());
        assertEquals("http://example.com/redirect/" + txnId, result);
        verify(transactionRepo, times(1)).save(transaction);
    }

//    @Test
//    void testGeneratePaymentPage() {
//        PaymentPageRequest request = new PaymentPageRequest();
//        request.setMerchantId(1L);
//        request.setAmount(100.0);
//
//        String txnId= "ashish";
//
//        when(transactionRepo.save(any(Transaction.class))).thenAnswer(invocation -> {
//            Transaction savedTransaction = invocation.getArgument(0);
//            savedTransaction.setTxnId(txnId);
//            return savedTransaction;
//        });
//
//        PaymentInitResponse result = transactionService.generatePaymentPage(request);
//
//        assertEquals(txnId, result.getTxnId());
//        //assertEquals("http://localhost:9090/payment-page/" + txnId, result.getUrl());
//    }

//

    @Test
    void testGeneratePaymentPage() {
        PaymentPageRequest request = new PaymentPageRequest();
        request.setMerchantId(1L);
        request.setAmount(100.0);

        // Use any() to match any Transaction object
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction savedTransaction = invocation.getArgument(0);
            return savedTransaction;
        });

        PaymentInitResponse result = transactionService.generatePaymentPage(request);

        // Check individual fields instead of the whole object
        assertNotNull(result.getTxnId());

        // Adjust this assertion based on your actual URL generation logic
        assertTrue(result.getUrl().contains("http://localhost:9090/payment-page/" + result.getTxnId()));
    }

}
