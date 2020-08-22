// src/main/scala/progscala3/basicoop/Abstract.scala
package progscala3.basicoop

enum LoggingLevel:                                                   // <1>
  case INFO, WARN, ERROR

trait Logging:
  import LoggingLevel._
  final def info(message: String): Unit = log(INFO, message)
  final def warn(message: String): Unit = log(WARN, message)
  final def error(message: String): Unit = log(ERROR, message)
  final def log(level: LoggingLevel, message: String): Unit =
    write(s"$level: $message")

  protected val write: String => Unit                                // <2>

trait ConsoleLogging extends Logging:
  protected val write = println                                      // <3>

open abstract class Service(val name: String) extends Logging:       // <4>
  import Service._
  final def handle(request: Request): Response =
    info(s"($name) Starting handle for request: $request")
    val result = process(request)
    info(s"($name) Finishing handle with result $result")
    result

  protected def process(request: Request): Response

object Service:                                                      // <5>
  type Request = Map[String,Any]
  type Response = Either[String,Map[String,Any]]

case class HelloService(override val name: String)                   // <6>
    extends Service(name) with ConsoleLogging:
  import Service._

  protected def process(request: Request): Response =
    request.get("user") match
      case Some(user) => Right(Map("message" -> s"Hello, $user"))
      case None => Left("No user field found!")

@main def HelloServiceMain(name: String, users: String*): Unit =     // <8>
  val hs = HelloService("hello")
  for
    user <- users
    request = Map("user" -> user)
  do hs.handle(request) match
    case Left(error) => println(s"ERROR! $error")
    case Right(map) => println(map("message"))

  println("Try an empty map:")
  println(hs.handle(Map.empty))
