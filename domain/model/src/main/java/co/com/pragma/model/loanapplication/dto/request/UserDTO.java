package co.com.pragma.model.loanapplication.dto.request;

import java.util.UUID;

public record UserDTO(
        UUID userId, String firstName, String lastName, String email, String id, String phoneNumber, Double baseSalary, String password, String roleId
) {
}
