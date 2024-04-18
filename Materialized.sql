CREATE MATERIALIZED VIEW unique_field_values AS
SELECT column_name,
       array_agg(DISTINCT value) AS unique_values
FROM (
    SELECT column_name,
           unnest(string_to_array(trim(trailing ')' from trim(leading 'ARRAY[' from column_default)), ',')) AS value
    FROM information_schema.columns
    WHERE table_name = 'your_table_name'
    AND data_type = 'ARRAY'

    UNION ALL

    SELECT column_name,
           column_default AS value
    FROM information_schema.columns
    WHERE table_name = 'your_table_name'
    AND data_type != 'ARRAY'
) subquery
GROUP BY column_name;
