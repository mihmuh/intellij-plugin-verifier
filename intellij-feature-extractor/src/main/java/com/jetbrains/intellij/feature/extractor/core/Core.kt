package com.jetbrains.intellij.feature.extractor.core

import org.jetbrains.intellij.plugins.internal.asm.Opcodes
import org.jetbrains.intellij.plugins.internal.asm.tree.AbstractInsnNode
import org.jetbrains.intellij.plugins.internal.asm.tree.ClassNode
import org.jetbrains.intellij.plugins.internal.asm.tree.FieldNode
import org.jetbrains.intellij.plugins.internal.asm.tree.MethodNode
import org.jetbrains.intellij.plugins.internal.asm.tree.analysis.Frame
import org.jetbrains.intellij.plugins.internal.asm.tree.analysis.Value
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun MethodNode.isAbstract(): Boolean = this.access and Opcodes.ACC_ABSTRACT != 0

fun FieldNode.isStatic(): Boolean = this.access and Opcodes.ACC_STATIC != 0

@Suppress("UNCHECKED_CAST")
fun ClassNode.findMethod(predicate: (MethodNode) -> Boolean): MethodNode? = (methods as List<MethodNode>).find(predicate)

@Suppress("UNCHECKED_CAST")
fun ClassNode.findField(predicate: (FieldNode) -> Boolean): FieldNode? = (fields as List<FieldNode>).find(predicate)

fun MethodNode.instructionsAsList(): List<AbstractInsnNode> = instructions.toArray().toList()

fun Frame.getOnStack(index: Int): Value? = this.getStack(this.stackSize - 1 - index)

inline fun <reified T> T.replicate(n: Int): List<T> = Array<T>(n) { this }.toList()

val LOG: Logger = LoggerFactory.getLogger("FeaturesExtractor")