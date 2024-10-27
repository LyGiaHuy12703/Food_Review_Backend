package com.HTTTDL.Backend.dto.Geo;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.locationtech.jts.geom.Geometry;

import java.awt.*;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeoRequest {
    String name;
    double lat;
    double lon;
    String address;
    String phone;
    LocalTime open;
    LocalTime close;
    String advantage;
    String disadvantage;
    Float rate;
}
