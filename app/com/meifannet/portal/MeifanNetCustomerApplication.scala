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
import jp.t2v.lab.play2.auth._
import jp.t2v.lab.play2.stackc.RequestWithAttributes
import com.meifannet.framework.MeifanNetApplication
import com.meifannet.framework.auth._
import com.meifannet.portal.auth.UserAuthConfigImpl


/**
 * Super controller class for customer applications.
 *
 * These features are implemented:
 * <ul>
 * <li>authorization</li>
 * <li>authentication</li>
 * </ul>
 */
class MeifanNetCustomerApplication extends MeifanNetApplication 
  with LoginLogout with AuthElement with UserAuthConfigImpl {  
}

/**
 * Super controller class for customer applications(optional login).
 *
 * These features are implemented:
 * <ul>
 * <li>authorization</li>
 * <li>authentication</li>
 * </ul>
 */
class MeifanNetCustomerOptionalApplication extends MeifanNetApplication 
  with LoginLogout with OptionalAuthElement with UserAuthConfigImpl {  
}
