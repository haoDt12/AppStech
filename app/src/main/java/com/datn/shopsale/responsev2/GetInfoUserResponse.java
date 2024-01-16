package com.datn.shopsale.responsev2;

import com.datn.shopsale.modelsv2.User;

public class GetInfoUserResponse extends BaseResponse {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
