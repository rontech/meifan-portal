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
package utils

import models._

object Tools{
  def getRestDays(restWay:String, restDay1:List[String], restDay2:List[String]) = {
    if(restWay.equals("Fixed")){
      RestDay(restWay, restDay1)
    }else{
      RestDay(restWay, restDay2)
    }
  }

  def setRestDays(restDays : RestDay) = {
    if(restDays.restWay.equals("Fixed")){
      (restDays.restWay, restDays.restDay, Nil)
    }else{
      (restDays.restWay, Nil, restDays.restDay)
    }
  }

  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)
}
