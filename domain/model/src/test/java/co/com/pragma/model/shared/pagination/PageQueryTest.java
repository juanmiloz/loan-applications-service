package co.com.pragma.model.shared.pagination;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PageQueryTest {

    @Test
    @DisplayName("of: valores normales se conservan")
    void ofNormal() {
        PageQuery pq = PageQuery.of(2, 10);
        assertThat(pq.page()).isEqualTo(2);
        assertThat(pq.size()).isEqualTo(10);
    }

    @Test
    @DisplayName("of: page negativo -> 0")
    void ofNegativePage() {
        PageQuery pq = PageQuery.of(-5, 10);
        assertThat(pq.page()).isEqualTo(0);
        assertThat(pq.size()).isEqualTo(10);
    }

    @Test
    @DisplayName("of: size mínimo -> 1")
    void ofMinSize() {
        PageQuery pq = PageQuery.of(1, 0);
        assertThat(pq.page()).isEqualTo(1);
        assertThat(pq.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("of: size máximo -> 200")
    void ofMaxSize() {
        PageQuery pq = PageQuery.of(1, 500);
        assertThat(pq.page()).isEqualTo(1);
        assertThat(pq.size()).isEqualTo(200);
    }

    @Test
    @DisplayName("equals/hashCode por record")
    void equalsHash() {
        PageQuery a = PageQuery.of(0, 1);
        PageQuery b = new PageQuery(0, 1);
        assertThat(a).isEqualTo(b);
        assertThat(a).hasSameHashCodeAs(b);
    }
}