package org.example;

import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.*;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.JavaConstant;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class MethodAnnotationMerger extends AsmVisitorWrapper.AbstractBase {

    private final RequestMapping originalAnnotation;

    private MethodAnnotationMerger(RequestMapping originalAnnotation) {

        this.originalAnnotation = originalAnnotation;

    }

    public static MethodAnnotationMerger normalize(RequestMapping originalAnnotation) {

        return new MethodAnnotationMerger(originalAnnotation);

    }

    @Override
    public ClassVisitor wrap(TypeDescription instrumentedType, ClassVisitor classVisitor, Implementation.Context implementationContext, TypePool typePool, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, int writerFlags, int readerFlags) {

        return new ClassVisitor(Opcodes.ASM9, classVisitor) {

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

                return new MethodVisitor(Opcodes.ASM9, super.visitMethod(access, name, descriptor, signature, exceptions)) {

                    @Override
                    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {

                        if (Type.getDescriptor(RequestMapping.class).equals(descriptor)) {

                            return new RequestMappingVisitor(Opcodes.ASM9, super.visitAnnotation(descriptor, visible), originalAnnotation);

                        } else {

                            return super.visitAnnotation(descriptor, visible);

                        }

                    }
                };
            }
        };

    }

}
