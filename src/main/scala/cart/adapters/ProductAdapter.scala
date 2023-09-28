package com.siriusxm
package cart.adapters

import cart.errors.CartError
import cart.domain.*

import zio.*
import zio.http._
import zio.json.*

trait ProductAdapter:
  def getProductById(productId: String): IO[CartError, CartProduct]
end ProductAdapter

private case class ProductAdapterLive(client: Client) extends ProductAdapter:
  private val URL_PREFIX = "https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main"
  override def getProductById(productId: String): IO[CartError, CartProduct] = {
    val urlString = s"$URL_PREFIX/$productId.json"
    for {
      url      <- ZIO.fromEither(URL.decode(urlString)).mapError(_ => CartError.UnknownError)
      response <- client
        .request(Request.default(Method.GET, url = url))
        .tapErrorCause(e => Console.printLine(e))
        .mapError(_ => CartError.UnknownError)

      data        <-
        response.status match
          case Status.NotFound             => ZIO.fail(CartError.ElementDoesNotExistsError)
          case status if status.code < 400 =>
            response.body.asString
              .tapErrorCause(e => Console.printLine(e))
              .mapError(_ => CartError.UnknownError)
          case status                      => ZIO.fail(CartError.UnknownError)
      cartProduct <- ZIO.fromEither(data.fromJson[CartProduct]).mapError(_ => CartError.CartDecodingError)
    } yield cartProduct
  }
end ProductAdapterLive

object ProductAdapter:
  def getProductById(productId: String): ZIO[ProductAdapter, CartError, CartProduct] =
    ZIO.serviceWithZIO(_.getProductById(productId))

  val live: ZLayer[Client, Nothing, ProductAdapter] = ZLayer.fromFunction(ProductAdapterLive(_))

end ProductAdapter
