package org.chromattic.docs.demo.shoppingcart

import junit.framework.TestCase
import org.chromattic.api.ChromatticBuilder
import org.reflext.api.ClassTypeInfo
import org.reflext.jlr.JavaLangReflectReflectionModel
import org.reflext.core.TypeResolverImpl
import org.chromattic.api.ChromatticBuilder.Option.Type
import org.reflext.api.TypeResolver
import org.chromattic.metamodel.typegen.SchemaBuilder
import org.chromattic.metamodel.typegen.XMLNodeTypeSerializer

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

class ShoppingCartTestCase extends TestCase {

  void _testSchema() {
    def domain = TypeResolverImpl.create(JavaLangReflectReflectionModel.getInstance())
    def javaTypes = [domain.resolve(Product),domain.resolve(ItemToPurchase),domain.resolve(ShoppingCart)] as Set
    def nodeTypes = new SchemaBuilder().build(javaTypes).values()
    def serializer = new XMLNodeTypeSerializer()
    serializer.addPrefixMapping("shop", "shop")
    nodeTypes.each { serializer.addNodeType(it) }
    def writer = new StringWriter();
    serializer.writeTo(writer);
    def xml = writer.toString();
    System.out.println("xml = " + xml);
  }

  void testFoo() {

    def builder = ChromatticBuilder.create()
    builder.add(Product)
    builder.add(ItemToPurchase)
    builder.add(ShoppingCart)
    def chromattic = builder.build()

    def session = chromattic.openSession()

    // Insert products
    def macbookpro = session.insert(Product, "macbookpro")
    macbookpro.description = "The MacBook Pro"
    macbookpro.price = 2500;
    def macpro = session.insert(Product, "macpro")
    macpro.description = "The Mac Pro"
    macpro.price = 3000;
    def macbookair = session.insert(Product, "macbookair")
    macbookair.description = "The MacBook Air"
    macbookair.price = 1000;
    session.save()

    // Make some shopping
    def cart = session.insert(ShoppingCart, "julien")
    def purchase1 = session.create(ItemToPurchase, "line1")
    cart.items.add(purchase1)
    purchase1.product = macbookpro
    def purchase2 = session.create(ItemToPurchase, "line2")
    cart.items.add(purchase2)
    purchase2.product = macpro
    purchase2.quantity = 2;
    session.save()

    //
    assertEquals(8500, cart.totalMoney)
  }
}
