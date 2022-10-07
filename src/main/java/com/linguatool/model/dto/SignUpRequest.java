package com.linguatool.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

	private Long userID;

	private String providerUserId;

	@NotEmpty
	private String username;

	@NotEmpty
	private String email;

	private SocialProvider socialProvider;

	private String profilePicLink;

	@Size(min = 8, message = "{Size.userDto.password}")
	private String password;

}
