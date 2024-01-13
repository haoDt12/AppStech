package com.datn.shopsale.responsev2;

import com.datn.shopsale.modelsv2.Banner;

import java.util.ArrayList;
import java.util.List;

public class GetBannerResponse extends BaseResponse{

    public List<Banner> banner;

    public List<Banner> getBanner() {
        return banner;
    }

    public void setBanner(List<Banner> banner) {
        this.banner = banner;
    }
}
