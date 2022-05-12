package org.example;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.springframework.web.bind.annotation.RequestMapping;

public class Main {
    public static void main(String[] args) {

        if (ExampleInterface.class.getAnnotations().length != 2) throw new RuntimeException("Does not comply with expected");

        try (ScanResult scanResult =
                     new ClassGraph()
                             .enableAnnotationInfo()
                             .acceptPackages("org.example")
                             .scan()) {
            for (ClassInfo routeClassInfo : scanResult.getClassesWithAnnotation(RequestMapping.class)) {

                Class<?> classToBeModified = routeClassInfo.loadClass(true);

                System.out.println("We found " + classToBeModified.getSimpleName());

                if (classToBeModified != null) {

                    InterfaceModifier.modifyAndReload(classToBeModified);

                }

            }

        }

        if (ExampleInterface.class.getAnnotations().length != 1) throw new RuntimeException("Does not comply with expected");
        if (((RequestMapping)ExampleInterface.class.getMethods()[0].getAnnotations()[0]).produces().length != 3) throw new RuntimeException("Does not comply with expected");

    }


}