package com.example

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext
import scala.util.Try

import cats.effect._
import fs2._
import fs2.async.mutable.Signal
import org.scalatest.{FlatSpec, Matchers}

class InterruptHandleBugTest extends FlatSpec with Matchers {

  // see failures even with the single thread executor
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(2))

  private val numTries = 50

  def runStream(useInterrupt: Boolean): Unit = {
    val stopSignal = Signal.constant[IO, Boolean](false)

    val stream = Stream(42)
      .covary[IO]
      .evalMap(_ => IO { sys.error("BOOOOM") })

    val interruptableStream =
      if (useInterrupt) stream.interruptWhen(stopSignal)
      else
        stream

    val res = for {
      exceptionSignal <- Signal[IO, Option[Throwable]](None)
      _ <- interruptableStream
        .handleErrorWith(t => Stream.eval(exceptionSignal.set(Some(t))))
        .compile
        .drain
      exception <- exceptionSignal.get
    } yield exception

    val _ = res.unsafeRunSync() should not be 'empty
  }

  "a stream" should "handle errors" in {
    val successes = (1 to numTries).map(_ => Try(runStream(useInterrupt = false)).isSuccess)
    successes should not contain false
  }

  "a stream" should "handle errors AND be interruptable" in {
    val successes = (1 to numTries).map(_ => Try(runStream(useInterrupt = true)).isSuccess)
    successes should not contain false
  }
}
