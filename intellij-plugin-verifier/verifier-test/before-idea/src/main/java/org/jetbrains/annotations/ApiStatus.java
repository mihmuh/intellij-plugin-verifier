package org.jetbrains.annotations;

import java.lang.annotation.*;

/**
 * Copied from IDEA Community sources.
 */
public class ApiStatus {

  @Documented
  @Retention(RetentionPolicy.CLASS)
  @Target({
      ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PACKAGE
  })
  public @interface Experimental {
  }

  @Documented
  @Retention(RetentionPolicy.CLASS)
  @Target({
      ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PACKAGE
  })
  public @interface ScheduledForRemoval {
    String inVersion() default "";
  }

  @Documented
  @Retention(RetentionPolicy.CLASS)
  @Target({
      ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PACKAGE
  })
  public @interface Internal {
  }

  @Documented
  @Retention(RetentionPolicy.CLASS)
  @Target({
      ElementType.TYPE, ElementType.METHOD
  })
  public @interface NonExtendable {
  }

  @Documented
  @Retention(RetentionPolicy.CLASS)
  @Target({
      ElementType.TYPE, ElementType.METHOD
  })
  public @interface OverrideOnly {
  }
}
