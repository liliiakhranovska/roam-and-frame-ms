INSERT INTO categories (code, name, description) VALUES
    ('cameras', 'Cameras', 'Mirrorless and compact cameras for travel photography'),
    ('lenses', 'Lenses', 'Prime and zoom lenses for every shooting style'),
    ('camera-accessories', 'Camera Accessories', 'Tripods; filters; batteries and memory cards'),
    ('video-stabilizers', 'Video & Stabilizers', 'Gimbals; action cameras and video gear'),
    ('bags-cases', 'Bags & Cases', 'Camera backpacks; protective cases and dry bags'),
    ('hiking-apparel', 'Hiking Apparel', 'Jackets; layers and trousers for the trail'),
    ('footwear', 'Footwear', 'Hiking boots; trail shoes and water shoes'),
    ('water-sports', 'Water Sports', 'Snorkeling and swimming gear')
ON CONFLICT (code) DO NOTHING;

INSERT INTO products (sku, name, description, category_id, brand, price, attributes)
SELECT v.sku, v.name, v.description, c.id, v.brand, v.price, v.attributes::jsonb
FROM (VALUES
    ('CAM-001', 'Aperture A7 Mirrorless Camera', 'Full-frame mirrorless body for travel and landscape work', 'cameras', 'Aperture', 1899.00::numeric, '{"sensor":"full-frame","megapixels":33,"mount":"AP-Z","weight_g":658,"weatherSealed":true}'),
    ('CAM-002', 'Aperture A5 Compact Camera', 'Pocketable compact with a fixed zoom lens', 'cameras', 'Aperture', 749.00::numeric, '{"sensor":"1-inch","megapixels":20,"zoom":"24-200mm","weight_g":302}'),
    ('CAM-003', 'Nordlight X2 Mirrorless Camera', 'APS-C body with in-body stabilization', 'cameras', 'Nordlight', 1099.00::numeric, '{"sensor":"APS-C","megapixels":26,"mount":"NL-X","weight_g":512,"weatherSealed":true}'),
    ('LNS-001', 'Aperture 35mm f/1.8 Prime', 'Fast everyday prime lens', 'lenses', 'Aperture', 549.00::numeric, '{"mount":"AP-Z","focalLengthMm":35,"maxAperture":"f/1.8","weight_g":280}'),
    ('LNS-002', 'Aperture 24-105mm f/4 Zoom', 'Versatile all-round travel zoom', 'lenses', 'Aperture', 1099.00::numeric, '{"mount":"AP-Z","focalLengthMm":"24-105","maxAperture":"f/4","weight_g":663,"weatherSealed":true}'),
    ('LNS-003', 'Nordlight 16mm f/2.8 Wide', 'Compact wide-angle for landscapes', 'lenses', 'Nordlight', 399.00::numeric, '{"mount":"NL-X","focalLengthMm":16,"maxAperture":"f/2.8","weight_g":185}'),
    ('ACC-001', 'Summit Carbon Travel Tripod', 'Lightweight carbon fibre tripod that folds small', 'camera-accessories', 'Summit', 229.00::numeric, '{"material":"carbon fibre","maxLoadKg":8,"foldedLengthCm":40,"weight_g":1150}'),
    ('ACC-002', 'ClearView ND Filter Set 67mm', 'Three-piece neutral density filter kit', 'camera-accessories', 'ClearView', 89.00::numeric, '{"diameterMm":67,"stops":[3,6,10]}'),
    ('ACC-003', 'Aperture 128GB V90 SD Card', 'Fast UHS-II card for 4K video', 'camera-accessories', 'Aperture', 59.00::numeric, '{"capacityGb":128,"speedClass":"V90","readMbps":300}'),
    ('VID-001', 'SteadyGo 3-Axis Gimbal', 'Foldable smartphone and camera gimbal', 'video-stabilizers', 'SteadyGo', 159.00::numeric, '{"axes":3,"maxPayloadKg":1.2,"batteryHours":12,"foldable":true}'),
    ('VID-002', 'TrailCam Action 5', 'Waterproof 4K action camera', 'video-stabilizers', 'TrailCam', 349.00::numeric, '{"maxResolution":"4K","fps":120,"waterproofMeters":18,"weight_g":154}'),
    ('BAG-001', 'Summit Photo Backpack 25L', 'Camera backpack with modular dividers', 'bags-cases', 'Summit', 189.00::numeric, '{"capacityL":25,"laptopInches":15,"waterResistant":true,"weight_g":1300}'),
    ('BAG-002', 'DryLine Dry Bag 20L', 'Roll-top waterproof bag for boats and beaches', 'bags-cases', 'DryLine', 34.00::numeric, '{"capacityL":20,"material":"PVC-free nylon","waterproof":true}'),
    ('APP-001', 'NorthPeak Hardshell Jacket', 'Waterproof breathable jacket for mountain weather', 'hiking-apparel', 'NorthPeak', 219.00::numeric, '{"sizes":["S","M","L","XL"],"waterproofRating":"20000mm","gender":"unisex","weight_g":420}'),
    ('APP-002', 'NorthPeak Trail Trousers', 'Quick-dry stretch trousers with zip-off legs', 'hiking-apparel', 'NorthPeak', 99.00::numeric, '{"sizes":["S","M","L","XL"],"material":"recycled nylon","quickDry":true,"gender":"unisex"}'),
    ('APP-003', 'WoolWise Merino Base Layer', 'Odour-resistant merino long sleeve top', 'hiking-apparel', 'WoolWise', 85.00::numeric, '{"sizes":["S","M","L","XL"],"material":"merino wool","weightGsm":200,"gender":"unisex"}'),
    ('SHO-001', 'Ridgeline Waterproof Hiking Boots', 'Mid-cut leather boots with grippy outsole', 'footwear', 'Ridgeline', 179.00::numeric, '{"sizesEu":[39,40,41,42,43,44,45],"waterproof":true,"cut":"mid","sole":"Vibram"}'),
    ('SHO-002', 'Ridgeline Trail Runners', 'Light and cushioned shoes for fast hikes', 'footwear', 'Ridgeline', 129.00::numeric, '{"sizesEu":[39,40,41,42,43,44,45],"waterproof":false,"cut":"low","dropMm":6}'),
    ('SHO-003', 'AquaStep Water Shoes', 'Quick-drain shoes for rivers and rocky beaches', 'footwear', 'AquaStep', 59.00::numeric, '{"sizesEu":[37,38,39,40,41,42,43,44],"quickDrain":true,"sole":"rubber"}'),
    ('WAT-001', 'BlueFin Snorkel Set', 'Mask and dry-top snorkel with anti-fog lens', 'water-sports', 'BlueFin', 69.00::numeric, '{"sizes":["S/M","L/XL"],"lens":"tempered glass","dryTop":true}'),
    ('WAT-002', 'BlueFin Travel Fins', 'Short fins that fit in hand luggage', 'water-sports', 'BlueFin', 79.00::numeric, '{"sizes":["S/M","L/XL"],"lengthCm":38,"weight_g":900}'),
    ('WAT-003', 'SunShield UV Rash Guard', 'Long sleeve UPF 50 swim shirt', 'water-sports', 'SunShield', 45.00::numeric, '{"sizes":["S","M","L","XL"],"upf":50,"gender":"unisex"}')
) AS v (sku, name, description, category_code, brand, price, attributes)
    JOIN categories c ON c.code = v.category_code
ON CONFLICT (sku) DO NOTHING;
