package com.jetbrains.pluginverifier.problems;

import com.google.common.base.Preconditions;
import com.jetbrains.pluginverifier.utils.MessageUtils;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.List;

@XmlRootElement
public class MethodNotImplementedProblem extends Problem {

  private String myMethod;

  public MethodNotImplementedProblem() {

  }

  public MethodNotImplementedProblem(@NotNull String method) {
    Preconditions.checkNotNull(method);
    myMethod = method;
  }

  public String getMethod() {
    return myMethod;
  }

  public void setMethod(String method) {
    myMethod = method;
  }

  @NotNull
  public String getDescription() {
    return "method isn't implemented" + " " + MessageUtils.convertMethodDescr(myMethod);
  }

  @NotNull
  @Override
  public Problem deserialize(@NotNull String... params) {
    return new MethodNotImplementedProblem(params[0]);
  }

  @NotNull
  @Override
  public List<Pair<String, String>> serialize() {
    return Collections.singletonList(new Pair<String, String>("method", myMethod));
  }

}
