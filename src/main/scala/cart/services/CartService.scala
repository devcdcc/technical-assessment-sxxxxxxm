package com.siriusxm
package cart.services

import cart.adapters.ProductAdapter
import cart.domain.*
import cart.errors.*
import cart.repositories.CartRepository
import zio.*

trait CartService:
  def getCart(sessionId: String): UIO[Cart]
  def addProduct(sessionId: String, quantity: Int, productId: String): IO[CartError, Unit]
  def getCartSummary(sessionId: String): IO[CartError, CartSummary]
end CartService

private case class CartServiceLive(productAdapter: ProductAdapter, repository: CartRepository) extends CartService:
  override def getCart(sessionId: String): UIO[Cart] = repository.getOrCreateCart(sessionId)

  override def addProduct(sessionId: String, quantity: Int, productId: String): IO[CartError, Unit] = for {
    productInfo <- productAdapter.getProductById(productId)
    _ <- repository.addOrIncrementProductQuantity(sessionId, productInfo.title, Price(productInfo.price), quantity)
  } yield ()

  override def getCartSummary(sessionId: String): IO[CartError, CartSummary] = for {
    cart <- getCart(sessionId)
    _    <- ZIO.fail(CartError.EmptyCartError).when(cart.cartItems.isEmpty)
    subTotal = cart.cartItems.map(item => item.price.toDouble * item.quantity).sum
    tax      = subTotal * CartService.TAX
    total    = subTotal + tax
  } yield CartSummary(cart.sessionId, cart.cartItems, Price(subTotal), Price(tax), Price(total))
end CartServiceLive

object CartService:

  private[services] val TAX = 12.5 / 100

  def getCart(sessionId: String): RIO[CartService, Cart] = ZIO.serviceWithZIO[CartService](_.getCart(sessionId))

  def addProduct(sessionId: String, quantity: Int, productId: String): ZIO[CartService, CartError, Unit] =
    ZIO.serviceWithZIO[CartService](_.addProduct(sessionId, quantity, productId))

  def getCartSummary(sessionId: String): ZIO[CartService, CartError, CartSummary] =
    ZIO.serviceWithZIO[CartService](_.getCartSummary(sessionId))

  val live: ZLayer[ProductAdapter & CartRepository, Nothing, CartService] =
    ZLayer.fromFunction(CartServiceLive(_, _))
end CartService
