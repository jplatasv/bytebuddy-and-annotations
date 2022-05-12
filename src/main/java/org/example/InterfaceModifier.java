package org.example;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import org.springframework.web.bind.annotation.RequestMapping;

public class InterfaceModifier {

    private static boolean agentInstalled;

    private InterfaceModifier() {

    }

    public static void modifyAndReload(Class<?> clazz) {

        if (!agentInstalled) {
            ByteBuddyAgent.install();
            agentInstalled = true;
        }

        DynamicType.Builder<?> builder = new ByteBuddy().redefine(clazz);

        RequestMapping originalAnnotation = getRequestMappingAnnotation(builder);

        builder.visit(MethodAnnotationMerger.normalize(originalAnnotation))
                .visit(ClassAnnotationRemover.remove(RequestMapping.class))
                .make()
                .load(clazz.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

    }

    private static RequestMapping getRequestMappingAnnotation(DynamicType.Builder<?> builder) {

        TypeDescription typeDescription = builder.toTypeDescription();
        AnnotationList annotationList = typeDescription.getInheritedAnnotations();

        AnnotationDescription.Loadable<RequestMapping> annotationDescription = annotationList.ofType(RequestMapping.class);

        return annotationDescription.load();
    }

}
