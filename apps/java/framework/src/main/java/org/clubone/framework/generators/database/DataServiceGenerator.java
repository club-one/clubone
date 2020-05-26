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

        log.info("Compiling {}::>>{}", className, classContent);

        JavaFileObject file = new JavaSourceFromString(className, classContent);
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.getDefault(), Charset.defaultCharset());

        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
        CompilationTask task = compiler.getTask(new PrintWriter(System.err), fileManager, diagnostics, null, null, compilationUnits);

        boolean success = task.call();
        if (!success) {
            diagnostics.getDiagnostics().forEach((diagnostic) -> {
                log.warn("{}", diagnostic.getCode());
                log.warn("{}", diagnostic.getKind());
                log.warn("{}", diagnostic.getPosition());
                log.warn("{}", diagnostic.getStartPosition());
                log.warn("{}", diagnostic.getEndPosition());
                log.warn("{}", diagnostic.getSource());
                log.warn("{}", diagnostic.getMessage(Locale.getDefault()));
            });
        }

        File compileDirectory = new File("./");
        URL[] urls = new URL[]{compileDirectory.toURI().toURL()};
        ClassLoader classLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
        Class cls = classLoader.loadClass(className);
        Object instance = cls.getConstructors()[0].newInstance();
        log.info("{}", cls.getMethod("toString", new Class[]{}).invoke(instance));
    }

    public static void main(String[] args) throws Exception {
        String classContent
                = "public class HelloDate {"
                + " @Override public String toString(){"
                + "    return new java.util.Date().toString();"
                + "  }"
                + "}";

        new DataServiceGenerator().compile("HelloDate", classContent);
    }
}

class JavaSourceFromString extends SimpleJavaFileObject {

    final String code;

    public JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}
