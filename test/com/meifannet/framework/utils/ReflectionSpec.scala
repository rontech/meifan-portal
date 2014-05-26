import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

import com.meifannet.framework.utils.ReflectionOperations._


object ReflectionSpec extends Specification {

  /**
   * Test Data.
   *   To insulate the affection of the  modification of the classes defined in model,
   *   we define the test case class separately here.
   */
  /** Embered test class. */ 
  case class Address(aid: Int, addr: String, postnum: String)

  case class Person(
    pid: String,
    pname: String,
    age: Int,
    phone: Option[String], 
    interests: List[String],
    addr: Address
  )
 
  /** our test instances. */
  val adr = Address(1001, "Suzhou Zhuyuan Road 209#", "215011")
  val ps = Person("012560", "jie-zhang", 31, Some("18662186718"), List("Programming", "Music", "Ridding"), adr) 
  val ps2 = Person("99198120", "Toruku Makto", 31, None, List(), adr) 


  /**
   * Test Case.
   */
  "Reflection Method [getFieldValueOfClass]" should {
    "get a 'plain' String type field value" in {
      val pn = getFieldValueOfClass[Person](ps, "pname")
      pn mustEqual "jie-zhang"
    }

    "get a 'plain' Int type field value" in {
      val age = getFieldValueOfClass[Person](ps, "age")
      age mustEqual 31 
    }

    "get a non empty List type field value" in {
      val lst = getFieldValueOfClass[Person](ps, "interests")
      lst must beAnInstanceOf[List[String]]
      lst mustEqual List("Programming", "Music", "Ridding")
    }

   "get an empty List type field value" in {
      val lst2 = getFieldValueOfClass[Person](ps2, "interests")
      lst2 must beAnInstanceOf[List[String]]
      lst2 mustEqual List()
    }

    "get an Option type [Some] field value" in {
      val opt = getFieldValueOfClass[Person](ps, "phone")
      opt must beAnInstanceOf[Option[String]]
      opt mustEqual Some("18662186718")
    }

   "get an Option type [None] field value" in {
      val opt2 = getFieldValueOfClass[Person](ps2, "phone")
      opt2 must beAnInstanceOf[Option[String]]
      opt2 mustEqual None
    }

    "get an Embed type field value" in {
      val adr = getFieldValueOfClass[Person](ps, "addr")
      adr must beAnInstanceOf[Address]
      adr mustEqual Address(1001, "Suzhou Zhuyuan Road 209#", "215011")
      adr.asInstanceOf[Address].postnum mustEqual "215011"
    }

  }

}

