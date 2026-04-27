package de.rieckpil.courses.book.review;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomReviewParameterResolverExtension implements ParameterResolver {

  private static final List<String> badReviews = List.of(
    """
      This book was shit I dont't like it,
      I was reading the book and I think the book is okay. I have aread better books and I think
      I know what's goot, Good book with good agende and good example.
      I can recommend for everyong
      """);


  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.PARAMETER)
  public @interface RandomReview {

  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
     return parameterContext.isAnnotated(RandomReview.class);
   }

  @Override
  public @Nullable Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return badReviews.get(ThreadLocalRandom.current().nextInt(badReviews.size()));
  }
}
