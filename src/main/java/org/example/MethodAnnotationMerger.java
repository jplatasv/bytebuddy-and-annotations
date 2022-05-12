package org.example;

import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.*;
import net.bytebuddy.pool.TypePool;
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

                            return new AnnotationVisitor(Opcodes.ASM9, super.visitAnnotation(descriptor, visible)) {

                                @Override
                                public AnnotationVisitor visitArray(String name) {

                                    if ("produces".equals(name)) {

                                        return new AnnotationVisitor(Opcodes.ASM9, super.visitArray(name)) {
                                            @Override
                                            public void visit(String name, Object value) {

                                                // I'd like to receive an array as value, so I can provide one with all values merged

                                                boolean tryToMerge = false;

                                                if (tryToMerge) {

                                                    //I cannot return array with everything
                                                    Object[] newValue = new Object[]{value};
                                                    value = Arrays.copyOf(newValue, newValue.length + originalAnnotation.produces().length);
                                                    System.arraycopy(originalAnnotation.produces(), 0, value, newValue.length, originalAnnotation.produces().length);

                                                } else {

                                                    //I can only replace a single value
                                                    value = originalAnnotation.produces()[0];

                                                }

                                                // How to set an array in produces?

                                                super.visit(name, value);

                                            }
                                        };
                                    } else {

                                        return super.visitArray(name);

                                    }
                                }
                            };

                        } else {

                            return super.visitAnnotation(descriptor, visible);

                        }

                    }
                };
            }
        };

    }

}
