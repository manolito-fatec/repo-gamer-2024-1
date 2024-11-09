DROP SEQUENCE ADMIN.ISEQ$$_96866;

CREATE SEQUENCE ADMIN.ISEQ$$_96866
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE
    NOCYCLE;

ALTER TABLE location
    MODIFY id_location DEFAULT ADMIN.ISEQ$$_96866.NEXTVAL;

-------

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

-------

ALTER TABLE location
    ADD CONSTRAINT enforce_srid_4326 CHECK (poly.sdo_srid = 4326);