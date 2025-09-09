package co.com.pragma.model.shared.pagination;

import lombok.With;

import java.util.List;

@With
public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {

    public static <T> PageResult<T> of(List<T> content, int page, int size, long total) {
        int totalPages = (int) Math.ceil(total / (double) size);
        boolean hasNext = (page + 1) < totalPages;
        boolean hasPrevious = page > 0;
        return new PageResult<>(content, page, size, total, totalPages, hasNext, hasPrevious);
    }

}
