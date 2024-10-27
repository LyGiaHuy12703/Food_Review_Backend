package com.HTTTDL.Backend.service;

import com.HTTTDL.Backend.configuration.CloudinaryConfig;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CloudinaryService {
    @Autowired
    private CloudinaryConfig cloudinary;

    public Set<String> uploadMultiImg(List<MultipartFile> files, String geoName) throws IOException {
        String sanitizedGeoName = geoName.trim();

        Set<String> urlList = new HashSet<>();
        for (MultipartFile file : files) {
            var result = cloudinary.cloudinary().uploader()
                    .upload(file.getBytes(), ObjectUtils.asMap(
                            "folder", "Geo/images/"+sanitizedGeoName
                    ));
            urlList.add((String) result.get("secure_url"));;
        }
        return urlList;
    }
}
