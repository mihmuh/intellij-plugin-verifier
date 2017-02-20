import com.jetbrains.pluginverifier.reference.SymbolicReference
import org.junit.Assert
import org.junit.Test

class TestDescriptors {

  @Test
  fun field() {
    val fieldFrom = SymbolicReference.fieldFrom("org/some/Class", "someField", "Ljava/lang/Object;")
    Assert.assertEquals("org.some.Class.someField : Object", fieldFrom.toString())
  }

  @Test
  fun method() {
    val methodFrom = SymbolicReference.methodFrom("org/some/Class", "someMethod", "(Ljava/lang/String;IF)Ljava/lang/Comparable;")
    Assert.assertEquals("org.some.Class.someMethod(String, int, float) : Comparable", methodFrom.toString())
  }

  @Test
  fun `class`() {
    val classFrom = SymbolicReference.classFrom("org/some/Class")
    Assert.assertEquals("org.some.Class", classFrom.toString())
  }
}