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

import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import play.api.Play.current
import play.api.PlayException
import play.api.Configuration
import se.radley.plugin.salat.OptionsFromConfig

/**
 * Wrapper for the <code>ModelCompanion</code> class.
 *
 * @since 1.0
 * @see com.movus.salat.dao.ModelCompanion
 */
abstract class MeifanNetModelCompanion[ObjectType <: AnyRef](implicit mot :Manifest[ObjectType]) extends ModelCompanion[ObjectType, ObjectId] {
    /**
     * Load collection according to the model name.
     * @param c (String) model name
     *
     */
    def loadCollection(c: String = mot.runtimeClass.getSimpleName) = {
        DBDelegate.db(c)
    }
}

/**
 * Preserve a single connection pool.
 *
 */
object DBDelegate {
    /** default db host */
    final val DEFAULT_DB_HOST = "127.0.0.1"
    /** default db name  */
    final val DEFAULT_DB_NAME = "fashion-mongo"
    /** default image db  */
    final val DEFAULT_IMAGE_DB = "Picture"

    /** configuration */
    val conf  = current.configuration
    val options = OptionsFromConfig(conf.getConfig("mongodb.default.options")).get

    /** Limit connection counts */
    val builder = new MongoClientOptions.Builder()
    builder.connectionsPerHost(options.connectionsPerHost)
    builder.connectTimeout(options.connectTimeout)
    builder.threadsAllowedToBlockForConnectionMultiplier(options.threadsAllowedToBlockForConnectionMultiplier)

    /** connection pool */
    val mongoClient = MongoClient(
                        conf.getString("mongodb.default.host").getOrElse(DEFAULT_DB_HOST),
                        builder.build())

    /** database excluding pictures */
    val db = mongoClient(conf.getString("mongodb.default.db").getOrElse(DEFAULT_DB_NAME))

    /** database for pictures  */
    val picDB = mongoClient(conf.getString("mongodb.image.db").getOrElse(DEFAULT_IMAGE_DB))
}
