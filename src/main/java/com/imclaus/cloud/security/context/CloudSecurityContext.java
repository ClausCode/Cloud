package com.imclaus.cloud.security.context;

import com.imclaus.cloud.models.UserModel;
import org.springframework.security.core.context.SecurityContext;

public interface CloudSecurityContext extends SecurityContext {
    UserModel getCurrentUser();
    String getBrowserId();
}
