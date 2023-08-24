package example

import zio.http._
import zio.stream.ZStream
import zio._

object StreamingResponse extends ZIOAppDefault {
  def run: Task[Nothing]        = Server.serve(app).provide(Server.default)
  private def app: HttpApp[Any] = Routes(
    Method.GET / "stream" ->
      handler(
        Response(
          status = Status.Ok,
          body = Body.fromStream {
            ZStream
              .iterate(1)(_ + 1)
              .tap { i =>
                ZIO.when(i % 10000 == 0)(ZIO.debug("sent 10k numbers"))
              }
              .map(_.toString)
          },
        ),
      ),
  ).toHttpApp
}
