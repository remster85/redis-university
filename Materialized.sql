CREATE MATERIALIZED VIEW unique_field_values AS
SELECT column_name,
       array_agg(DISTINCT value) AS unique_values
FROM (
    SELECT column_name,
           value
    FROM (
        SELECT column_name,
               unnest(array_agg(DISTINCT column_name)) AS value
        FROM information_schema.columns
        WHERE table_name = 'your_table_name'
        GROUP BY column_name

        UNION ALL

        SELECT column_name,
               unnest(array_agg(DISTINCT column_default)) AS value
        FROM information_schema.columns
        WHERE table_name = 'your_table_name'
        GROUP BY column_name
    ) AS sub
) AS subquery
GROUP BY column_name;
