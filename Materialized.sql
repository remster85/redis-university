CREATE MATERIALIZED VIEW unique_field_values AS
SELECT column_name,
       array_agg(DISTINCT column_value) AS unique_values
FROM (
    SELECT column_name,
           CASE 
               WHEN data_type = 'ARRAY' THEN unnest(string_to_array(column_default, ','))::text
               ELSE column_default
           END AS column_value
    FROM information_schema.columns
    WHERE table_name = 'your_table_name'
) subquery
GROUP BY column_name;
