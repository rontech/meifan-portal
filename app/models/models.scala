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

import com.novus.salat.{TypeHintFrequency, StringTypeHintStrategy, Context}
import play.api.Play
import play.api.Play.current
/*
  Adding a custom Salat context to work with Play's Classloader
  Using example from:
  https://github.com/leon/play-salat/blob/master/sample/app/models/mongoContext.scala
*/
package object mongoContext {
  implicit val context = {
  val context = new Context {
   val name = "global"
   override val typeHintStrategy = StringTypeHintStrategy(when = TypeHintFrequency.WhenNecessary, typeHint = "_t")
  }
  context.registerGlobalKeyOverride(remapThis = "id", toThisInstead = "_id")
  context.registerClassLoader(Play.classloader)
  context
  }
}

