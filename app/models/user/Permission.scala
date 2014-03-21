package models

sealed trait Permission
case object Administrator extends Permission
case object LoggedIn extends Permission
case object GuestUser extends Permission

object Permission {

  def valueOf(value: String): Permission = value match {
    case "Administrator" => Administrator
    case "Loggedin"      => LoggedIn
    case "GuestUser"	 => GuestUser
    case _ => throw new IllegalArgumentException()
  }

}
