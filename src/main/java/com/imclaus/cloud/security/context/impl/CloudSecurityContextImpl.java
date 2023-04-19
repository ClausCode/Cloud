package com.imclaus.cloud.security.context.impl;

import com.imclaus.cloud.models.UserModel;
import com.imclaus.cloud.security.context.CloudSecurityContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;

@Getter
@Setter
@NoArgsConstructor
public class CloudSecurityContextImpl extends SecurityContextImpl implements CloudSecurityContext {
    public CloudSecurityContextImpl(Authentication auth) {
        super(auth);
    }

    private String browserId;
    private UserModel currentUser;
}
