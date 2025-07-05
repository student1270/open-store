package ru.gb.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.model.Review;
import ru.gb.repository.ReviewRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;


    public List<Review> findReviewsByProductIdDesc(Long productId) {
        return reviewRepository.findByProductIdOrderByLocalTimeDesc(productId);
    }
    public List<Review> findTop2ByProductIdOrderByLocalDateTimeDesc(Long productId){
        return reviewRepository.findTop2ByProductIdOrderByLocalDateTimeDesc(productId);
    }
}
