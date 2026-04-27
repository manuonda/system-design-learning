package de.rieckpil.courses.book.review;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(RandomReviewParameterResolverExtension.class)
class ReviewVerifierTest {


  private ReviewVerifier reviewVerifier;

  //Antes de cada ejecucion
  @BeforeEach
  void setup(){
    System.out.println("setup");
    reviewVerifier = new ReviewVerifier();
  }

  // despues de cada ejecucion
  @AfterEach
  void tearDown(){
    System.out.println("tearDown");
  }

  // se ejecuta una sola vez
  @BeforeAll
  static void beforeAll(){
    System.out.println("beforeAll");
  }

  //se ejecuta una sola vez
  @AfterAll
  static void afterAll(){
    System.out.println("afterAll");
  }

  @Test
  void shouldFailWhenReviewContainsSwearWord() {
    String reviewer = "This book is shit";
    boolean result = reviewVerifier.doesMeetQualityStandards(reviewer);
    System.out.println("result : " + result);

    assertFalse(result,"BookReviewVerifier did not detect swear word");
    assertEquals(false, result);

  }

  @Test
  @DisplayName("Should fail when review contains 'lorem ipsum'")
  void testLoremIpsum() {
    String review = """
      Lorem ipsum dolor sit amet, conseteur sadispcing elitr,
      sed diam nonumy eirmod tempor indvidunt ut laboret et
      dolore magna aliquyam era, sed diam voluptua.
      At vero eos et accusam et justo du dolores et ea rebum
      """;
    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(result, "Reviewer did not detect swear word");
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/badReview.csv")
  @DisplayName("ShouldFail when review Is of Bad Quality")
  void shouldFailWhenReviewIsOfBadQuality(String review){
    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(result, "Reviewer did not detect swear word");
  }


  @Test
  @DisplayName("Should Fail When RandomReview Quality is Bad. This is Extension")
  void shouldFailWhenRandomReviewQualityIsBad2(@RandomReviewParameterResolverExtension.RandomReview String review) {
    System.out.println(review);
    boolean result =  reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(result, "Reviewer did not detect random bad review");
  }

  @Test
  @DisplayName("Should Pass when review is good")
  void shouldPassWhenReviewIsGood() {
    String review = """
       I can tottally recommend this book,
       who is interested in learning how to write Java code!!!
      """;
    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    assertTrue(result, "Reviewer did not detect swear word");
  }



}
