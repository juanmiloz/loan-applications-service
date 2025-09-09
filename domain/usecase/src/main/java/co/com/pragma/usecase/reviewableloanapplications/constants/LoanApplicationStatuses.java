package co.com.pragma.usecase.reviewableloanapplications.constants;

import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public final class LoanApplicationStatuses {

    public static final String PENDING = "PENDING";
    public static final String REJECTED = "REJECTED";
    public static final String MANUAL_REVIEW = "MANUAL_REVIEW";

    public static final Set<String> REVIEWABLE_NAMES = Set.of(PENDING, REJECTED, MANUAL_REVIEW);

}
