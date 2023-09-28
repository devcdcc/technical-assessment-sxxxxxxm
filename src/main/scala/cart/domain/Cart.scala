package com.siriusxm
package cart.domain

opaque type Price = Double

object Price:
  def apply(value: Double): Price = "%.2f".format(value).toDouble
end Price
case class CartItem(title: String, price: Price, quantity: Int)
case class Cart(sessionId: String, cartItems: List[CartItem])
case class CartSummary(sessionId: String, cartItems: List[CartItem], subTotal: Price, tax: Price, total: Price)
