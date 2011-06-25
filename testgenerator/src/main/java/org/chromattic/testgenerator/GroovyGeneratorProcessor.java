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

import japa.parser.ParseException;
import japa.parser.Parser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.visitor.DumpVisitorFactory;
import org.chromattic.testgenerator.builder.GroovyFromJavaSourceChromatticBuilder;
import org.chromattic.testgenerator.builder.GroovyFromJavaSourceTestBuilder;
import org.chromattic.testgenerator.visitor.renderer.GroovyCompatibilityFactory;
import org.chromattic.testgenerator.visitor.renderer.GroovyPropertiesFactory;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
@SupportedSourceVersion(SourceVersion.RELEASE_5)
@SupportedAnnotationTypes({"*"})
public class GroovyGeneratorProcessor extends AbstractProcessor
{
   enum Module
   {
      CORE("core"),
      MOTEAMODEL("metamodel");
     
      private String name;

      private Module (final String name)
      {
         this.name = name;
      }

      String get()
      {
         return name;
      }
   }

   private Filer filer;

  @Override
   public boolean process(final Set<? extends TypeElement> typeElements, final RoundEnvironment roundEnvironment)
   {
      filer = processingEnv.getFiler();

      TestSerializer serializer = new TestSerializer();
      if (roundEnvironment.processingOver())
      {
         try
         {
            Class c = Class.forName("load.Ref");
            for (Module module : Module.values())
            {
               InputStream is = c.getResource("testsRef-" + module.get() + ".xml").openStream();
               for (TestRef ref : serializer.getClassNames(is))
               {
                  writeGroovySource(GroovyOutputFormat.GETTER_SETTER, ref, module.get());
                  writeGroovySource(GroovyOutputFormat.PROPERTIES, ref, module.get());
                  writeGroovySource(GroovyOutputFormat.CHROMATTIC, ref, module.get());
               }
            }
         }
         catch (Exception e) { e.printStackTrace(); }
      }
      return false;
   }

   private void writeGroovySource(GroovyOutputFormat format, TestRef ref, String module) throws ParseException
   {

      try {
         switch(format)
         {
            case GETTER_SETTER:
               writeGroovyTest(format, ref, new GroovyCompatibilityFactory(), module);
               break;

            case PROPERTIES:
               writeGroovyTest(format, ref, new GroovyPropertiesFactory(), module);
               break;

            case CHROMATTIC:
               Set<String> chromatticClassNames = ref.getChromatticObject();
               for (String chromatticQualifiedClassName : chromatticClassNames)
               {
                  String name = format.getPackageName(chromatticQualifiedClassName).toString().replace(".", "/") + "/" + format.javaFileName(chromatticQualifiedClassName);
                  InputStream chromatticIs = filer.getResource(StandardLocation.SOURCE_PATH, "", module + "/src/test/java/" + name).openInputStream();
                  CompilationUnit chromatticUnit = Parser.parse(chromatticIs);
                  try
                  {
                     OutputStream chromatticOs = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, format.getPackageName(chromatticQualifiedClassName) + ".groovy", format.getClassName(chromatticQualifiedClassName) + ".groovy").openOutputStream();
                     GroovyFromJavaSourceChromatticBuilder chromatticBuilder = new GroovyFromJavaSourceChromatticBuilder(chromatticUnit);
                     chromatticBuilder.build();
                     SourceUtil.writeSource(chromatticBuilder.toString(), chromatticOs);
                  }
                  catch (FilerException ignore)
                  { /* already written */ }
               }
               break;
         }
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private void writeGroovyTest(GroovyOutputFormat format, TestRef ref, DumpVisitorFactory factory, String module) throws IOException, ParseException
   {

      // TODO : active excludedMethods
      String name = format.getPackageName(ref).toString().replace(".", "/") + "/" + format.javaFileName(ref);

      InputStream testIs = filer.getResource(StandardLocation.SOURCE_PATH, "", module + "/src/test/java/" + name).openInputStream();
      CompilationUnit testUnit = Parser.parse(testIs);
      FileObject jfo = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, format.getPackageName(ref), format.groovyFileName(ref));
      GroovyFromJavaSourceTestBuilder testBuilder = new GroovyFromJavaSourceTestBuilder(testUnit, format.testName(ref), ref.getChromatticObject());
      testBuilder.build(factory, new ArrayList<String>());
      SourceUtil.writeSource(testBuilder.toString(), jfo.openOutputStream());
   }
}
