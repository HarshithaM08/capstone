package com.savingsapp.groupservice.repository;

import com.savingsapp.groupservice.model.SavingsGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsGroupRepository extends MongoRepository<SavingsGroup, String> {

    List<SavingsGroup> findByOrganizerId(String organizerId);

    List<SavingsGroup> findByStatus(SavingsGroup.GroupStatus status);

    Optional<SavingsGroup> findByIdAndOrganizerId(String id, String organizerId);

    List<SavingsGroup> findByMembersUserId(String userId);

    List<SavingsGroup> findByPendingMemberIdsContaining(String userId);
}