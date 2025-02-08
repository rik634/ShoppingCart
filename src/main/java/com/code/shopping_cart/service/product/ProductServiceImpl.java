package com.code.shopping_cart.service.product;

import com.code.shopping_cart.Bean.ImageBean;
import com.code.shopping_cart.Bean.ProductBean;
import com.code.shopping_cart.exception.ProductNotFoundException;
import com.code.shopping_cart.model.Category;
import com.code.shopping_cart.model.Image;
import com.code.shopping_cart.model.Product;
import com.code.shopping_cart.repository.CategoryRepository;
import com.code.shopping_cart.repository.ImageRepository;
import com.code.shopping_cart.repository.ProductRepository;
import com.code.shopping_cart.request.AddProductRequest;
import com.code.shopping_cart.request.ProductUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import java.util.List;
import java.util.Optional;

@Service

public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ImageRepository imageRepository;
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper, ImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.categoryRepository=categoryRepository;
        this.modelMapper = modelMapper;
        this.imageRepository = imageRepository;
    }
    @Override
    public Product addProduct(AddProductRequest request) {
        //check if the category is found in the DB
        //if yes, set it as the new product in that category
        //if no, then save it as a new category
        //and set it as new product in that category
        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(()->{
                    Category newCategory = new Category(request.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });
        request.setCategory(category);
        return productRepository.save(createProduct(request,category));
    }

    private Product createProduct(AddProductRequest request, Category category)
    {
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                request.getCategory()
        );
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException("Product not found"));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id)
                .ifPresentOrElse(productRepository::delete,()->{throw new ProductNotFoundException("Product not found!");});
    }

    @Override
    public Product updateProduct(ProductUpdateRequest request, Long productId) {
        return productRepository.findById(productId)
                .map(existingProduct->updateExistingProduct(existingProduct,request))
                .map(productRepository::save)
                .orElseThrow(()-> new ProductNotFoundException("Product not found!"));
    }

    private Product updateExistingProduct(Product existingProduct, ProductUpdateRequest request)
    {
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());

        Category category = categoryRepository.findByName(request.getCategory().getName());
        existingProduct.setCategory(category);
        return existingProduct;
    }
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category,brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand,name);
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand,name);
    }

    @Override
    public ProductBean convertToBean(Product product) {
        ProductBean productDto = modelMapper.map(product, ProductBean.class);
        List<Image> images = imageRepository.findByProductId(product.getId());
        List<ImageBean> imageDtos = images.stream()
                .map(image -> modelMapper.map(image, ImageBean.class))
                .toList();
        productDto.setImages(imageDtos);
        return productDto;
    }

   @Override
   public List<ProductBean> getConvertedProducts(List<Product> products) {
        return products.stream().map(this::convertToBean).toList();
    }
}
