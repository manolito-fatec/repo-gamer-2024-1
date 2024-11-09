UPDATE location
SET poly = SDO_CS.TRANSFORM(poly, 4326)
WHERE poly IS NOT NULL AND poly.sdo_srid IS NULL;


---------------------

INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID)
VALUES (
           'LOCATION',
           'POLY',
           SDO_DIM_ARRAY(
                   SDO_DIM_ELEMENT('LONG', -180, 180, 0.5),
                   SDO_DIM_ELEMENT('LAT', -90, 90, 0.5)
           ),
           4326
       );