-- ===== ROLES =====
INSERT INTO roles (id, name) VALUES
     ('f23d1a64-1111-4f4b-aaa1-000000000001', 'FIRST_MINISTER'),
     ('f23d1a64-1111-4f4b-aaa1-000000000002', 'TREASURER'),
     ('f23d1a64-1111-4f4b-aaa1-000000000003', 'GENERAL'),
     ('f23d1a64-1111-4f4b-aaa1-000000000004', 'SECRETARY_OF_STATE'),
     ('f23d1a64-1111-4f4b-aaa1-000000000005', 'MINISTER_OF_STATE'),
     ('f23d1a64-1111-4f4b-aaa1-000000000006', 'CIVIL')
ON CONFLICT (id) DO NOTHING;

-- ===== SPECIES =====
INSERT INTO species (id, name, weight_kg, height_mts) VALUES
  ('a6a3c9ee-aaaa-4b1b-bbbb-000000000001', 'Felis Domestica', 4.5, 0.25),
  ('a6a3c9ee-aaaa-4b1b-bbbb-000000000002', 'Canis Familiaris', 18.0, 0.45),
  ('a6a3c9ee-aaaa-4b1b-bbbb-000000000003', 'Rattus Norvegicus', 0.3, 0.10),
  ('a6a3c9ee-aaaa-4b1b-bbbb-000000000004', 'Equus Ferus', 500.0, 1.7),
  ('a6a3c9ee-aaaa-4b1b-bbbb-000000000005', 'Gallus Gallus Domesticus', 2.5, 0.4)
ON CONFLICT (id) DO NOTHING;
