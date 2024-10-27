package com.HTTTDL.Backend.dto.Geo;

import com.HTTTDL.Backend.model.Review;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeoResponse {
    Long id;
    String name;
    List<Double> point;
    String address;
    String phone;
    LocalTime open;
    LocalTime close;
    String advantage;
    String disadvantage;
    Float rate;
    List<Review> reviews;
    Set<String> images;
}
