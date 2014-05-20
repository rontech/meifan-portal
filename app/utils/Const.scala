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
/**
 * 表单输入项目唯一性check类型区分
 *
 * Created by YS-HAN on 14/04/23.
 */
object Const {
  //userId or salonId
  val ITEM_TYPE_ID = "loginId"
  //nickName or salonName
  val ITEM_TYPE_NAME = "name"
  //salonNameAbbr
  val ITEM_TYPE_NAME_ABBR = "nameAbbr"
  //email for User
  val ITEM_TYPE_EMAIL = "email"
  //tel for User
  val ITEM_TYPE_TEL = "tel"
  //styleName
  val ITEM_TYPE_STYLE = "style"
  //couponName
  val ITEM_TYPE_COUPON = "coupon"
  //serviceName
  val ITEM_TYPE_SERVICE = "service"
  //menuName
  val ITEM_TYPE_MENU = "menu"
}
