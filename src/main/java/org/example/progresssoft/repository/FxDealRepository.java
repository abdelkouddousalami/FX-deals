package org.example.progresssoft.repository;

import org.example.progresssoft.entity.FxDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface FxDealRepository extends JpaRepository<FxDeal, Long> {

    /**
     * Check if a deal with the given unique ID already exists.
     *
     * @param dealUniqueId the unique identifier of the deal
     * @return true if deal exists, false otherwise
     */
    boolean existsByDealUniqueId(String dealUniqueId);

    /**
     * Find a deal by its unique identifier.
     *
     * @param dealUniqueId the unique identifier of the deal
     * @return Optional containing the deal if found
     */
    Optional<FxDeal> findByDealUniqueId(String dealUniqueId);
}
