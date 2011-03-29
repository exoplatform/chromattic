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
import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
@SupportedSourceVersion(SourceVersion.RELEASE_5)
@SupportedAnnotationTypes({"org.chromattic.testgenerator.GroovyTestGeneration"})
public class GroovyGeneratorProcessor extends AbstractProcessor
{
   private Filer filer;
   private Set<String> generatedTests = new HashSet<String>();

   @Override
   public boolean process(final Set<? extends TypeElement> typeElements, final RoundEnvironment roundEnvironment)
   {
      filer = processingEnv.getFiler();
      for (Element element : roundEnvironment.getElementsAnnotatedWith(GroovyTestGeneration.class))
      {
         TypeElement typeElt = (TypeElement)element;
         try
         {
            writeGroovySource(GroovyOutputFormat.GETTER_SETTER, typeElt);
            writeGroovySource(GroovyOutputFormat.PROPERTIES, typeElt);
            writeGroovySource(GroovyOutputFormat.CHROMATTIC, typeElt);
         }
         catch (ParseException e)
         {
            e.printStackTrace();
         }

      }

      if (roundEnvironment.processingOver())
      {
         try
         {
            FileObject xmlFile = filer.createResource(StandardLocation.SOURCE_OUTPUT, "", "generatedTests.xml");
            TestSerializer xmlSerializer = new TestSerializer(generatedTests);
            Writer xmlWriter = xmlFile.openWriter();
            xmlSerializer.writeTo(xmlWriter);
            xmlWriter.close();
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
      return false;
   }

   private void writeGroovySource(GroovyOutputFormat format, TypeElement typeElt) throws ParseException
   {

      try {
         FileObject jfo = null;
         List<String> excludedMethods;
         List<String> chromatticClassNames = SourceUtil.getChromatticClassName(typeElt);
         try {
           excludedMethods = SourceUtil.excludedMethods(typeElt);
         } catch (TestGeneratorException tge) {
           excludedMethods = new ArrayList<String>();
         }
         switch(format)
         {
            case GETTER_SETTER:
               InputStream testIs = processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH, getPackageName(typeElt), typeElt.getSimpleName() + ".java").openInputStream();
               CompilationUnit testUnit = JavaParser.parse(testIs);
               jfo = filer.createResource(StandardLocation.SOURCE_OUTPUT, getPackageName(typeElt)/* + ".groovy"*/, "GroovyGetSet_" + typeElt.getSimpleName() + ".groovy");
               GroovyFromJavaSourceTestBuilder testBuilder = new GroovyFromJavaSourceTestBuilder(testUnit, "GroovyGetSet_" + typeElt.getSimpleName(), chromatticClassNames);
               testBuilder.build(new JavaToGroovySyntaxTransformer(), excludedMethods);
               SourceUtil.writeSource(testBuilder.toString(), jfo.openOutputStream());
               generatedTests.add(getPackageName(typeElt) + ".GroovyGetSet_" + typeElt.getSimpleName());
               break;

            case PROPERTIES:
               InputStream testIsProperties = processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH, getPackageName(typeElt), typeElt.getSimpleName() + ".java").openInputStream();
               CompilationUnit testUnitProperties = JavaParser.parse(testIsProperties);
               jfo = filer.createResource(StandardLocation.SOURCE_OUTPUT, getPackageName(typeElt)/* + ".groovy"*/, "GroovyProperties_" + typeElt.getSimpleName() + ".groovy");
               GroovyFromJavaSourceTestBuilder testBuilderProperties = new GroovyFromJavaSourceTestBuilder(testUnitProperties, "GroovyProperties_" + typeElt.getSimpleName(), chromatticClassNames);
               testBuilderProperties.build(new JavaToGroovyPropertiesSyntaxTransformer(), excludedMethods);
               SourceUtil.writeSource(testBuilderProperties.toString(), jfo.openOutputStream());
               generatedTests.add(getPackageName(typeElt) + ".GroovyProperties_" + typeElt.getSimpleName());
               break;

            case CHROMATTIC:
               for (String chromatticQualifiedClassName : chromatticClassNames)
               {
                  InputStream chromatticIs = processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH, getPackageName(chromatticQualifiedClassName), getClassName(chromatticQualifiedClassName) + ".java").openInputStream();
                  CompilationUnit chromatticUnit = JavaParser.parse(chromatticIs);
                  try
                  {
                     OutputStream chromatticOs = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, getPackageName(chromatticQualifiedClassName) + ".groovy", getClassName(chromatticQualifiedClassName) + ".groovy").openOutputStream();
                     GroovyFromJavaSourceChromatticBuilder chromatticBuilder = new GroovyFromJavaSourceChromatticBuilder(chromatticUnit);
                     chromatticBuilder.build();
                     SourceUtil.writeSource(chromatticBuilder.toString(), chromatticOs);
                  }
                  catch (FilerException ignore)
                  { /* Source is already generated */ }
               }
               break;
         }
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private CharSequence getPackageName(TypeElement typeElt)
   {
      return getPackageName(typeElt.getQualifiedName());
   }

   private CharSequence getPackageName(CharSequence typeName)
   {
      int lastIndex = typeName.toString().lastIndexOf(".");
      return typeName.subSequence(0, lastIndex);
   }

   private CharSequence getClassName(CharSequence typeName)
   {
      int lastIndex = typeName.toString().lastIndexOf(".") + 1;
      return typeName.subSequence(lastIndex, typeName.length());
   }
}
