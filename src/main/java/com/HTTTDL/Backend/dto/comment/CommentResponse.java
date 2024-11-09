package com.HTTTDL.Backend.dto.comment;

import com.HTTTDL.Backend.model.GeoFeature;
import com.HTTTDL.Backend.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CommentResponse {
    Long id;
    String comment;
    int rating;
    GeoFeature feature;
    User author;
}
