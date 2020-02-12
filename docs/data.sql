CREATE KEYSPACE IF NOT EXISTS revision
    WITH replication = {
        'class' : 'SimpleStrategy',
        'replication_factor' : 1
        };

CREATE TABLE IF NOT EXISTS revision.amendments
(
    cpid text,
    id   uuid,
    data text,
    primary key (cpid, id)
);

CREATE TABLE IF NOT EXISTS  revision.history
(
    operation_id text,
    command text,
    operation_date timestamp,
    json_data text,
    primary key(operation_id, command)
);