package example

import zio._
import zio.http._

import java.util.concurrent.atomic.AtomicInteger

object SimpleClient extends ZIOAppDefault {
  def run: RIO[Scope, Any]               = program.provideSome[Scope](Client.default)
  def program: RIO[Scope & Client, Unit] = for {
    client <- ZIO.service[Client]
    request = client.request(Request.get("http://localhost:8080/stream"))
//    res <- request
    res <- ZIO.scoped(request)
    counter = new AtomicInteger(0)
    _ <- res.body.asStream.runForeach { _ =>
      ZIO.when(counter.incrementAndGet() % 10000 == 0)(ZIO.debug("Received 10k bytes"))
    }
  } yield ()

}
