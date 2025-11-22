package com.tarasov.market.repository.impl;

import com.tarasov.market.model.dto.db.OfferingWithCartItem;
import com.tarasov.market.model.dto.db.PageRequest;
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
    public Flux<OfferingWithCartItem> findOfferings(PageRequest pageRequest) {
        return fetchData(prepareSQLQuery(pageRequest, null),
                collectQueryParams(pageRequest, null));
    }

    @Override
    public Flux<OfferingWithCartItem> findOfferings(String search, PageRequest pageRequest) {
        return fetchData(prepareSQLQuery(pageRequest, search),
                collectQueryParams(pageRequest, search));
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

    private String prepareSQLQuery(PageRequest pageRequest, String search) {
        StringBuilder sqlQuery = new StringBuilder(SQLConstants.FETCH_OFFERINGS_SQL);
        if (StringUtils.isNotEmpty(search)) {
            sqlQuery.append(SQLConstants.SEARCH_CONDITION_SQL);
        }
        if (pageRequest.getSortField() != null) {
            sqlQuery.append(SQLConstants.SORTING_CLAUSE_SQL.formatted(pageRequest.getSortField()));
        }
        sqlQuery.append(SQLConstants.PAGING_CLAUSE_SQL);
        return sqlQuery.toString();
    }

    private Map<String, Object> collectQueryParams(PageRequest pageRequest, String search) {
        Map<String, Object> queryParams = new HashMap<>(
                Map.of("limit", pageRequest.getPageSize(),
                        "offset", pageRequest.getPageSize() * (pageRequest.getPageNumber() - 1))
        );
        if (StringUtils.isNotEmpty(search)) {
            queryParams.put("search", search);
        }
        return queryParams;
    }
}
