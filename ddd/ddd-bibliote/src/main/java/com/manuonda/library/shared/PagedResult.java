package com.manuonda.library.shared;

import java.util.List;
import java.util.function.Function;
/** * Class PagedResult
 * @author  dgarcia
 * @version 1.0
 * @date    15/01/2025
 */
public record PagedResult<T>(
        List<T> data,
        long totalElements,
        int pageNumber,
        int totalPages,
        boolean isFirst,
        boolean isLast,
        boolean hasNext,
        boolean hasPrevious) {

    public static <S, T> PagedResult<T> of(PagedResult<S> pagedResult, Function<S, T> mapper) {
        return new PagedResult<>(
                pagedResult.data.stream().map(mapper).toList(),
                pagedResult.totalElements,
                pagedResult.pageNumber,
                pagedResult.totalPages,
                pagedResult.isFirst,
                pagedResult.isLast,
                pagedResult.hasNext,
                pagedResult.hasPrevious);
    }
}