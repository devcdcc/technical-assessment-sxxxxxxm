package com.siriusxm
package cart.repositories

import cart.domain.*

import zio._

trait CartRepository:
  def getOrCreateCart(sessionId: String): UIO[Cart]
  def addOrIncrementProductQuantity(sessionId: String, title: String, price: Price, quantity: Int): UIO[Unit]
end CartRepository

private case class CartRepositoryLive(ref: Ref[Map[String, Cart]]) extends CartRepository:
  override def getOrCreateCart(sessionId: String): UIO[Cart] =
    ref.modify { data =>
      val newCart = data.getOrElse(sessionId, Cart(sessionId = sessionId, cartItems = List.empty))
      (newCart, data ++ Map((sessionId, newCart)))
    }

  private def addItemReducer(cartItems: List[CartItem], title: String, price: Price, quantity: Int) =
    val newCartItem = CartItem(title, price, quantity)
    cartItems.find(_.title == title) match
      case Some(value) =>
        cartItems.map { oldItem =>
          if (oldItem.title == newCartItem.title) // weird condition in oder to preserve the same order
            newCartItem.copy(quantity = quantity + oldItem.quantity)
          else
            oldItem
        }
      case None        => cartItems :+ newCartItem

  override def addOrIncrementProductQuantity(sessionId: String, title: String, price: Price, quantity: Int): UIO[Unit] =
    ref.modify { data =>

      val oldCart = data.getOrElse(sessionId, Cart(sessionId = sessionId, cartItems = List.empty))
      val newCart = oldCart.copy(cartItems = addItemReducer(oldCart.cartItems, title, price, quantity))
      (newCart, data ++ Map((sessionId, newCart)))
    }

end CartRepositoryLive

object CartRepository:

  def getOrCreateCart(sessionId: String): RIO[CartRepository, Cart] =
    ZIO.serviceWithZIO(_.getOrCreateCart(sessionId = sessionId))

  def addOrIncrementProductQuantity(
    sessionId: String,
    title: String,
    price: Price,
    quantity: Int
  ): RIO[CartRepository, Unit] =
    ZIO.serviceWithZIO(_.addOrIncrementProductQuantity(sessionId, title, price, quantity))

  val live: ZLayer[Any, Nothing, CartRepository] =
    ZLayer.fromZIO(Ref.make(Map.empty).map(data => CartRepositoryLive(data)))

end CartRepository
