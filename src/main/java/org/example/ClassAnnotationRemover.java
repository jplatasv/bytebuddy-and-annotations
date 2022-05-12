package org.example;

import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.AnnotationVisitor;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.pool.TypePool;

import java.lang.annotation.Annotation;

public class ClassAnnotationRemover extends AsmVisitorWrapper.AbstractBase {

    private final String annotationDescriptor;

    private ClassAnnotationRemover(Class<? extends Annotation> annotationType) {

        annotationDescriptor = Type.getDescriptor(annotationType);

    }

    public static ClassAnnotationRemover remove(Class<? extends Annotation> annotationType) {

        return new ClassAnnotationRemover(annotationType);

    }

    @Override
    public ClassVisitor wrap(TypeDescription instrumentedType, ClassVisitor classVisitor, Implementation.Context implementationContext, TypePool typePool, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, int writerFlags, int readerFlags) {

        return new ClassVisitor(Opcodes.ASM9, classVisitor) {
            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {

                if (descriptor.equals(annotationDescriptor)) {
                    System.out.println("Removing annotation "+descriptor);
                    return null;
                }

                return super.visitAnnotation(descriptor, visible);
            }
        };

    }

}
