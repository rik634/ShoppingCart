package com.code.shopping_cart.service.image;

import com.code.shopping_cart.Bean.ImageBean;
import com.code.shopping_cart.exception.ResourceNotFoundException;
import com.code.shopping_cart.model.Image;
import com.code.shopping_cart.model.Product;
import com.code.shopping_cart.repository.ImageRepository;
import com.code.shopping_cart.service.category.CategoryServiceImpl;
import com.code.shopping_cart.service.product.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service

public class ImageServiceImpl implements ImageService{

    private final ImageRepository imageRepository;
    private final ProductServiceImpl productService;

    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository,ProductServiceImpl productService) {
        this.imageRepository = imageRepository;
        this.productService=productService;
    }
    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Image not found with id: "+id));
    }

    @Override
    public void deleteImageById(Long id) {
        imageRepository.findById(id).ifPresentOrElse(imageRepository::delete,()-> {throw new ResourceNotFoundException("Image not found with id:");});
    }

    @Override
    public List<ImageBean> saveImages(List<MultipartFile> files, Long productId) {
        Product product = productService.getProductById(productId);
        List<ImageBean> savedImageBeanList = new ArrayList<>();
        for(MultipartFile file:files)
        {
            try{
                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProduct(product);

                String buildDownloadUrl = "/api/v1/images/image/download/";
                String downloadUrl = buildDownloadUrl+image.getId();
                image.setDownloadUrl(downloadUrl);
                Image savedImage = imageRepository.save(image);
                savedImage.setDownloadUrl(buildDownloadUrl+savedImage.getId());
                imageRepository.save(savedImage);
                ImageBean bean = new ImageBean();
                bean.setImageId(savedImage.getId());
                bean.setImageName(savedImage.getFileName());
                bean.setDownloadUrl(savedImage.getDownloadUrl());
                savedImageBeanList.add(bean);
            }
            catch(IOException | SQLException e){
                throw new RuntimeException(e.getMessage());
            }
        }
        return savedImageBeanList;

    }

    @Override
    public void updateImage(MultipartFile file, Long productId) {
        Image image = getImageById(productId);
        try {
            image.setFileName(file.getOriginalFilename());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}
