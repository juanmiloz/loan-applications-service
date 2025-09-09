package co.com.pragma.usecase.reviewableloanapplications.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoanApplicationStatusesTest {

    @Test
    @DisplayName("REVIEWABLE_NAMES contiene exactamente PENDING, REJECTED y MANUAL_REVIEW y es inmutable")
    void reviewableNames_containsExactlyAndIsUnmodifiable() {
        assertEquals("PENDING", LoanApplicationStatuses.PENDING);
        assertEquals("REJECTED", LoanApplicationStatuses.REJECTED);
        assertEquals("MANUAL_REVIEW", LoanApplicationStatuses.MANUAL_REVIEW);

        Set<String> names = LoanApplicationStatuses.REVIEWABLE_NAMES;
        assertEquals(3, names.size());
        assertTrue(names.contains("PENDING"));
        assertTrue(names.contains("REJECTED"));
        assertTrue(names.contains("MANUAL_REVIEW"));

        // Set.of(...) es inmodificable: debe lanzar UnsupportedOperationException
        assertThrows(UnsupportedOperationException.class, () -> names.add("ANY"));
    }

}