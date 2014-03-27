package models

sealed trait Permission
object Administrator extends Permission
object LoggedIn extends Permission
object GuestUser extends Permission

object Permission {

  def valueOf(value: Permission): String = value match {
    case Administrator => "Administrator"
    case LoggedIn      => "LoggedIn"
    case GuestUser     => "GuestUser"
    case  _ => throw new IllegalArgumentException()
  }
}