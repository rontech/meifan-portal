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
package com.meifannet.framework.db

import com.novus.salat._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
/**
 * Wrapper for the <code>SalatDAO</code> class.
 * Referring SalatDAO directly is not recommended since we are not sure
 * the continuity of SalatDAO class.
 *
 * @since 1.0
 * @see com.movus.salat.dao.SalatDAO
 */
class MeifanNetDAO[ObjectType <: AnyRef](override val collection: MongoCollection)(implicit mot: Manifest[ObjectType], mid: Manifest[ObjectId], ctx: Context)
  extends SalatDAO[ObjectType, ObjectId](collection)(mot, mid, ctx) {
}
