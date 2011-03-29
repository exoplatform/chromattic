/*
* Copyright (C) 2003-2009 eXo Platform SAS.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

package org.chromattic.testgenerator;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import org.chromattic.testgenerator.sourcetransformer.GroovyFromJavaSourceChromatticBuilder;
import org.chromattic.testgenerator.sourcetransformer.GroovyFromJavaSourceTestBuilder;
import org.chromattic.testgenerator.sourcetransformer.JavaToGroovyPropertiesSyntaxTransformer;
import org.chromattic.testgenerator.sourcetransformer.JavaToGroovySyntaxTransformer;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
@SupportedSourceVersion(SourceVersion.RELEASE_5)
@SupportedAnnotationTypes({"org.chromattic.testgenerator.UniversalTest"})
public class TestGeneratorProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (Element element : roundEnv.getElementsAnnotatedWith(UniversalTest.class)) {
      String suffix = SourceUtil.suffixOf(element);
      String sourceBase = String.format("%s/src/test/java/", SourceUtil.sourceBaseDirectory(element));
      String testCompletSourcePath =  sourceBase + SourceUtil.getTestPath(element);
      List<String> excludedMethods;
      try {
        excludedMethods = SourceUtil.excludedMethods(element);
      } catch (TestGeneratorException tge) {
        excludedMethods = new ArrayList<String>();
      }
      String testGroovyPath = SourceUtil.groovyPath(testCompletSourcePath).replace(sourceBase, "").replaceAll("\\.groovy", "_" + suffix + ".groovy");
      String testPropertiesGroovyPath = SourceUtil.groovyPath(testCompletSourcePath).replace(sourceBase, "").replaceAll("\\.groovy", "_Property_" + suffix + ".groovy");
      try {
        InputStream testIs = processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH, "", testCompletSourcePath).openInputStream();
        CompilationUnit testUnit = JavaParser.parse(testIs);
        OutputStream testOs = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", testGroovyPath, element).openOutputStream();
        GroovyFromJavaSourceTestBuilder testBuilder = new GroovyFromJavaSourceTestBuilder(testUnit, suffix, null);
        testBuilder.build(new JavaToGroovySyntaxTransformer(), excludedMethods);
        SourceUtil.writeSource(testBuilder.toString(), testOs);

        //
        InputStream testPropertiesIs = processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH, "", testCompletSourcePath).openInputStream();
        CompilationUnit testPropertiesUnit = JavaParser.parse(testPropertiesIs);
        OutputStream testPropertiesOs = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", testPropertiesGroovyPath, element).openOutputStream();
        GroovyFromJavaSourceTestBuilder testPropertiesBuilder = new GroovyFromJavaSourceTestBuilder(testPropertiesUnit, "Property_" + suffix, null);
        testPropertiesBuilder.build(new JavaToGroovyPropertiesSyntaxTransformer(), excludedMethods);
        SourceUtil.writeSource(testPropertiesBuilder.toString(), testPropertiesOs);

        //
        for (String chromatticSourcePath : SourceUtil.getChromatticClassName(element)) {
          String chromatticCompletSourcePath = sourceBase + chromatticSourcePath;
          String chromatticGroovyPath = SourceUtil.groovyPath(chromatticSourcePath);
          InputStream chromatticIs = processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH, "", chromatticCompletSourcePath).openInputStream();
          CompilationUnit chromatticUnit = JavaParser.parse(chromatticIs);
          try {
            OutputStream chromatticOs = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", chromatticGroovyPath, element).openOutputStream();
            GroovyFromJavaSourceChromatticBuilder chromatticBuilder = new GroovyFromJavaSourceChromatticBuilder(chromatticUnit);
            chromatticBuilder.build();
            SourceUtil.writeSource(chromatticBuilder.toString(), chromatticOs);
          } catch (FilerException ignore) { /* Source is already generated */ }
        }
      } catch (IOException e) {
        throw new TestGeneratorException(e.getMessage(), e);
      } catch (ParseException e) {
        throw new TestGeneratorException(e.getMessage(), e);
      }
    }
    return true;
  }
}