package com.tarasov.market.repository.impl;

import com.tarasov.market.model.db.OfferingWithCartItem;
import com.tarasov.market.model.db.PageRequest;
import com.tarasov.market.repository.OfferingRepositoryCustom;
import com.tarasov.market.repository.impl.sql.SQLConstants;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
public class OfferingRepositoryCustomImpl implements OfferingRepositoryCustom {

    private final R2dbcEntityTemplate entityTemplate;

    @Override
    public Flux<OfferingWithCartItem> findOfferings(PageRequest pageRequest, String search) {
        return fetchData(prepareSQLQuery(null, pageRequest, search),
                collectQueryParams(null, pageRequest, search));
    }

    @Override
    public Flux<OfferingWithCartItem> findOfferings(Long userId, PageRequest pageRequest, String search) {
        return fetchData(prepareSQLQuery(userId, pageRequest, search),
                collectQueryParams(userId, pageRequest, search));
    }

    private Flux<OfferingWithCartItem> fetchData(String sqlQuery, Map<String, Object> params) {
        return entityTemplate.getDatabaseClient()
                .sql(sqlQuery)
                .bindValues(params)
                .map((row, rowMetadata) -> new OfferingWithCartItem(
                        row.get("offering_id", Long.class),
                        row.get("offering_title", String.class),
                        row.get("offering_description", String.class),
                        row.get("offering_img_path", String.class),
                        row.get("offering_price", BigDecimal.class),
                        row.get("cart_item_id", Long.class),
                        row.get("amount_in_cart", Integer.class)
                ))
                .all();
    }

    private String prepareSQLQuery(Long userId, PageRequest pageRequest, String search) {
        StringBuilder sqlQuery = userId != null
                ? new StringBuilder(SQLConstants.FETCH_OFFERINGS_SQL_FOR_USER)
                : new StringBuilder(SQLConstants.FETCH_OFFERINGS_SQL_ANONYMOUS);
        if (StringUtils.isNotEmpty(search)) {
            sqlQuery.append(SQLConstants.SEARCH_CONDITION_SQL);
        }
        if (pageRequest.getSortField() != null) {
            sqlQuery.append(SQLConstants.SORTING_CLAUSE_SQL.formatted(pageRequest.getSortField()));
        }
        sqlQuery.append(SQLConstants.PAGING_CLAUSE_SQL);
        return sqlQuery.toString();
    }

    private Map<String, Object> collectQueryParams(Long userId, PageRequest pageRequest, String search) {
        Map<String, Object> queryParams = new HashMap<>(
                Map.of("limit", pageRequest.getPageSize(),
                        "offset", pageRequest.getPageSize() * (pageRequest.getPageNumber() - 1))
        );
        if (StringUtils.isNotEmpty(search)) {
            queryParams.put("search", search);
        }
        if (userId != null) {
            queryParams.put("user_id", userId);
        };
        return queryParams;
    }
}
