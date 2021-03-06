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
package com.meifannet.portal

import play.api.mvc.{Controller, Result}
import scala.concurrent.Future
import play.api.mvc.Controller
import jp.t2v.lab.play2.auth.AuthConfig
import jp.t2v.lab.play2.auth.LoginLogout
import jp.t2v.lab.play2.auth.OptionalAuthElement
import jp.t2v.lab.play2.auth.AuthElement

import com.meifannet.framework.MeifanNetApplication
import com.meifannet.portal.auth.SalonAuthConfigImpl

/**
 * Super controller class for salon applications.
 *
 * These features are implemented:
 * <ul>
 * <li>authorization</li>
 * <li>authentication</li>
 * </ul>
 */
class MeifanNetSalonApplication extends MeifanNetApplication
  with LoginLogout with AuthElement with SalonAuthConfigImpl {
}
