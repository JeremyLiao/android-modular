package com.jeremyliao.compiler;

import com.google.auto.service.AutoService;
import com.jeremyliao.base.anotation.ModuleService;
import com.jeremyliao.base.inner.bean.ModuleServiceInfo;
import com.jeremyliao.base.inner.utils.GsonUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * Created by liaohailiang on 2018/8/30.
 */
@AutoService(Processor.class)
public class ModularProcessor extends AbstractProcessor {

    private static final String TAG = "[[ModularProcessor]]";
    private static final String MODULAR_PATH = "META-INF/modules/module_info/";
    private static final String MODULE_NAME = "moduleName";

    Filer filer;
    Types types;
    Elements elements;

    private String moduleName = null;
    private List<ModuleServiceInfo> moduleServiceInfos = new ArrayList<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        Map<String, String> options = processingEnvironment.getOptions();
        for (String key : options.keySet()) {
            String value = options.get(key);
            if (MODULE_NAME.equals(key)) {
                moduleName = value;
            }
        }
        if (moduleName == null || moduleName.length() == 0) {
            moduleName = getRandomString(10);
        }
        printLog("moduleName: " + moduleName);
        filer = processingEnvironment.getFiler();
        types = processingEnvironment.getTypeUtils();
        elements = processingEnvironment.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(ModuleService.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (roundEnvironment.processingOver()) {
            generateConfigFiles();
        } else {
            processService(roundEnvironment);
        }
        return true;
    }

    private void processService(RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(ModuleService.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                ModuleService moduleService = typeElement.getAnnotation(ModuleService.class);
                ModuleServiceInfo serviceInfo = new ModuleServiceInfo();
                serviceInfo.setSingleton(moduleService.singleton());
                List<? extends TypeMirror> typeMirrors = getInterface(moduleService);
                if (typeMirrors != null && !typeMirrors.isEmpty()) {
                    for (TypeMirror mirror : typeMirrors) {
                        if (mirror == null) {
                            continue;
                        }
                        String interfaceName = getClassName(mirror);
                        serviceInfo.setInterfaceClassName(interfaceName);
                    }
                }
                serviceInfo.setImplementClassName(typeElement.getQualifiedName().toString());
                printLog("serviceInfo: " + serviceInfo.toString());
                moduleServiceInfos.add(serviceInfo);
            }
        }
    }

    private void generateConfigFiles() {
        if (moduleServiceInfos.size() == 0) {
            return;
        }
        writeServiceFile(MODULAR_PATH + moduleName, GsonUtil.toJson(moduleServiceInfos));
    }

    private void writeServiceFile(String fileName, String content) {
        if (isEmpty(fileName) || isEmpty(content)) {
            return;
        }
        printLog("writeServiceFile fileName: " + fileName);
        printLog("writeServiceFile content: " + content);
        try {
            FileObject res = filer.createResource(StandardLocation.CLASS_OUTPUT, "", fileName);
            OutputStream os = res.openOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write(content);
            writer.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
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

    private static boolean isEmpty(String path) {
        return path == null || path.length() == 0;
    }

    private static String getClassName(TypeMirror typeMirror) {
        return typeMirror == null ? "" : typeMirror.toString();
    }

    private static String getRandomString(int length) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(3);
            long result = 0;
            switch (number) {
                case 0:
                    result = Math.round(Math.random() * 25 + 65);
                    sb.append((char) result);
                    break;
                case 1:
                    result = Math.round(Math.random() * 25 + 97);
                    sb.append((char) result);
                    break;
                case 2:
                    sb.append(new Random().nextInt(10));
                    break;
            }

        }
        return sb.toString();
    }

    private static void printLog(String log) {
        System.out.println(TAG + log);
    }
}
