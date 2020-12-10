package com.pingchat.authenticationservice.util.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedSearchResult<T> {
    private List<T> page;
    private Long totalElements;
}
