package com.HTTTDL.Backend.dto.Geo;

import com.HTTTDL.Backend.model.GeoFeature;
import com.HTTTDL.Backend.model.Review;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeoResponse {
    String id;
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
