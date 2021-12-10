package com.tqz.business.service;

import com.tqz.business.model.request.ProductRatingRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:application.xml"})
public class RatingServiceTest {

    @Autowired
    RatingService ratingService;

    @Test
    public void productRatingOne() {
        assertFalse(ratingService.productRating(new ProductRatingRequest(-1, 160597, 4.0)));
    }

    @Test
    public void productRatingTwo() {
        assertFalse(ratingService.productRating(new ProductRatingRequest(63350368, -1, 4.0)));
    }

    @Test
    public void productRatingThree() {
        assertFalse(ratingService.productRating(new ProductRatingRequest(63350368, 160597, 100.0)));
    }

//    @Test
//    public void productRatingFour() {
//        assertFalse(ratingService.productRating(new ProductRatingRequest(63350368, 160597, -1.0)));
//    }

    @Test
    public void productRatingFive() {
        assertTrue(ratingService.productRating(new ProductRatingRequest(63350368, 160597, 4.0)));
    }

    @Test
    public void productRatingSix() {
        assertTrue(ratingService.productRating(new ProductRatingRequest(63350368, 160597, 5.0)));
    }
}