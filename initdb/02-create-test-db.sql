-- Only create if not exists (guard manually)
SELECT 'CREATE DATABASE walkit_test OWNER walkit'
    WHERE NOT EXISTS (
  SELECT FROM pg_database WHERE datname = 'walkit_test'
)\gexec

-- Install PostGIS
\c walkit_test
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;
