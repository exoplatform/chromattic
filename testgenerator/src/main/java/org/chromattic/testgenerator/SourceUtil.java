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

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.ExecutableElement;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class SourceUtil {

  public static List<String> getChromatticPaths(Element classElement) {
    List<String> paths = new ArrayList<String>();
    List<AnnotationValue> annotationValues = getUniversalTestConfig(classElement, "chromatticClasses");
    for(AnnotationValue currentClass : annotationValues) {
      paths.add(classnameToPath(currentClass.toString()));
    }
    return paths;
  }

  public static String getTestPath(Element classElement) {
    return classnameToPath(getUniversalTestConfig(classElement, "sourceClass").toString() + ".class");
  }

  public static <T> T getUniversalTestConfig(Element classElement, String key) {
    for (AnnotationMirror annotationMirror : classElement.getAnnotationMirrors()) {
      for (ExecutableElement executableElement : annotationMirror.getElementValues().keySet()) {
        if (key.equals(executableElement.getSimpleName().toString())) {
          return (T) annotationMirror.getElementValues().get(executableElement).getValue();
        }
      }
    }
    throw new TestGeneratorException("Configuration key not found [@UniversalTest." + key + "()] for " + classElement.getSimpleName());
  }

  public static String classnameToPath(String classname) {
    return classname
      .replaceAll("\\.", "/")
      .replaceAll("/class$", ".java");
  }

  public static String groovyPath(String javaPath) {
    return javaPath.replaceAll("\\.java", ".groovy");
  }

  public static String sourceBaseDirectory(Element classElement) {
    return getUniversalTestConfig(classElement, "baseDir");
  }

  public static String suffixOf(Element classElement) {
    return getUniversalTestConfig(classElement, "suffix");
  }

  public static void writeSource(String code, OutputStream os) {
    PrintWriter printWriter = new PrintWriter(os);
    printWriter.append(code);
    printWriter.flush();
    printWriter.close();
  }
}
