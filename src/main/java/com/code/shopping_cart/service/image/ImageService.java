package com.code.shopping_cart.service.image;

import com.code.shopping_cart.Bean.ImageBean;
import com.code.shopping_cart.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    Image getImageById(Long id);
    void deleteImageById(Long id);
    List<ImageBean> saveImages(List<MultipartFile> files, Long productId);
    void updateImage(MultipartFile file, Long productId);
}
