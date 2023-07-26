package com.customerService.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@ApiModel(description = "Model of Customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Customer {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(notes = "The unique id of the Customer")
    private Long id;

    @ApiModelProperty(notes = "The unique email")
    @NotBlank(message = "Email cannot be null or blank")
    @Email(message = "Invalid email format")
    @Size(max = 50, message = "Email length must not exceed 50 characters")
    private String email;

    @ApiModelProperty(notes = "The Password")
    @Size(min = 5, max = 64, message = "Password should be min 5 characters and max 64 characters")
    @NotBlank(message = "Password cannot be Empty or blank")
    private String password;

    @ApiModelProperty(notes = "The first name")
    @NotBlank(message = "first name cannot be blank or null")
    @Size(max = 50, message = "First name length must not exceed 50 characters")
    private String firstName;

    @ApiModelProperty(notes = "The last name")
    @Size(max = 50, message = "Last name length must not exceed 50 characters")
    @NotBlank(message = "last name cannot be blank or null")
    private String lastName;

    @ApiModelProperty(notes = "The customer address")
    @Valid
    private Address address;
}
