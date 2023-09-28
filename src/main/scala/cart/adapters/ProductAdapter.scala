package com.siriusxm
package cart.adapters

import cart.errors.CartError
import cart.domain.CartProduct

import zio._
import zio.http.Client

trait ProductAdapter:
  def getProductById(productId: String): IO[CartError, CartProduct]
end ProductAdapter

private case class ProductAdapterLive(client: Client) extends ProductAdapter:
  override def getProductById(productId: String): IO[CartError, CartProduct] = ???
end ProductAdapterLive

object ProductAdapter:
  def getProductById(productId: String): ZIO[ProductAdapter, CartError, CartProduct] =
    ZIO.serviceWithZIO(_.getProductById(productId))

  val live: ZLayer[Client, Nothing, ProductAdapter] = ZLayer.fromFunction(ProductAdapterLive(_))

end ProductAdapter
