package com.adregamdi.core.oauth2.dto;

import com.adregamdi.member.domain.SocialType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDTO {
    private String name;
    private String handle;
    private String email;
    private String age;
    private String gender;
    private String socialId;
    private SocialType socialType;
}
