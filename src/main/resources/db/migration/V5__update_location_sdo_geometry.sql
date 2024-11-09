UPDATE location
SET poly = SDO_GEOMETRY(
    2003, -- 2003 indicates a polygon (update if your geometry type is different)
    4326, -- Setting SRID to 4326
    NULL,
    poly.sdo_elem_info,
    poly.sdo_ordinates
)
WHERE poly IS NOT NULL;

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