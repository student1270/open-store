package ru.gb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.gb.model.Category;
import ru.gb.model.Product;
import ru.gb.repository.ProductRepository;

import java.math.BigDecimal;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private CategoryService categoryService;

    public boolean saveProduct(Product product, MultipartFile image, String categoryPath) {
        boolean productNameConditions = product != null && product.getName() != null && !product.getName().trim().isEmpty() && product.getName().length() <= 100;
        boolean productPriceConditions = product.getPrice() != null && product.getPrice().compareTo(BigDecimal.ZERO) > 0;
        boolean productStockQuantityConditions = product.getStockQuantity() != null && product.getStockQuantity() > 0;
        boolean productDescriptionConditions = product.getDescription() != null && !product.getDescription().trim().isEmpty() && !product.getDescription().isBlank();
        String imagePath = imageService.uploadImage(image);
        boolean imagePathConditions = imagePath != null;

        if (productNameConditions && productPriceConditions && productStockQuantityConditions && productDescriptionConditions && imagePathConditions) {
            if (categoryPath != null && !categoryPath.trim().isEmpty()) {
                Category category = categoryService.findOrCreateCategory(categoryPath);
                if (category != null) {
                    product.setCategory(category);
                } else {
                    return false;
                }
            }
            product.setImagePath(imagePath);
            if (product.getCommentCount() == null) {
                product.setCommentCount(0);
            }
            productRepository.save(product);
            return true;
        }
        return false;
    }

    public Product findProductsById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }
}