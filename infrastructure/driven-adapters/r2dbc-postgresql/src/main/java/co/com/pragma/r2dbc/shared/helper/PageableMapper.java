package co.com.pragma.r2dbc.shared.helper;

import co.com.pragma.model.shared.pagination.PageQuery;
import co.com.pragma.model.shared.pagination.PageResult;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Mono;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PageableMapper {

    default Pageable toPageable(PageQuery q) {
        if (q == null) {
            return PageRequest.of(0, 20,
                    Sort.by(Sort.Order.desc("createdAt")).and(Sort.by(Sort.Order.asc("id"))));
        }

        return PageRequest.of(q.page(), q.size());
    }

    default <T> PageResult<T> toPageResult(List<T> content, Pageable pageable, long totalElements) {
        int page = (pageable != null) ? pageable.getPageNumber() : 0;
        int size = (pageable != null) ? pageable.getPageSize() : (content != null ? content.size() : 0);
        return PageResult.of(content, page, size, totalElements);
    }

    default <T> PageResult<T> toPageResult(Page<T> page) {
        return new PageResult<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    default <T> Mono<PageResult<T>> toPageResult(Mono<Page<T>> page) {
        return page.map(this::toPageResult);
    }

}
