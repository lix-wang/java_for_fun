package com.xiao.framework.base.jvm;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * 嵌入式注解处理器
 *
 * 支持所有的Annotations
 *
 * @author lix wang
 */
@SupportedAnnotationTypes("*")
public class NameCheckProcessor extends AbstractProcessor {
    private NameChecker nameChecker;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        nameChecker = new NameChecker(processingEnv);
    }

    /**
     * 对输入的语法树的各个节点进行名称检查
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            for (Element element : roundEnv.getRootElements()) {
                nameChecker.checkNames(element);
            }
        }
        return false;
    }

    private static class NameChecker {
        private final Messager messager;
        private ProcessingEnvironment processingEnvironment;

        private NameChecker(ProcessingEnvironment processingEnvironment) {
            this.processingEnvironment = processingEnvironment;
            this.messager = processingEnvironment.getMessager();
        }

        private void checkNames(Element element) {

        }
    }
}
