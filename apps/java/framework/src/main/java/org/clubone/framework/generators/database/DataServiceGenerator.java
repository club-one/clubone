/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clubone.framework.generators.database;

import java.io.File;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.Arrays;
import java.util.Locale;
import javax.sql.*;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author netuser
 */
@Slf4j
public class DataServiceGenerator {

    /**
     *
     * @param dataSource
     * @param query
     * @param tableName
     * @throws Exception
     */
    private void generate(DataSource dataSource, String query, String tableName) throws Exception {
        dataSource.getConnection().prepareStatement(query).setString(1, tableName);
    }

    /**
     *
     * @param dataSource
     * @param tableIntrospectionQuery
     * @param tableDefinitionFetchQuery
     * @param databaseName
     * @throws Exception
     */
    private void generate(DataSource dataSource, String tableIntrospectionQuery, String tableDefinitionFetchQuery, String databaseName) throws Exception {

        PreparedStatement statement = dataSource.getConnection().prepareStatement(tableIntrospectionQuery);
        if (databaseName != null) {
            statement.setString(1, databaseName);
        }

        ResultSet resultSet = statement.executeQuery(tableIntrospectionQuery);
        while (resultSet.next()) {
            generate(dataSource, tableDefinitionFetchQuery, resultSet.getString(0));
        }
    }

    public void compile(String className, String classContent) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        JavaFileObject file = new JavaSourceFromString(className, classContent);
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.getDefault(), Charset.defaultCharset());

        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
        CompilationTask task = compiler.getTask(new PrintWriter(System.err), fileManager, diagnostics, null, null, compilationUnits);

        boolean success = task.call();
        for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
            log.info("{}",diagnostic.getCode());
            log.info("{}",diagnostic.getKind());
            log.info("{}",diagnostic.getPosition());
            log.info("{}",diagnostic.getStartPosition());
            log.info("{}",diagnostic.getEndPosition());
            log.info("{}",diagnostic.getSource());
            log.info("{}",diagnostic.getMessage(null));
        }
        log.info("{}","Success: " + success);
        
        if (success) {
            
            File compileDirectory = new File("./");
            Arrays.asList(compileDirectory.list()).stream().forEach((x) -> log.info("{}","child file:"+x));
            
            ClassLoader classLoader = new URLClassLoader(new URL[]{compileDirectory.toURI().toURL()});
            Class cls = classLoader.loadClass(className);
            Object instance = cls.getConstructors()[0].newInstance();
            log.info("{}",cls.getMethod("toString", new Class[]{}).invoke(instance));
        }
    }

    public static void main(String[] args) throws Exception {
        String classContent
                = " public class HelloWorld {"
                + "  public static void main(String args[]) {"
                + "    log.info(\"{}\",\"This is in another java file\");"
                + "  }"
                + "}";

        new DataServiceGenerator().compile("HelloWorld", classContent);
    }
}

class JavaSourceFromString extends SimpleJavaFileObject {

    final String code;

    JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        System.out.print(">>> "+URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension));
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}
