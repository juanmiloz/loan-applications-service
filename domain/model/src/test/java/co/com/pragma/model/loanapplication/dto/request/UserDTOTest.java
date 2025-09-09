package co.com.pragma.model.loanapplication.dto.request;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserDTOTest {

    @Test
    @DisplayName("UserDTO: getters, valor y equals/hashCode")
    void userDtoBasics() {
        UUID userId = UUID.randomUUID();
        UserDTO u1 = new UserDTO(
                userId, "Juan", "Pérez", "juan.perez@mail.com",
                "1234567890", "+57 3000000000", 3_500_000D, "s3cr3t", "ROLE_ADMIN"
        );

        assertThat(u1.userId()).isEqualTo(userId);
        assertThat(u1.firstName()).isEqualTo("Juan");
        assertThat(u1.lastName()).isEqualTo("Pérez");
        assertThat(u1.email()).isEqualTo("juan.perez@mail.com");
        assertThat(u1.id()).isEqualTo("1234567890");
        assertThat(u1.phoneNumber()).isEqualTo("+57 3000000000");
        assertThat(u1.baseSalary()).isEqualTo(3_500_000D);
        assertThat(u1.password()).isEqualTo("s3cr3t");
        assertThat(u1.roleId()).isEqualTo("ROLE_ADMIN");

        UserDTO u2 = new UserDTO(
                userId, "Juan", "Pérez", "juan.perez@mail.com",
                "1234567890", "+57 3000000000", 3_500_000D, "s3cr3t", "ROLE_ADMIN"
        );

        assertThat(u1).isEqualTo(u2);
        assertThat(u1).hasSameHashCodeAs(u2);
        assertThat(u1.toString()).contains("juan.perez@mail.com");
    }
}