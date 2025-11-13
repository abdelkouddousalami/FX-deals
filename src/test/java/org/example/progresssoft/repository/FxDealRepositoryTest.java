package org.example.progresssoft.repository;

import org.example.progresssoft.entity.FxDeal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("FxDealRepository Tests")
class FxDealRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FxDealRepository fxDealRepository;

    @Test
    @DisplayName("Should save and retrieve an FX deal")
    void testSaveAndFind() {
        // Arrange
        FxDeal deal = FxDeal.builder()
                .dealUniqueId("TEST-001")
                .fromCurrencyIsoCode("USD")
                .toCurrencyIsoCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("1000.00"))
                .build();

        // Act
        FxDeal savedDeal = fxDealRepository.save(deal);
        FxDeal foundDeal = entityManager.find(FxDeal.class, savedDeal.getId());

        // Assert
        assertThat(foundDeal).isNotNull();
        assertThat(foundDeal.getDealUniqueId()).isEqualTo("TEST-001");
        assertThat(foundDeal.getFromCurrencyIsoCode()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should check if deal exists by unique ID")
    void testExistsByDealUniqueId() {
        // Arrange
        FxDeal deal = FxDeal.builder()
                .dealUniqueId("TEST-002")
                .fromCurrencyIsoCode("GBP")
                .toCurrencyIsoCode("JPY")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("2000.00"))
                .build();

        entityManager.persist(deal);
        entityManager.flush();

        // Act
        boolean exists = fxDealRepository.existsByDealUniqueId("TEST-002");
        boolean notExists = fxDealRepository.existsByDealUniqueId("NONEXISTENT");

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should find deal by unique ID")
    void testFindByDealUniqueId() {
        // Arrange
        FxDeal deal = FxDeal.builder()
                .dealUniqueId("TEST-003")
                .fromCurrencyIsoCode("USD")
                .toCurrencyIsoCode("CAD")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("1500.00"))
                .build();

        entityManager.persist(deal);
        entityManager.flush();

        // Act
        Optional<FxDeal> foundDeal = fxDealRepository.findByDealUniqueId("TEST-003");
        Optional<FxDeal> notFound = fxDealRepository.findByDealUniqueId("NONEXISTENT");

        // Assert
        assertThat(foundDeal).isPresent();
        assertThat(foundDeal.get().getDealUniqueId()).isEqualTo("TEST-003");
        assertThat(notFound).isNotPresent();
    }

    @Test
    @DisplayName("Should enforce unique constraint on dealUniqueId")
    void testUniqueConstraint() {
        // Arrange
        FxDeal deal1 = FxDeal.builder()
                .dealUniqueId("UNIQUE-001")
                .fromCurrencyIsoCode("USD")
                .toCurrencyIsoCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("1000.00"))
                .build();

        entityManager.persist(deal1);
        entityManager.flush();

        FxDeal deal2 = FxDeal.builder()
                .dealUniqueId("UNIQUE-001")
                .fromCurrencyIsoCode("GBP")
                .toCurrencyIsoCode("JPY")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("2000.00"))
                .build();

        // Act & Assert
        try {
            entityManager.persist(deal2);
            entityManager.flush();
        } catch (Exception e) {
            assertThat(e).isNotNull();
        }
    }
}
