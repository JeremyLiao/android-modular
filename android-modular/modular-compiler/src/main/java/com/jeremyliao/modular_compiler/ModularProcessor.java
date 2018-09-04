package com.jeremyliao.modular_compiler;

import com.google.auto.service.AutoService;
import com.jeremyliao.modular_base.anotation.ModuleEvents;
import com.jeremyliao.modular_base.anotation.ModuleService;
import com.jeremyliao.modular_base.inner.bean.ModuleEventsInfo;
import com.jeremyliao.modular_base.inner.bean.ModuleInfo;
import com.jeremyliao.modular_base.inner.bean.ModuleServiceInfo;
import com.jeremyliao.modular_base.inner.utils.GsonUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import static com.google.common.base.Charsets.UTF_8;

/**
 * Created by liaohailiang on 2018/8/30.
 */
@AutoService(Processor.class)
public class ModularProcessor extends AbstractProcessor {

    private static final String TAG = "-----------ModularProcessor----------";
    private static final String MODULAR_PATH = "META-INF/modules/";
    private static final String MODULE_NAME = "moduleName";

    protected Filer filer;
    protected Types types;
    protected Elements elements;
    private String moduleName = null;

    private Map<String, ModuleInfo> moduleInfoMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        Map<String, String> options = processingEnvironment.getOptions();
        for (String key : options.keySet()) {
            String value = options.get(key);
            System.out.println(TAG + "key: " + key + " value: " + value);
            if (MODULE_NAME.equals(key)) {
                moduleName = value;
            }
        }
        System.out.println(TAG + "moduleName: " + moduleName);
        filer = processingEnvironment.getFiler();
        types = processingEnvironment.getTypeUtils();
        elements = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println(TAG + "process");
        if (roundEnvironment.processingOver()) {
            generateConfigFiles();
        } else {
            processAnnotations(roundEnvironment);
        }
        return true;
    }

    private void processAnnotations(RoundEnvironment roundEnvironment) {
        processService(roundEnvironment);
        processEvents(roundEnvironment);
    }

    private void processService(RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(ModuleService.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                ModuleService moduleService = typeElement.getAnnotation(ModuleService.class);
                System.out.println(TAG + typeElement.getSimpleName());
                System.out.println(TAG + typeElement.getQualifiedName());
                ModuleServiceInfo serviceInfo = new ModuleServiceInfo();
                serviceInfo.setModule(moduleService.module());
                serviceInfo.setSingleton(moduleService.singleton());
                System.out.println(TAG + moduleService);
                List<? extends TypeMirror> typeMirrors = getInterface(moduleService);
                if (typeMirrors != null && !typeMirrors.isEmpty()) {
                    for (TypeMirror mirror : typeMirrors) {
                        if (mirror == null) {
                            continue;
                        }
                        String interfaceName = getClassName(mirror);
                        System.out.println(TAG + "interfaceName: " + interfaceName);
                        serviceInfo.setInterfaceClassName(interfaceName);
                    }
                }
                serviceInfo.setImplementClassName(typeElement.getQualifiedName().toString());
                System.out.println(TAG + "serviceInfo: " + serviceInfo.toString());
                if (!moduleInfoMap.containsKey(moduleService.module())) {
                    moduleInfoMap.put(moduleService.module(), ModuleInfo.newInstance(moduleService.module()));
                }
                moduleInfoMap.get(moduleService.module()).getServiceInfos().add(serviceInfo);
            }
        }
    }

    private void processEvents(RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(ModuleEvents.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                ModuleEvents moduleEvents = typeElement.getAnnotation(ModuleEvents.class);
                System.out.println(TAG + typeElement.getSimpleName());
                System.out.println(TAG + typeElement.getQualifiedName());
                ModuleEventsInfo eventsInfo = new ModuleEventsInfo();
                eventsInfo.setModule(moduleEvents.module());
                List<String> events = new ArrayList<>();
                List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
                for (Element element1 : enclosedElements) {
                    System.out.println(TAG + "element1: " + element1);
                    if (element1.getKind() == ElementKind.FIELD) {
                        VariableElement ve = (VariableElement) element1;
                        Object constantValue = ve.getConstantValue();
                        System.out.println(TAG + "constantValue: " + constantValue);
                        if (constantValue != null && constantValue instanceof String) {
                            events.add((String) constantValue);
                        }
                    }
                }
                eventsInfo.setEvents(events);
                eventsInfo.setEvents(events);
                if (!moduleInfoMap.containsKey(moduleEvents.module())) {
                    moduleInfoMap.put(moduleEvents.module(), ModuleInfo.newInstance(moduleEvents.module()));
                }
                moduleInfoMap.get(moduleEvents.module()).setEventsInfo(eventsInfo);
            }
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(ModuleService.class.getCanonicalName());
        annotataions.add(ModuleEvents.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void generateConfigFiles() {
        System.out.println(TAG + "generateConfigFiles");
        if (moduleInfoMap.size() == 0) {
            System.out.println(TAG + "no module info found!!");
            return;
        }
        if (moduleInfoMap.size() > 1) {
            System.out.println(TAG + "more than one module info found, not allowed!!");
            return;
        }
        for (String name : moduleInfoMap.keySet()) {
            ModuleInfo moduleInfo = moduleInfoMap.get(name);
            writeServiceFile(MODULAR_PATH + moduleName, GsonUtil.toJson(moduleInfo));
        }
    }

    public void writeServiceFile(String fileName, String content) {
        if (isEmpty(fileName) || isEmpty(content)) {
            return;
        }
        System.out.println(TAG + "writeServiceFile");
        System.out.println(TAG + fileName);
        System.out.println(TAG + content);
        try {
            FileObject res = filer.createResource(StandardLocation.CLASS_OUTPUT, "", fileName);
            System.out.println(TAG + res.getName());
            OutputStream os = res.openOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, UTF_8));
            writer.write(content);
            writer.flush();
            os.close();
            System.out.println(TAG + "finish writeServiceFile");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(TAG + e.toString());
        }
    }

    private static List<? extends TypeMirror> getInterface(ModuleService service) {
        try {
            service.interfaceDefine();
        } catch (MirroredTypesException mte) {
            return mte.getTypeMirrors();
        }
        return null;
    }

    public static boolean isEmpty(String path) {
        return path == null || path.length() == 0;
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static String getClassName(TypeMirror typeMirror) {
        return typeMirror == null ? "" : typeMirror.toString();
    }
}
