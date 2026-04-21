package de.rieckpil.courses.book.review;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewVerifierTest {


  @Test
  void shouldFailWhenReviewContainsSwearWord() {
    String reviewer = "This book is shit";
    ReviewVerifier reviewVerifier = new ReviewVerifier();
    boolean result = reviewVerifier.doesMeetQualityStandards(reviewer);
    System.out.println("result : " + result);

  }
}
