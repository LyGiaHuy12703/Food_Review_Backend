package com.HTTTDL.Backend.dto.comment;

import com.HTTTDL.Backend.model.GeoFeature;
import com.HTTTDL.Backend.model.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ReviewResponse {
    String id;
    String comment;
    int rating;
    GeoFeature geoFeatures;
    User user;
}
