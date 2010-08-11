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

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
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
      for (String chromatticSourcePath : SourceUtil.getChromatticPaths(element)) {
        String groovyPath = SourceUtil.groovyPath(chromatticSourcePath);
        try {
          InputStream is = processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH, "", chromatticSourcePath).openInputStream();
          CompilationUnit compilationUnit = JavaParser.parse(is);
          OutputStream os = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", groovyPath, element).openOutputStream();
          PrintWriter printWriter = new PrintWriter(os);
          GroovyFromJavaSourceChromatticBuilder builder = new GroovyFromJavaSourceChromatticBuilder(compilationUnit);
          builder.build();
          printWriter.append(builder.toString());
          printWriter.flush();
          printWriter.close();
        } catch (FilerException ignore) { // Source is already generated
        } catch (IOException e) {
          throw new TestGeneratorException(e.getMessage(), e);
        } catch (ParseException e) {
          throw new TestGeneratorException(e.getMessage(), e);
        } 
      }
    }
    return true;
  }
}