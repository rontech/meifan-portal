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
import models.portal.salon.RestDay

object Tools {
  /**
   * 取得店铺休息规则（用于由表单到对象的映射）
   *
   * @param restWay   休息类别：“固定休息”或“不定息”
   * @param restDay1    休息日
   * @param restDay2    休息规则
   * @return  固定休息时，返回“固定休息”及休息日；反之，返回“不定休”及休息规则
   */
  def getRestDays(restWay: String, restDay1: List[String], restDay2: List[String]) = {
    if (restWay.equals("Fixed")) {
      RestDay(restWay, restDay1)
    } else {
      RestDay(restWay, restDay2)
    }
  }

  /**
   * 反向设置店铺休息规则（用于从对象到表单的映射）
   *
   * @param restDays    对象用休息规则
   * @return    表单用休息规则
   */
  def setRestDays(restDays: RestDay) = {
    if (restDays.restWay.equals("Fixed")) {
      (restDays.restWay, restDays.restDay, Nil)
    } else {
      (restDays.restWay, Nil, restDays.restDay)
    }
  }

  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)
}
