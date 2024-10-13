package com.HTTTDL.Backend.dto.Api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class ApiResponse<T> {
    @Builder.Default
    private boolean success = true;
    private String code;
    private String message;
    private T data;
}
