package ru.gb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gb.model.Product;
import ru.gb.repository.ProductRetrievalRepository;

import java.util.List;

@Service
public class ProductRetrievalService {

    @Autowired
    private ProductRetrievalRepository productRetrievalRepository;

    public List<Product> findProductsByCategory(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Kategoriya ID si bo'sh bo'lmasligi kerak");
        }
        List<Product> products = productRetrievalRepository.findProductsByCategory(categoryId);
        if (products.isEmpty()) {
            return products;
        }
        return products;
    }
    public List<Product> findProductsByCategoryAndPriceAsc(Long categoryId){
        if (categoryId == null) {
            throw new IllegalArgumentException("Kategoriya ID si bo'sh bo'lmasligi kerak");
        }
        List<Product> products = productRetrievalRepository.findProductsByCategoryAndPriceAsc(categoryId);
        if (products.isEmpty()) {
            return products;
        }
        return products;
    }
    public List<Product> findProductsByCategoryAndPriceDesc(Long categoryId){
        if (categoryId == null) {
            throw new IllegalArgumentException("Kategoriya ID si bo'sh bo'lmasligi kerak");
        }
        List<Product> products = productRetrievalRepository.findProductsByCategoryAndPriceDesc(categoryId);
        if (products.isEmpty()) {
            return products;
        }
        return products;
    }
}