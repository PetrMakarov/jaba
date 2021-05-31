package com.pmakarov.jabaproxygen;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeSingleton;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author pmakarov
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class JabaProxyGen extends AbstractMojo {

    private Logger logger = Logger.getLogger(this.getClass());

    private final String postfixGeneratedName = "ProxyBinding";

    private final List<String> defaultExcludeFileNames = Collections.unmodifiableList(new ArrayList<String>() {{
        add("ObjectFactory");
    }});

    private final List<String> javaLangAndPrimitives = Collections.unmodifiableList(new ArrayList<String>() {{
        add("java.lang.Character");
        add("java.lang.Boolean");
        add("java.lang.Integer");
        add("java.lang.Double");
        add("java.lang.Float");
        add("java.lang.Number");
        add("java.lang.Short");
        add("java.lang.Byte");
        add("java.lang.Object");
        add("java.lang.String");
        add("int");
        add("float");
        add("double");
        add("byte");
        add("short");
        add("long");
        add("boolean");
        add("char");
    }});

    private final List<String> standartObjectMethods = Collections.unmodifiableList(new ArrayList<String>() {{
        add("toString");
        add("hashCode");
        add("equals");
    }});

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(property = "genOptions", alias = "options", required = true)
    private List<GenOptions> genOptions;

    @Parameter(property = "toTargetFolder", defaultValue = "true")
    private Boolean toTargetFolder;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        String outputBaseDirectory = JabaGenConstants.GENERATED_DIR;
        if (!toTargetFolder) {
            outputBaseDirectory = JabaGenConstants.SOURCE_DIR;
        }
        logger.debug("output base: " + outputBaseDirectory);

        String finalOutputBaseDirectory = outputBaseDirectory;
//        genOptions = new ArrayList<>();
//        genOptions.add(new com.pmakarov.jabaproxygen.GenOptions("source", "output", ""));
        List<JavaClass> allProxyClasses = getAllProxyClasses(genOptions);
        genOptions.forEach(genOption -> {
            logger.debug("Generate for:" + genOption.toString());
            JavaProjectBuilder builder = new JavaProjectBuilder();
            String outputDirectory = concatDirectories(finalOutputBaseDirectory, genOption.getGeneratedPackage());
            String sourceDirectory = concatDirectories(JabaGenConstants.SOURCE_DIR, genOption.getSourcePackage());
            logger.debug("source directory: " + sourceDirectory);
            logger.debug("output directory: " + outputDirectory);
//            builder.addSourceTree(new File("C:\\hg\\libs\\workenviroment\\src\\main\\java\\environment\\pojo\\repository"));
            builder.addSourceTree(new File(sourceDirectory));
            Set<String> excludes = getFullExcludes(genOption);
            List<JabaGeneratedClassEntry> classEntries = builder.getClasses().stream()
                    .filter(javaClass -> !excludes.contains(javaClass.getName()) && !javaClass.isEnum())
                    .map(javaClass -> {
                        logger.debug("generating code for: " + javaClass.getFullyQualifiedName());
                        JabaGeneratedClassEntry entry = new JabaGeneratedClassEntry();
                        entry.setPackageString(genOption.getGeneratedPackage());
                        entry.setImports(javaClass.getSource().getImports());
                        entry.setAnnotations(javaClass.getAnnotations().stream().map(JavaModel::getCodeBlock).collect(Collectors.toList()));
                        entry.setName(javaClass.getName() + postfixGeneratedName);
                        entry.setExtendsClassName(javaClass.getGenericFullyQualifiedName()); // maybe not
                        entry.setExtendsInstanceName(uncapitalizeFirst(javaClass.getName()));
                        entry.setProxyWrapeeString(getProxyWrappeeString(allProxyClasses, javaClass, entry));
                        entry.setMethods(getMethods(javaClass, entry));
                        return entry;
                    }).collect(Collectors.toList());
//            generateContent(classEntries, "C:\\hg\\libs\\jabaproxygen\\target\\generated-sources\\jabaproxy");
            generateContent(classEntries, outputDirectory);
        });

    }
//
//    public static void main(String[] args) throws MojoFailureException, MojoExecutionException {
//        new com.pmakarov.jabaproxygen.JabaProxyGen().execute();
//    }

    /**
     * Create list of methods of class
     *
     * @param javaClass  java class
     * @param classEntry class entry
     * @return list of java class methods entries
     */
    private List<JabaGeneratedClassMethodEntry> getMethods(JavaClass javaClass, JabaGeneratedClassEntry classEntry) {
        return javaClass.getMethods().stream()
                .filter(javaMethod -> isJavaLangOrPrimitiveMember(javaMethod))
                .map(javaMethod -> {
                    JabaGeneratedClassMethodEntry methodEntry = new JabaGeneratedClassMethodEntry();
                    methodEntry.setDeclareSignature(javaMethod.getDeclarationSignature(true));
                    String body = "";
                    //get parameter values names as string separated by comma
                    String parametersValue = String.join(",", javaMethod.getParameters().stream().map(JavaParameter::getName).collect(Collectors.toList()));
                    // if setter
                    if (javaMethod.isPropertyMutator()) {
                        body = "this." + classEntry.getExtendsInstanceName() + "." + javaMethod.getCallSignature() + ";" + System.lineSeparator();
                        body = body + "        valuePropagator.execute(\"" + javaMethod.getPropertyName() + "\"," + parametersValue + ");";
                    }
                    // if getter
                    if (javaMethod.isPropertyAccessor() || standartObjectMethods.contains(javaMethod.getName())) {
                        body = "return this." + classEntry.getExtendsInstanceName() + "." + javaMethod.getCallSignature() + ";";
                    }
                    methodEntry.setCallSignature(body);
                    return methodEntry;
                }).collect(Collectors.toList());
    }

    /**
     * Proxy wrap string for object tree construction
     *
     * @param allProxyClasses all parsed classes(files)
     * @param currentClass    current class
     * @param currentEntry    current class entry
     * @return generated string
     */
    private String getProxyWrappeeString(List<JavaClass> allProxyClasses, JavaClass currentClass, JabaGeneratedClassEntry currentEntry) {
        List<String> wrappers = new ArrayList<>();
        currentClass.getFields().stream()
                .filter(javaField -> (findProxyClassByType(allProxyClasses, javaField.getType().getGenericFullyQualifiedName())
                        || isProxyGenericCollection(allProxyClasses, javaField.getType().getGenericFullyQualifiedName()))
                        && !javaField.getType().isEnum())
                .forEach(javaField -> {
                    String getter = getGetterMethodNameByField(currentClass, javaField);
                    String setter = getSetterMethodNameByField(currentClass, javaField);
                    if (null != getter) {
                        String wrappee = "";
                        String fullType = javaField.getType().getGenericFullyQualifiedName();
                        if (isProxyGenericCollection(allProxyClasses, fullType)) {
                            String simpleName = getClassName(allProxyClasses, getSingleGeneric(fullType));
                            String labmdaObj = javaField.getName() + "$";
                            wrappee = "this." + javaField.getName()
                                    + " = " + currentEntry.getExtendsInstanceName() + "." + getter + "()"
                                    + ".stream().map(" + labmdaObj + " -> {return new " + simpleName + postfixGeneratedName
                                    + "(" + labmdaObj + ");}).collect(java.util.stream.Collectors.toList());";
                        } else {
                            wrappee = "this." + javaField.getName()
                                    + " = new " + javaField.getType().getName() + postfixGeneratedName
                                    + "(" + currentEntry.getExtendsInstanceName() + "." + getter + "());";
                        }
                        wrappers.add(wrappee);
                    }
                });
        return String.join(System.lineSeparator() + "        ", wrappers);
    }

    /**
     * Find parsed class by full type
     *
     * @param allProxyClasses all parsed classes
     * @param fullType        full type
     * @return boolean result
     */
    private boolean findProxyClassByType(List<JavaClass> allProxyClasses, String fullType) {
        System.out.println();
        return allProxyClasses.stream()
                .anyMatch(javaClass -> Objects.equals(javaClass.getFullyQualifiedName(), fullType));
    }

    /**
     * Determines whether or not type is generic collection
     *
     * @param allProxyClasses all parsed classes
     * @param fullType        full type
     * @return boolean result
     */
    private boolean isProxyGenericCollection(List<JavaClass> allProxyClasses, String fullType) {
        if (isSingleGeneric(fullType)) {
            return findProxyClassByType(allProxyClasses, getSingleGeneric(fullType));
        }
        return false;
    }

    /**
     * Determines generic type
     *
     * @param type type
     * @return boolean result
     */
    private boolean isSingleGeneric(String type) {
        return null != type && !type.isEmpty() && type.contains("<") && type.contains(">") && !type.contains(",");
    }

    /**
     * Get generic from type
     *
     * @param type full type
     * @return generic
     */
    private String getSingleGeneric(String type) {
        return type.substring(type.indexOf("<") + 1, type.indexOf(">"));
    }

    /**
     * Get class name
     *
     * @param allProxyClasses all parsed classes
     * @param fullName        ful name
     * @return class
     */
    private String getClassName(List<JavaClass> allProxyClasses, String fullName) {
        return allProxyClasses.stream()
                .map(JavaClass::getName)
                .filter(name -> Objects.equals(name, fullName))
                .findFirst()
                .orElse(fullName.substring(fullName.lastIndexOf('.') + 1).trim());
    }

    /**
     * Getter method name by parsed java field
     *
     * @param cuurentClass current class
     * @param field        field
     * @return getter name
     */
    private String getGetterMethodNameByField(JavaClass cuurentClass, JavaField field) {
        return cuurentClass.getMethods().stream()
                .filter(javaMethod -> javaMethod.isPropertyAccessor() && Objects.equals(javaMethod.getPropertyName(), field.getName()))
                .map(JavaMember::getName)
                .findFirst()
                .orElse(null);
    }

    /**
     * Setter method name by parsed java field
     *
     * @param cuurentClass current class
     * @param field        field
     * @return setter name
     */
    private String getSetterMethodNameByField(JavaClass cuurentClass, JavaField field) {
        return cuurentClass.getMethods().stream()
                .filter(javaMethod -> javaMethod.isPropertyMutator() && Objects.equals(javaMethod.getPropertyName(), field.getName()))
                .map(JavaMember::getName)
                .findFirst()
                .orElse(null);
    }

    /**
     * Generate excludes files set
     *
     * @param genOptions current option entry
     * @return excludes as set
     */
    private Set<String> getFullExcludes(GenOptions genOptions) {
        Set<String> excludes = new HashSet<>(Arrays.asList(genOptions.getExclude().split("\\s*,\\s*")));
        excludes.addAll(defaultExcludeFileNames);
        excludes.remove("");
        return excludes;
    }

    /**
     * Get all classes that will be parsed (passed in maven plugin)
     *
     * @param genOptions list of options entry
     * @return list of all classes to be generated
     */
    private List<JavaClass> getAllProxyClasses(List<GenOptions> genOptions) {
        List<JavaClass> allProxyClasses = new ArrayList<>();
        logger.debug("getting proxy classes tree");
        genOptions.forEach(genOption -> {
            logger.debug("current options: " + genOption);
            JavaProjectBuilder builder = new JavaProjectBuilder();
            String source = concatDirectories(JabaGenConstants.SOURCE_DIR, genOption.getSourcePackage());
            logger.debug("current source: " + source);
//            builder.addSourceTree(new File("C:\\hg\\libs\\workenviroment\\src\\main\\java\\environment\\pojo\\repository"));
            builder.addSourceTree(new File(source));
            allProxyClasses.addAll(
                    builder.getClasses().stream()
                            .filter(javaClass -> !getFullExcludes(genOption).contains(javaClass.getName()))
                            .collect(Collectors.toList())
            );
        });
        return allProxyClasses;
    }

    /**
     * Generate java proxy files by parsed entries
     *
     * @param entries   parsed entries
     * @param outputDir output directory
     */
    private void generateContent(List<JabaGeneratedClassEntry> entries, String outputDir) {
        // set up velocity engine
        logger.debug(entries.size() + " files will be generated");
        VelocityEngine velocityEngine = new VelocityEngine();
        Properties p = new Properties();
        p.setProperty("resource.loader", "class");
        p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        p.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogChute");
        velocityEngine.init(p);
        // try to get and check resource file
        String template = "templates/class.vm";
        Map<String, String> param = new HashMap<>();
        param.put("resources", "/templates");
        boolean isResourceExists = velocityEngine.resourceExists(template);
        if (isResourceExists) {
            try {
                tryDeleteDirectory(outputDir);
                logger.debug("clear directory: " + outputDir);
            } catch (IOException e) {
                logger.error(e);
            }
            // try create file on each entry
            entries.forEach(jabaGeneratedClassEntry -> {
                // create context
                VelocityContext context = new VelocityContext(param);
                context.put("entry", jabaGeneratedClassEntry);
                Writer writer = new StringWriter();
                velocityEngine.mergeTemplate(template, RuntimeSingleton.getString("input.encoding", "utf-8"), context, writer);
                String fileContent = writer.toString();
                //try create output folder
                try {
                    //recreate out dir
                    tryCreateDir(outputDir);
                    logger.debug("try create " + jabaGeneratedClassEntry.getName());
                } catch (IOException e) {
                    logger.error(e);
                }
                //try to write content to file
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputDir + File.separator + jabaGeneratedClassEntry.getName() + ".java"))) {
                    bufferedWriter.write(fileContent);
                } catch (IOException e) {
                    logger.error(e);
                }
            });
        }
    }

    /**
     * Determine whether java method is setter or getter of primitive object type
     *
     * @param javaMethod method
     * @return boolean result
     */
    private boolean isJavaLangOrPrimitiveMember(JavaMethod javaMethod) {
        return javaLangAndPrimitives.contains(javaMethod.getReturns().getFullyQualifiedName())
                || null != javaMethod.getParameters()
                && javaMethod.getParameters().size() == 1
                && javaLangAndPrimitives.contains(javaMethod.getParameters().get(0).getFullyQualifiedName());
    }

    /**
     * Concat directories (or package)
     *
     * @param base        directory
     * @param distPackage package
     * @return concatenated directory
     */
    private String concatDirectories(String base, String distPackage) {
        return Paths.get(base, distPackage.split("\\.")).toString();
    }

    /**
     * Try create passed directory
     *
     * @param directory directory
     * @throws IOException creation exception
     */
    private void tryCreateDir(String directory) throws IOException {
        File folder = new File(directory);
        if (!folder.exists()) {
            folder.mkdirs();
        }
//        Path path = Paths.get(directory);
//        if (!Files.exists(path)) {
//            Files.createDirectory(path);
//        }
    }

    /**
     * Try remove passed directory
     *
     * @param directory dir
     * @throws IOException remove exception
     */
    private void tryDeleteDirectory(String directory) throws IOException {
        Path path = Paths.get(directory);
        if (Files.exists(path)) {
            Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
    }

    /**
     * Lower case first char
     *
     * @param input input string
     * @return input string with uncapitalize first char
     */
    private String uncapitalizeFirst(String input) {
        if (null == input || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }
}
