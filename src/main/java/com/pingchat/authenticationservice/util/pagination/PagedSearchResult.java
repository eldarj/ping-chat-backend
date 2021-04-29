package com.pingchat.authenticationservice.util.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedSearchResult<T> {
    private List<T> page;
    private Long totalElements;

    private Map<String, Object> additionalData;

    public PagedSearchResult(List<T> page, Long totalElements) {
        this.page = page;
        this.totalElements = totalElements;
    }
}
