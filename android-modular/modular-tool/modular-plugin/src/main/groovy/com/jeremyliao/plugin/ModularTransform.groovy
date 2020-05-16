package com.jeremyliao.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.gson.reflect.TypeToken
import com.jeremyliao.asm.ModularManagerVisitor
import com.jeremyliao.base.inner.bean.ModuleServiceInfo
import com.jeremyliao.base.inner.utils.GsonUtil
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.lang.reflect.Type
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.stream.Collectors
import java.util.zip.ZipEntry

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES

/**
 * Created by liaohailiang on 2018/12/26.
 */
class ModularTransform extends Transform {

    static final String TAG = "[[ModularTransform]]"
    static final String MODULAR_PATH = "META-INF/modules/module_info/"
    static final String MODULE_MANAGER_CLASS_NAME = "ModuleRpcManager.class"

    private final Project project

    private List<ModuleServiceInfo> moduleServiceInfos = new ArrayList<>();
    //输出的目标jar
    private JarInput outPutJarInput

    ModularTransform(Project project) {
        this.project = project
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        traversal(transformInvocation)
        handleOutputJarInputs(transformInvocation.outputProvider)
    }

    /**
     * 处理输入
     * @param transformInvocation
     */
    private void traversal(TransformInvocation transformInvocation) {
        printLog("处理输入")
        Collection<TransformInput> inputs = transformInvocation.inputs
        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        //遍历inputs
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                handleDirectoryInput(directoryInput, outputProvider)
            }

            input.jarInputs.each { JarInput jarInput ->
                handleJarInputs(jarInput, outputProvider)
            }
        }
    }

    @Override
    String getName() {
        return ModularTransform.simpleName
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    private void handleJarInputs(JarInput jarInput, TransformOutputProvider outputProvider) {
        boolean copyAfterProcess = true
        File file = jarInput.getFile()
        JarFile jarFile = new JarFile(file)
        Enumeration<JarEntry> entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement()
            def name = entry.getName()
            if (name.contains(MODULE_MANAGER_CLASS_NAME)) {
                printLog("ModuleRpcManager JarEntry name: " + name)
                printLog("ModuleRpcManager path: " + file.getAbsolutePath())
                outPutJarInput = jarInput
                copyAfterProcess = false
            }
            if (name.contains(MODULAR_PATH)) {
                printLog("JarEntry: " + entry)
                try {
                    InputStream inputStream = jarFile.getInputStream(entry)
                    String json = new BufferedReader(new InputStreamReader(inputStream))
                            .lines().parallel().collect(Collectors.joining(System.lineSeparator()))
                    Type type = new TypeToken<ArrayList<ModuleServiceInfo>>() {
                    }.getType()
                    ArrayList<ModuleServiceInfo> serviceInfos = GsonUtil.fromJson(json, type)
                    if (serviceInfos != null && serviceInfos.size() > 0) {
                        moduleServiceInfos.addAll(serviceInfos)
                    }
                    inputStream.close()
                } catch (IOException e) {
                    e.printStackTrace()
                }
            }
        }
        //处理完输入文件之后，要把输出给下一个任务
        if (copyAfterProcess) {
            File dest = outputProvider.getContentLocation(jarInput.name, jarInput.contentTypes,
                    jarInput.scopes, Format.JAR)
            FileUtils.copyFile(file, dest)
        }
    }

    private void handleDirectoryInput(DirectoryInput directoryInput, TransformOutputProvider outputProvider) {
        //是否是目录
        if (directoryInput.file.isDirectory()) {
            //列出目录所有文件（包含子文件夹，子文件夹内文件）
            directoryInput.file.eachFileRecurse { File file ->
                def name = file.name
                if (name.contains(MODULAR_PATH)) {
                    printLog('filename: ' + name)
                    try {
                        InputStream inputStream = new FileInputStream(file)
                        String json = new BufferedReader(new InputStreamReader(inputStream))
                                .lines().parallel().collect(Collectors.joining(System.lineSeparator()))
                        Type type = new TypeToken<ArrayList<ModuleServiceInfo>>() {
                        }.getType()
                        ArrayList<ModuleServiceInfo> serviceInfos = GsonUtil.fromJson(json, type)
                        if (serviceInfos != null && serviceInfos.size() > 0) {
                            moduleServiceInfos.addAll(serviceInfos)
                        }
                        inputStream.close()
                    } catch (IOException e) {
                        e.printStackTrace()
                    }
                }
            }
        }
        //处理完输入文件之后，要把输出给下一个任务
        def dest = outputProvider.getContentLocation(directoryInput.name,
                directoryInput.contentTypes, directoryInput.scopes,
                Format.DIRECTORY)
        FileUtils.copyDirectory(directoryInput.file, dest)
    }

    private void handleOutputJarInputs(TransformOutputProvider outputProvider) {
        if (outPutJarInput == null) {
            return
        }
        if (moduleServiceInfos == null || moduleServiceInfos.size() == 0) {
            return
        }
        JarFile jarFile = new JarFile(outPutJarInput.file)
        Enumeration enumeration = jarFile.entries()
        File tmpFile = new File(outPutJarInput.file.getParent() + File.separator + "classes_temp.jar")
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))
        //用于保存
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            String entryName = jarEntry.getName()
            ZipEntry zipEntry = new ZipEntry(entryName)
            InputStream inputStream = jarFile.getInputStream(jarEntry)
            if (entryName.contains(MODULE_MANAGER_CLASS_NAME)) {
                //class文件处理
                jarOutputStream.putNextEntry(zipEntry)
                ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                ClassVisitor cv = new ModularManagerVisitor(classWriter, GsonUtil.toJson(moduleServiceInfos))
                classReader.accept(cv, EXPAND_FRAMES)
                byte[] code = classWriter.toByteArray()
                jarOutputStream.write(code)
            } else {
                jarOutputStream.putNextEntry(zipEntry)
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            jarOutputStream.closeEntry()
        }
        //结束
        jarOutputStream.close()
        jarFile.close()
        File dest = outputProvider.getContentLocation(outPutJarInput.name,
                outPutJarInput.contentTypes, outPutJarInput.scopes, Format.JAR)
        printLog("src path: " + outPutJarInput.file.absolutePath)
        printLog("copy dest: " + dest.absolutePath)
        FileUtils.copyFile(tmpFile, dest)
        tmpFile.delete()
    }

    private static void printLog(String log) {
        println TAG + log
    }
}
