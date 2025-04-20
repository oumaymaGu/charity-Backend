package tn.example.charity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.example.charity.Entity.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(
            Long sender1, Long receiver1, Long sender2, Long receiver2);

    List<Message> findBySenderIdAndReceiverIdAndAssociationIdAssOrReceiverIdAndSenderIdAndAssociationIdAssOrderByTimestampAsc(
            Long sender1, Long receiver1, Long associationId1, Long sender2, Long receiver2, Long associationId2);

}
