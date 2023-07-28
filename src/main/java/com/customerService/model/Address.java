package com.customerService.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@ApiModel(description = "Model of Address")
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(notes = "The unique id of the Address")
    private Long id;

    @ApiModelProperty(notes = "The Phone number")
    @Size(max = 11, message = "Phone number should be max 11 characters")
    @NotBlank(message = "Phone number cannot be Empty or blank")
    private String phoneNumber;

    @ApiModelProperty(notes = "The Country")
    @Size(max = 30, message = "Country should be max 30 characters")
    @NotBlank(message = "Country cannot be Empty or blank")
    private String country;

    @ApiModelProperty(notes = "The city")
    @Size(max = 30, message = "City should be max 30 characters")
    @NotBlank(message = "City cannot be Empty or blank")
    private String city;

    @ApiModelProperty(notes = "The postalCode")
    @Size(max = 10, message = "postalCode should be max 10 characters")
    @NotBlank(message = "postalCode cannot be Empty or blank")
    private String postalCode;
}
