DO
$$
DECLARE
    _table_name TEXT := 'your_table_name';
    _query TEXT;
BEGIN
    FOR _col IN
        SELECT column_name
        FROM information_schema.columns
        WHERE table_name = _table_name
    LOOP
        _query := 'SELECT DISTINCT ' || quote_ident(_col.column_name) || ' FROM ' || quote_ident(_table_name);
        EXECUTE _query;
    END LOOP;
END
$$;
