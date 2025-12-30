package com.tarasov.market.repository.impl.sql;

public class SQLConstants {

    public static final String FETCH_OFFERINGS_SQL_ANONYMOUS = """
    SELECT o.id offering_id,
           o.title offering_title,
           o.description offering_description,
           o.img_path offering_img_path,
           o.price offering_price,
           0 cart_item_id,
           0 amount_in_cart
    FROM offerings o
    """;

    public static final String FETCH_OFFERINGS_SQL_FOR_USER = """
    SELECT o.id offering_id,
           o.title offering_title,
           o.description offering_description,
           o.img_path offering_img_path,
           o.price offering_price,
           c.id cart_item_id,
           c.amount amount_in_cart
    FROM offerings o
    LEFT JOIN cart c ON o.id = c.offering_id AND c.user_id = :user_id
    """;

    public static final String SEARCH_CONDITION_SQL = """
    WHERE o.title LIKE CONCAT('%', :search, '%') OR o.description LIKE CONCAT('%', :search, '%')
    """;

    public static final String SORTING_CLAUSE_SQL = """
    ORDER BY %s
    """;

    public static final String PAGING_CLAUSE_SQL = """
    LIMIT :limit OFFSET :offset
    """;
}
