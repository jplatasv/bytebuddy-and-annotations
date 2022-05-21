package org.example;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.jar.asm.AnnotationVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.utility.JavaConstant;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

public class RequestMappingVisitor extends AnnotationVisitor {

    private RequestMapping originalAnnotation;

    public RequestMappingVisitor(int api, AnnotationVisitor annotationVisitor, RequestMapping originalAnnotation) {
        super(api, annotationVisitor);
        this.originalAnnotation = originalAnnotation;
    }

    @Override
    public void visitEnd() {
        super.visit("headers", "example" );
        super.visitEnd();
    }

    @Override
    public void visitEnum(String name, String descriptor, String value) {
        super.visitEnum(name, descriptor, value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
        return super.visitAnnotation(name, descriptor);
    }

    @Override
    public void visit(String name, Object value) {
        super.visit(name, value);
    }

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

        } else if ("headers".equals(name)) {

            return new AnnotationVisitor(Opcodes.ASM9, super.visitArray(name)) {

                @Override
                public void visit(String name, Object value) {
                    super.visit(name, value);
                }

                @Override
                public void visitEnd() {
                    super.visitEnd();
                }
            };

        } else {

            return super.visitArray(name);

        }

    }

}
