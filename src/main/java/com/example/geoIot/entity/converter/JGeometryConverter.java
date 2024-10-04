package com.example.geoIot.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Converter(autoApply = true)
@Component
public class JGeometryConverter implements AttributeConverter<JGeometry, Object> {
    @Autowired
    private DataSource dataSource;

    @Override
    public Object convertToDatabaseColumn(JGeometry jGeometry) {
        if (jGeometry == null) {
            return null;
        }
        try (Connection connection = dataSource.getConnection()){
            return JGeometry.store(jGeometry, connection);
        } catch (SQLException e) {
            throw new RuntimeException("Error in JGeometry conversion: ", e);
        }
    }

    @Override
    public JGeometry convertToEntityAttribute(Object o) {
        if (o == null) {
            return null;
        }
        try {
            return JGeometry.load((STRUCT) o);
        } catch (SQLException e) {
            throw new RuntimeException("Error in JGeometry conversion: ", e);
        }
    }
}
