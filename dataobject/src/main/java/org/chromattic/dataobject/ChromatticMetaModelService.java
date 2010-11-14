/*
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.chromattic.dataobject;

import org.exoplatform.services.jcr.ext.resource.UnifiedNodeReference;
import org.exoplatform.services.jcr.ext.script.groovy.JcrGroovyCompiler;
import org.exoplatform.services.jcr.ext.script.groovy.JcrGroovyResourceLoader;

import java.net.URL;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ChromatticMetaModelService {

  public ChromatticMetaModelService() {
  }

  public void start() {
  }

  public void stop() {

  }

  public Class[] generateClasses(
    String repository,
    String workspace,
    String path,
    String... dataObjectPaths) throws Exception {

    // Build the classloader url
    URL url = new URL("jcr://" + repository + "/" + workspace + "#" + path);

    //
    JcrGroovyCompiler compiler = new JcrGroovyCompiler();
    compiler.getGroovyClassLoader().setResourceLoader(new JcrGroovyResourceLoader(new java.net.URL[]{url}));

    //
    UnifiedNodeReference[] dataObjectRefs = new UnifiedNodeReference[dataObjectPaths.length];
    for  (int i = 0;i < dataObjectPaths.length;i++) {
      dataObjectRefs[i] = new UnifiedNodeReference(repository, workspace, dataObjectPaths[i]);
    }

    System.out.println("dataObjectRefs = " + dataObjectRefs);

    // Compile to classes
    Class[] classes = compiler.compile(dataObjectRefs);

    //
    return classes;



/*
    try {



//      BeanMappingBuilder mappingBuilder = new BeanMappingBuilder();
//      mappingBuilder.

      GroovyClassLoader loader = new GroovyClassLoader();



//      loader.getResourceLoader()

//      GroovyResourceLoader loader = new GroovyResourceLoader() {
//        public URL loadGroovySource(String filename) throws MalformedURLException {
//          return null;
//        }
//      };

      StringBuilder sb = new StringBuilder();

      for (String dataObjectPath : dataObjectPaths) {
        GroovyResourceContainer dataObjectFile = session.findByPath(GroovyResourceContainer.class, dataObjectPath);
        if (dataObjectFile == null) {
          throw new IllegalArgumentException("The path " + dataObjectPath + " is not valid");
        }
      }


    } finally {
      session.close();
    }
*/






  }

}
