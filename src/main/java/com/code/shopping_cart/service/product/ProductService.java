package com.code.shopping_cart.service.product;

import com.code.shopping_cart.Bean.ProductBean;
import com.code.shopping_cart.model.Product;
import com.code.shopping_cart.request.AddProductRequest;
import com.code.shopping_cart.request.ProductUpdateRequest;

import java.util.List;

public interface ProductService {
    Product addProduct(AddProductRequest product);
    Product getProductById(Long id);
    void deleteProductById(Long id);
    Product updateProduct(ProductUpdateRequest product, Long productId);
    List<Product> getAllProducts();

    List<Product> getProductsByCategory(String category);

    List<Product> getProductsByBrand(String brand);
    List<Product> getProductsByCategoryAndBrand(String category, String brand);
    List<Product> getProductsByName(String name);
    List<Product> getProductsByBrandAndName(String brand, String name);
    Long countProductsByBrandAndName(String brand, String name);

    ProductBean convertToBean(Product product);

    List<ProductBean> getConvertedProducts(List<Product> products);
}
