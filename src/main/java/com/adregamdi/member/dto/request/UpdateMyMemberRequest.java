package com.adregamdi.member.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdateMyMemberRequest(
        @NotNull
        @NotEmpty
        String profile,
        @NotNull
        @NotEmpty
        String name,
        @NotNull
        @NotEmpty
        String handle
) {
}
