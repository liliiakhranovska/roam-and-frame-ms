CREATE USER customer_svc  WITH PASSWORD 'customer';  CREATE DATABASE customer  OWNER customer_svc;
CREATE USER catalog_svc   WITH PASSWORD 'catalog';   CREATE DATABASE catalog   OWNER catalog_svc;
CREATE USER inventory_svc WITH PASSWORD 'inventory'; CREATE DATABASE inventory OWNER inventory_svc;
CREATE USER cart_svc      WITH PASSWORD 'cart';      CREATE DATABASE cart      OWNER cart_svc;
CREATE USER orders_svc    WITH PASSWORD 'orders';    CREATE DATABASE orders    OWNER orders_svc;
CREATE USER payment_svc   WITH PASSWORD 'payment';   CREATE DATABASE payment   OWNER payment_svc;
CREATE USER shipping_svc  WITH PASSWORD 'shipping';  CREATE DATABASE shipping  OWNER shipping_svc;
CREATE USER checkout_svc  WITH PASSWORD 'checkout';  CREATE DATABASE checkout  OWNER checkout_svc;