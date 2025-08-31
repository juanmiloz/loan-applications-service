package co.com.pragma.model.loantype;

import co.com.pragma.model.util.LoanTypeCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LoanTypeTest {

    @Test
    @DisplayName("createValidLoanType: rangos y campos coherentes")
    void validLoanType() {
        LoanType lt = LoanTypeCreator.createValidLoanType();

        assertThat(lt.getLoanTypeId()).isNotNull();
        assertThat(lt.getName()).isNotBlank();
        assertThat(lt.getMinAmount()).isNotNull();
        assertThat(lt.getMaxAmount()).isNotNull();
        assertThat(lt.getMinAmount().compareTo(BigDecimal.ZERO)).isGreaterThanOrEqualTo(0);
        assertThat(lt.getMaxAmount().compareTo(lt.getMinAmount())).isGreaterThanOrEqualTo(0);
        assertThat(lt.getInterestRate().compareTo(BigDecimal.ZERO)).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("createWithNegativeMinAmount: minAmount < 0")
    void negativeMinAmount() {
        LoanType lt = LoanTypeCreator.createWithNegativeMinAmount();
        assertThat(lt.getMinAmount().compareTo(BigDecimal.ZERO)).isNegative();
    }

    @Test
    @DisplayName("createWithMaxLessThanMin: max < min")
    void maxLessThanMin() {
        LoanType lt = LoanTypeCreator.createWithMaxLessThanMin();
        assertThat(lt.getMaxAmount().compareTo(lt.getMinAmount())).isNegative();
    }

    @Test
    @DisplayName("createWithZeroInterestRate: tasa == 0")
    void zeroInterestRate() {
        LoanType lt = LoanTypeCreator.createWithZeroInterestRate();
        assertThat(lt.getInterestRate()).isZero();
    }

    @Test
    @DisplayName("createWithHighInterestRate: tasa > 100%")
    void highInterestRate() {
        LoanType lt = LoanTypeCreator.createWithHighInterestRate();
        assertThat(lt.getInterestRate().compareTo(BigDecimal.ONE)).isPositive();
    }

}