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
        boolean productPriceConditions = product.getPrice().compareTo(BigDecimal.ZERO) > 0 && product.getPrice() != null;
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
            product.setImagePath(imagePath); // ImagePath ni o‘rnatish
            productRepository.save(product);
            return true;
        }
        return false;
    }
}