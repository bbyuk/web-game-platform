package com.bb.webcanvasservice.user.domain.mapper;

import com.bb.webcanvasservice.user.domain.model.User;
import com.bb.webcanvasservice.user.domain.view.UserInfo;

/**
 * domain layer model <-> view mapper
 */
public class UserModelViewMapper {

    public static UserInfo toView(User model) {
        return new UserInfo(model.getId(), model.getFingerprint(), model.getRefreshToken(), model.getState());
    }
}
