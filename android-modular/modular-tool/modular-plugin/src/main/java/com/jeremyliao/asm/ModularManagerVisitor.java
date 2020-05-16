package com.jeremyliao.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by liaohailiang on 2019-09-30.
 */
public class ModularManagerVisitor extends ClassVisitor implements Opcodes {

    private static final String INJECT_METHOD_NAME = "getModuleJson";
    private final String outputJson;

    public ModularManagerVisitor(ClassVisitor cv, String json) {
        super(Opcodes.ASM5, cv);
        outputJson = json;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (!INJECT_METHOD_NAME.equals(name)) {
            return mv;
        }
        return new MethodVisitor(Opcodes.ASM5, mv) {
            @Override
            public void visitCode() {
                super.visitCode();
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitLineNumber(66, l0);
                mv.visitLdcInsn(outputJson);
                mv.visitInsn(ARETURN);
                mv.visitMaxs(1, 0);
                mv.visitEnd();
            }
        };
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
