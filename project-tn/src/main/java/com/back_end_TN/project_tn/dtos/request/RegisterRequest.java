package com.back_end_TN.project_tn.dtos.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRequest {
    private String email;
    private String userName;
    private String password;
}
