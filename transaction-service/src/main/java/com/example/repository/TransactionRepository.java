package com.example.repository;

import com.example.model.Txn;
import com.example.model.TxnStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Transactional
public interface TransactionRepository extends JpaRepository<Txn,Integer> {

    @Modifying
    @Query("update Txn t set t.txnStatus=?2 where t.externalTxnId=?1")
    void updateTxnStatus(String externalTxnId, TxnStatus transactionStatus);

    Txn findByExternalTxnId(String externalTxnId);

//    List<Txn> findBySender(String username);

    @Query("SELECT t FROM Txn t WHERE t.sender = :username OR t.receiver = :username ORDER BY t.CreatedTime DESC")
    List<Txn> findBySenderOrReceiverOrderByCreatedTimeDesc(String username);
}
