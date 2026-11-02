INSERT INTO stock_items (sku, quantity) VALUES
                                            ('CAM-001', 12), ('CAM-002', 25), ('CAM-003', 0),
                                            ('LNS-001', 8),  ('LNS-002', 0),  ('LNS-003', 2),
                                            ('ACC-001', 15), ('ACC-002', 40), ('ACC-003', 120),
                                            ('VID-001', 18), ('VID-002', 1),
                                            ('BAG-001', 9),  ('BAG-002', 60),
                                            ('APP-001', 3),  ('APP-002', 22), ('APP-003', 35),
                                            ('SHO-001', 0),  ('SHO-002', 14), ('SHO-003', 48),
                                            ('WAT-001', 27), ('WAT-002', 0),  ('WAT-003', 75)
    ON CONFLICT (sku) DO NOTHING;