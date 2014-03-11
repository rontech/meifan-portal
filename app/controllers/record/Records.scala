package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Record
import org.bson.types.ObjectId
import views._
import se.radley.plugin.salat.Binders._
import models._
import com.mongodb.casbah.commons.MongoDBObject
import java.util.Date
import java.text.SimpleDateFormat


object Records extends Controller{
  val pageSize:Int = 5	 //每页显示记录
  /**
   * 定义一个履历表单
   */
  val recordForm:Form[Record] = Form(
	    mapping(
	        "store" -> text,
	        "serviceStatus" -> number,
	        "serviceStart" ->date("yyyy-MM-dd") ,
	        "serviceEnd" -> date("yyyy-MM-dd"),
	        "serviceDesigner" -> text,
	        "serviceItem" -> text,
	        "userName" -> text,
	        "userPhone" -> text,
	        "userLeaveMessage" -> text,
	        "costTotal" -> number
	        ){
	      (store,serviceStatus,serviceStart,serviceEnd,serviceDesigner,serviceItem,userName,userPhone,userLeaveMessage,costTotal) =>
	          Record(new ObjectId,store,serviceStatus,serviceStart,serviceEnd,serviceDesigner,serviceItem,userName,userPhone,userLeaveMessage,costTotal)
	    }
	    {
	      record=> Some((record.store,record.serviceStatus,record.serviceStart,record.serviceEnd,record.serviceDesigner,record.serviceItem,record.userName,record.userPhone,record.userLeaveMessage,record.costTotal))
	    }
	)
  
	/**
	 * 根据传过来的页数查找
	 */
	def findReserv(page:Int) = Action{implicit request =>
	  val re = "spahome"
	  val records = Record.findAllrecord(re,page,pageSize)
	  val count = Record.counts(re)
	  var pages:Int = 0
	  if(count % pageSize == 0){
	    pages = count.toInt/ pageSize
	  }else{
		pages = count.toInt/pageSize+1
	  }
	  Ok(views.html.storeReservation.findReserv(records,count,pages,page))
	}
  
  	/**
  	 * 根据组合条件搜索查找
  	 */
  	def findRecordByCondition = Action {implicit request =>
  	  val re = "spahome"
  	  val designer = request.getQueryString("serviceDesigner")
  	  val serviceStart = request.getQueryString("serviceStart")
  	  val serviceStatus = request.getQueryString("serviceStatus")
  	  /*designer match{
  	    case Some(e)=>e
  	    case _=>None
  	  }*/
  	  val builder = MongoDBObject.newBuilder
  	  builder +="store" -> re
  	  println("designer..."+designer.get)
  	  println("serviceStart!=None "+(!(serviceStart.get).equals("")))
  	  if(!(designer.get).equals("")){
  	    builder +="serviceDesigner" -> designer.get
  	  }
  	  if(!(serviceStart.get).equals("")){
  	    builder +="serviceStart" -> serviceStart.get
  	  }
  	  if(serviceStatus!=None){
  	    builder +="serviceStatus" -> serviceStatus.get.toInt
  	  }
  	  val query=builder.result
  	  val records = Record.findByQuery(query, 1, pageSize)
  	  val count = Record.countByCondition(query)
	  var pages:Int = 0
	  if(count % pageSize == 0){
	    pages = count.toInt/ pageSize
	  }else{
		pages = count.toInt/pageSize+1
	  }
  	  
  	   Ok(views.html.storeReservation.findReserv(records,count,pages,1))
  	}
  	  	  
  	 /**
  	  *       店铺查看技师日程
  	  */ 	    
  	 def checkStylistReserv = Action {
	   
	  var date:Date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2005-06-09 06:30")
	  var list:List[java.util.Date]=Nil
	  val from = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
	  for(i<-1 to 25){
	    date.setMinutes(date.getMinutes() - 30)
	    println("date......."+from.format(date))
	    list:::=List(new Date(date.toString())) //error
	  }
	  Ok(views.html.storeReservation.checkStylistReserv(list.reverse))
	} 		
	      
	    
  	
  	/**
  	 * 对某一条记录做详细查看
  	 */
	def checkReservInfo(id:ObjectId) = Action{
	  Record.findOneById(id).map { record =>
	  	Ok(views.html.storeReservation.checkReservInfo(record))
	  }getOrElse {
		  NotFound
    }
	  
	}
	
	/**
	 * 店铺查看履历跳至技师页面
	 */
	def selectStylistReserv = Action {
	  Ok(views.html.storeReservation.selectStylistReserv(""))
	}
	
	/**
	 *
	 *变更预约状态
	 * -1：已取消
	 * 0：已预约
	 * 1：已消费
	 * 2：已过期
	 *   
	 */
	
	
	/**
	 *取消预约将状态改成-1
	 */
	def cancelRecord(id: ObjectId) = Action {
		Record.findOneById(id).map { record =>
		val records = new Record(new ObjectId,record.store,-1,record.serviceStart,record.serviceEnd,
		record.serviceDesigner,record.serviceItem,record.userName,record.userPhone,record.userLeaveMessage,record.costTotal)
		Record.save(records.copy(id = record.id))
		
		}
		Redirect(routes.Records.findReserv(1))
	}
	//
	/*def inRecord(id: ObjectId) = Action {
		Record.findOneById(id).map { record =>
		val records = new Record(new ObjectId,record.store,0,record.serviceStart,record.serviceEnd,
		record.serviceDesigner,record.serviceItem,record.userName,record.userPhone,record.userLeaveMessage,record.costTotal)
		Record.save(records.copy(id = record.id))
		
		}
		Redirect(routes.Records.findReserv)
	}*/
	/**
	 * 将预约状态改成已完成消费1
	 */
	def finishRecord(id: ObjectId) = Action {
		Record.findOneById(id).map { record =>
		val records = new Record(new ObjectId,record.store,1,record.serviceStart,record.serviceEnd,
		record.serviceDesigner,record.serviceItem,record.userName,record.userPhone,record.userLeaveMessage,record.costTotal)
		Record.save(records.copy(id = record.id))
		
		}
		Redirect(routes.Records.findReserv(1))
	}
	/**
	 * 将预约状态改成已过期2
	 */
	def overdateRecord(id: ObjectId) = Action {
		Record.findOneById(id).map { record =>
		val records = new Record(new ObjectId,record.store,2,record.serviceStart,record.serviceEnd,
		record.serviceDesigner,record.serviceItem,record.userName,record.userPhone,record.userLeaveMessage,record.costTotal)
		Record.save(records.copy(id = record.id))
		
		}
		Redirect(routes.Records.findReserv(1))
	}
	
	
}
