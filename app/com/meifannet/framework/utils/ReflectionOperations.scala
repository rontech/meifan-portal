/**
 * RONTECH CONFIDENTIAL
 * __________________
 *
 *  [2014] - SuZhou Rontech Co.,Ltd.(http://www.sz-rontech.com)
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of SuZhou Rontech Co.,Ltd. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to SuZhou Rontech Co.,Ltd.
 * and its suppliers and may be covered by China and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from SuZhou Rontech Co.,Ltd..
 */
package com.meifannet.framework.utils

import scala.reflect.runtime.universe._
import scala.reflect.ClassTag

/**
 * Utils class using reflection to operate class/function/variables.
 *
 */
object ReflectionOperations {
  /**
   * Return a new instance with typeTag solution.
   * Private constructor cannot be invoked before 2.11.0-M6.
   */
  def newInstance[T: TypeTag]: T = {
    val clazz = typeTag[T].mirror.reflectClass(typeOf[T].typeSymbol.asClass)
    val init = typeOf[T].members.find { case m: MethodSymbol => m.isConstructor case _ => false }.get
    val ctor = clazz.reflectConstructor(init.asMethod)
    ctor().asInstanceOf[T]
  }

  /**
   * Get a field value of a class.
   * 取得指定类实例的指定字段的值
   *
   * @param inst Instance of a class. 类的实例
   * @param fieldName Class field name. 需要求值的字段名
   */
  def getFieldValueOfClass[T: ClassTag : TypeTag](inst: T, fieldName: String) = {

    try {
      // obtain the class mirror 
      val m = runtimeMirror(inst.getClass.getClassLoader) 
      // obtain the field method Symbol.
      val ftermsymb = typeOf[T].declaration(newTermName(fieldName)).asTerm
      // obtain the instance mirror.
      val im = m.reflect(inst)
      // obtain the field mirror using the field method Symbol to access the field.
      val fmirr = im.reflectField(ftermsymb)

      fmirr.get

    } catch {
      case ex: Exception => println(ex)
      ""
    }
  }
}
