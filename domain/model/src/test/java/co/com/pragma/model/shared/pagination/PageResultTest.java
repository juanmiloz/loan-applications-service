package co.com.pragma.model.shared.pagination;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageResultTest {

    @Test
    @DisplayName("of: cálculo de totalPages (ceil) y flags")
    void ofTotalPagesAndFlags() {
        PageResult<String> p0 = PageResult.of(List.of("a","b","c"), 0, 10, 95);
        assertThat(p0.totalPages()).isEqualTo(10);   // ceil(95/10)
        assertThat(p0.hasNext()).isTrue();           // (0+1)<10
        assertThat(p0.hasPrevious()).isFalse();

        PageResult<String> pMid = PageResult.of(List.of("x"), 5, 10, 95);
        assertThat(pMid.totalPages()).isEqualTo(10);
        assertThat(pMid.hasNext()).isTrue();
        assertThat(pMid.hasPrevious()).isTrue();

        PageResult<String> pLast = PageResult.of(List.of(), 9, 10, 95);
        assertThat(pLast.totalPages()).isEqualTo(10);
        assertThat(pLast.hasNext()).isFalse();       // (9+1)<10 -> false
        assertThat(pLast.hasPrevious()).isTrue();
    }

    @Test
    @DisplayName("of: bordes de total=0")
    void ofZeroTotal() {
        PageResult<String> p = PageResult.of(List.of(), 0, 10, 0);
        assertThat(p.totalPages()).isEqualTo(0);
        assertThat(p.hasNext()).isFalse();
        assertThat(p.hasPrevious()).isFalse();
    }

    @Test
    @DisplayName("of: otros casos de ceil")
    void ofCeilCases() {
        PageResult<String> p1 = PageResult.of(List.of(), 0, 10, 20);
        PageResult<String> p2 = PageResult.of(List.of(), 0, 10, 21);

        assertThat(p1.totalPages()).isEqualTo(2);
        assertThat(p2.totalPages()).isEqualTo(3);
    }

    @Test
    @DisplayName("@With: crear nueva instancia con campo modificado")
    void withersWork() {
        PageResult<String> base = PageResult.of(List.of("a","b"), 1, 5, 12);
        PageResult<String> changed = base.withPage(3).withHasNext(false);

        assertThat(changed.page()).isEqualTo(3);
        assertThat(changed.hasNext()).isFalse();
        // el resto permanece igual
        assertThat(changed.size()).isEqualTo(base.size());
        assertThat(changed.totalElements()).isEqualTo(base.totalElements());
        assertThat(changed.totalPages()).isEqualTo(base.totalPages());
        assertThat(changed.hasPrevious()).isEqualTo(base.hasPrevious());
        assertThat(changed.content()).containsExactlyElementsOf(base.content());

        // inmutabilidad (instancias distintas)
        assertThat(changed).isNotSameAs(base);
    }

}