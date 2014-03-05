package com.jetbrains.pluginverifier.verifiers.instruction;

import com.jetbrains.pluginverifier.VerificationContext;
import com.jetbrains.pluginverifier.problems.ClassNotFoundProblem;
import com.jetbrains.pluginverifier.problems.ProblemLocation;
import com.jetbrains.pluginverifier.resolvers.Resolver;
import com.jetbrains.pluginverifier.verifiers.util.VerifierUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

/**
 * @author Dennis.Ushakov
 */
public class TypeInstructionVerifier implements InstructionVerifier {
  public void verify(final ClassNode clazz, final MethodNode method, final AbstractInsnNode instr, final Resolver resolver, final VerificationContext ctx) {
    if (!(instr instanceof TypeInsnNode)) return;

    String className = ((TypeInsnNode)instr).desc;
    if (className.startsWith("[")) {
      className = VerifierUtil.extractClassNameFromDescr(className);
    }

    if(className == null || VerifierUtil.classExists(ctx.getOptions(), resolver, className)) return;

    ctx.registerProblem(new ClassNotFoundProblem(className), ProblemLocation.fromMethod(clazz.name, method.name + method.desc));
  }
}
