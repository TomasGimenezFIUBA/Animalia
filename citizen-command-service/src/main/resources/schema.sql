-- SCHEMA CREATION FOR ANIMALIA

CREATE TABLE IF NOT EXISTS species (
   id UUID PRIMARY KEY,
   name VARCHAR(255) NOT NULL,
   weight_kg DOUBLE PRECISION NOT NULL,
   height_mts DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS roles (
     id UUID PRIMARY KEY,
     name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS citizens (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    has_human_pet BOOLEAN NOT NULL,
    species_id UUID NOT NULL,
    CONSTRAINT fk_species FOREIGN KEY (species_id) REFERENCES species(id)
);

CREATE TABLE IF NOT EXISTS citizen_roles (
     citizen_id UUID NOT NULL,
     role_id UUID NOT NULL,
     PRIMARY KEY (citizen_id, role_id),
     CONSTRAINT fk_citizen FOREIGN KEY (citizen_id) REFERENCES citizens(id) ON DELETE CASCADE,
     CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- INDEXES
CREATE INDEX IF NOT EXISTS idx_roles_name ON roles(name);
CREATE INDEX IF NOT EXISTS idx_citizen_roles_role_id ON citizen_roles(role_id);
CREATE INDEX IF NOT EXISTS idx_citizen_roles_citizen_id ON citizen_roles(citizen_id);

CREATE INDEX IF NOT EXISTS idx_outbox_processed_created_aggregate
    ON outbox_citizen_events (processed, created_at, aggregate_id);

