package co.com.pragma.model.status;

import co.com.pragma.model.util.StatusCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class StatusTest {

    @Test
    @DisplayName("createValidStatus: campos obligatorios")
    void validStatus() {
        Status s = StatusCreator.createValidStatus();

        assertThat(s.getStatusId()).isNotNull();
        assertThat(s.getName()).isNotBlank();
        assertThat(s.getDescription()).isNotBlank();
    }

    @Test
    @DisplayName("createWithBlankName: name en blanco")
    void blankName() {
        Status s = StatusCreator.createWithBlankName();
        assertThat(s.getName()).isBlank();
        assertThat(s.getDescription()).isNotBlank();
    }

    @Test
    @DisplayName("createWithBlankDescription: description en blanco")
    void blankDescription() {
        Status s = StatusCreator.createWithBlankDescription();
        assertThat(s.getDescription()).isBlank();
        assertThat(s.getName()).isNotBlank();
    }

    @Test
    @DisplayName("createWithNullDescription: description null")
    void nullDescription() {
        Status s = StatusCreator.createWithNullDescription();
        assertThat(s.getDescription()).isNull();
    }

    @Test
    @DisplayName("createDuplicatedName: nombre duplicado esperado")
    void duplicatedName() {
        Status s = StatusCreator.createDuplicatedName();
        assertThat(s.getName()).isEqualTo("APPROVED");
    }

}