package com.siriusxm
package cart.repositories

import cart.domain.*

import zio._

trait CartRepository:
  def getOrCreateCart(sessionId: String): UIO[Cart]
  def addOrIncrementProductQuantity(sessionId: String, title: String, price: Price, quantity: Int): UIO[Unit]
end CartRepository

private case class CartRepositoryLive() extends CartRepository:

  override def getOrCreateCart(sessionId: String): UIO[Cart] = ???
  override def addOrIncrementProductQuantity(sessionId: String, title: String, price: Price, quantity: Int): UIO[Unit] =
    ???

end CartRepositoryLive

object CartRepository:

  def getOrCreateCart(sessionId: String): UIO[Cart]                                                           = ???
  def addOrIncrementProductQuantity(sessionId: String, title: String, price: Price, quantity: Int): UIO[Unit] = ???

  val live: ZLayer[Any, Nothing, CartRepository] = ZLayer.fromFunction(() => CartRepositoryLive())

end CartRepository
