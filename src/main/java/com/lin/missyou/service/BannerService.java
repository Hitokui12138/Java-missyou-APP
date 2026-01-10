package com.lin.missyou.service;


import com.lin.missyou.model.Banner;
import org.springframework.stereotype.Service;


/**
 *
 */

public interface BannerService {
    Banner getByName(String name);
}
