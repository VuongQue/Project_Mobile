package com.example.project_mobile.api;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryConfig {
    public static Cloudinary getCloudinaryInstance() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dbwzloucf",
                "api_key", "322178371811559",
                "api_secret", "ZTu1_QCWmcZOz4mQ17UXuekMJc8"
        ));
    }
}