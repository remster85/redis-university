SELECT column_name, array_agg(DISTINCT value) AS unique_values
FROM (
    SELECT column_name, value
    FROM (
        SELECT column_name, unnest(array_agg(DISTINCT column_name)) AS value
        FROM information_schema.columns
        WHERE table_name = 'your_table_name'
        GROUP BY column_name

        UNION ALL

        SELECT column_name, value
        FROM (
            SELECT column_name, value
            FROM your_table_name
            CROSS JOIN LATERAL (
                SELECT value FROM unnest(array[<column_names>]) AS value
            ) AS subquery
        ) AS distinct_values
        GROUP BY column_name, value
    ) AS sub
) AS final
GROUP BY column_name;
