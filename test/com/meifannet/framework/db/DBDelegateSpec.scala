package com.meifannet.framework.db

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import org.specs2.specification._

import com.meifannet.framework.utils._

/**
 * Spec class for com.meifannet.framework.db.DBDelegate.
 *
 */
@RunWith(classOf[JUnitRunner])
class DBDelegateSpec extends Specification {
  sequential
  /** no props */
  val noDefaultMap = Map("test" -> "test")
  /** all props */
  val allPropsMap = Map("mongodb.default.db" -> "dummy_db",
    "mongodb.image.db" -> "dummy_image",
    "mongodb.default.host" -> "localhost",
    "mongodb.default.options" ->
      Map("connectionsPerHost" -> "333",
        "connectTimeout" -> "555",
        "threadsAllowedToBlockForConnectionMultiplier" -> "777"))

  /**
   * test cases.
   */
  "DBDelegate" should {
    "Return default options if config is empty" in {
      running(FakeApplication(additionalConfiguration = noDefaultMap)) {
        //default db host
        DBDelegate.mongoClient.address.getHost() must equalTo(DBDelegate.DEFAULT_DB_HOST)
        //default db name
        DBDelegate.db.getName must equalTo(DBDelegate.DEFAULT_DB_NAME)
        //default image db
        DBDelegate.picDB.getName must equalTo(DBDelegate.DEFAULT_IMAGE_DB)
        //default connectionsPerHost
        val connectionsPerHost = DBDelegate.mongoClient.underlying
          .getMongoClientOptions.getConnectionsPerHost()
        connectionsPerHost must beEqualTo(Integer.parseInt(
          DBDelegate.DEFAULT_OPS_MAP("connectionsPerHost")))
        //default connectTimeout
        val connectTimeout = DBDelegate.mongoClient.underlying
          .getMongoClientOptions.getConnectTimeout()
        connectTimeout must beEqualTo(Integer.parseInt(
          DBDelegate.DEFAULT_OPS_MAP("connectTimeout")))
        //default threadsAllowedToBlockForConnectionMultiplier
        val threadsAllowedToBlockForConnectionMultiplier = DBDelegate.mongoClient.underlying
          .getMongoClientOptions.getThreadsAllowedToBlockForConnectionMultiplier()
        threadsAllowedToBlockForConnectionMultiplier must beEqualTo(Integer.parseInt(
          DBDelegate.DEFAULT_OPS_MAP("threadsAllowedToBlockForConnectionMultiplier")))
      }
    }

    "Override all defaults when all props are present" in {
      running(FakeApplication(additionalConfiguration = allPropsMap)) {
        // scala2.10 cannot invoke private constructor so we implement the function
        // in java.
        //val delegate = ReflectionOperations.newInstance[ShowTime.type]
        val delegate = ROperations.invokePrivateConstructor("com.meifannet.framework.db.DBDelegate$", DBDelegate)
        //db host
        delegate.mongoClient.address.getHost() must equalTo("localhost")
        //db name
        delegate.db.getName must equalTo("dummy_db")
        //image db
        delegate.picDB.getName must equalTo("dummy_image")
        //connectionsPerHost
        val connectionsPerHost = delegate.mongoClient.underlying
          .getMongoClientOptions.getConnectionsPerHost()
        connectionsPerHost must beEqualTo(333)
        //connectTimeout
        val connectTimeout = delegate.mongoClient.underlying
          .getMongoClientOptions.getConnectTimeout()
        connectTimeout must beEqualTo(555)
        //threadsAllowedToBlockForConnectionMultiplier
        val threadsAllowedToBlockForConnectionMultiplier = delegate.mongoClient.underlying
          .getMongoClientOptions.getThreadsAllowedToBlockForConnectionMultiplier()
        threadsAllowedToBlockForConnectionMultiplier must beEqualTo(777)
      }
    }
  }
}
