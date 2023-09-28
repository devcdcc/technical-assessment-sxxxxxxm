package com.siriusxm
package cart.services

import com.siriusxm.cart.domain.*
import com.siriusxm.cart.errors.*

import zio.*

trait CartService:
  def getCart(sessionId: String): UIO[Cart]
  def addProduct(quantity: Int, sessionId: String): IO[CartError, Unit]
  def getCartSummary(sessionId: String): IO[CartError, CartSummary]
end CartService

object CartService:

  def getCart(sessionId: String): RIO[CartService, Cart] = ZIO.serviceWithZIO[CartService](_.getCart(sessionId))

  def addProduct(quantity: Int, productId: String): ZIO[CartService, CartError, Unit] =
    ZIO.serviceWithZIO[CartService](_.addProduct(quantity, productId))

  def getCartSummary(sessionId: String): ZIO[CartService, CartError, CartSummary] =
    ZIO.serviceWithZIO[CartService](_.getCartSummary(sessionId))

  private case class CartServiceImpl() extends CartService:
    override def getCart(sessionId: String): UIO[Cart]                             = ???
    override def addProduct(quantity: Int, productId: String): IO[CartError, Unit] = ???
    override def getCartSummary(sessionId: String): IO[CartError, CartSummary]     = ???
  end CartServiceImpl

  val live: ZLayer[Any, Nothing, CartService] = ZLayer.fromFunction(() => CartServiceImpl())
end CartService
