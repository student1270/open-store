package ru.gb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gb.model.Review;
import ru.gb.repository.ReviewRepository;

import java.util.Collections;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> findReviewsByProductIdDesc(Long productId) {
        List<Review> reviews = reviewRepository.findReviewsByProductIdDesc(productId);
        return reviews != null ? reviews : Collections.emptyList();
    }

    public List<Review> findTop2ByProductIdOrderByLocalDateTimeDesc(Long productId) {
        List<Review> reviews = reviewRepository.findTop2ByProductIdOrderByLocalDateTimeDesc(productId);
        return reviews != null ? reviews : Collections.emptyList();
    }
}