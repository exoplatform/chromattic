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

import groovy.lang.GroovyClassLoader;
import org.chromattic.api.Chromattic;
import org.chromattic.api.ChromatticBuilder;
import org.chromattic.api.ChromatticSession;
import org.chromattic.ext.ntdef.NTFile;
import org.chromattic.ext.ntdef.NTFolder;
import org.chromattic.ext.ntdef.NTHierarchyNode;
import org.chromattic.ext.ntdef.NTResource;

import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ChromatticMetaModelService {

  /** . */
  private Chromattic chromattic;

  public ChromatticMetaModelService() {
  }

  public void start() {
    ChromatticBuilder builder = ChromatticBuilder.create();
    builder.add(NTFile.class);
    builder.add(NTFolder.class);
    builder.add(NTHierarchyNode.class);
    builder.add(NTResource.class);
    builder.add(GroovyResourceContainer.class);
    chromattic = builder.build();
  }

  public void stop() {

  }

  public void generateNodeType(List<String> dataObjectPaths) {

    ChromatticSession session = chromattic.openSession();


    // application/x-chromattic+groovy


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






  }

}
