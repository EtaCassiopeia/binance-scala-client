/*
 * Copyright (c) 2020 Paolo Boni
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.paoloboni.http

import cats.Monad
import cats.effect.Concurrent
import cats.implicits._
import log.effect.LogWriter
import upperbound.Limiter

package object ratelimit {
  implicit class LimiterOps[F[_]: Concurrent: LogWriter](limiter: Limiter[F]) {
    private implicit val l: Limiter[F] = limiter

    def await[A](
        job: F[A],
        priority: Int = 0,
        weight: Int = 1
    )(implicit F: Monad[F]): F[A] = {
      List.fill(weight - 1)(LogWriter.debug("Dummy job")).map(Limiter.await(_, priority)).sequence *> Limiter.await(
        job,
        priority
      )
    }
  }
}
