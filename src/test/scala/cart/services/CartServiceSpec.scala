package com.siriusxm
package cart.services

import cart.domain.*

import zio.*
import zio.test.*
import TestAspect.*
import com.siriusxm.cart.adapters.ProductAdapter
import com.siriusxm.cart.errors.CartError
import com.siriusxm.cart.repositories.CartRepository

object CartServiceSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("CartService")(
    suite("getCart")(
      test("should get an empty card for a new SessionId") {
        // given
        val sessionId = "getCart-new"
        val expected  = Cart(sessionId = sessionId, cartItems = List.empty)
        for {
          // when
          result <- CartService.getCart(sessionId)
          // then
        } yield assertTrue(result == expected)
      },
      test("should return the existing card assigned to SessionId") {
        // given
        val sessionId = "getCart-exist"
        val expected  = Cart(
          sessionId = sessionId,
          cartItems = List(CartItem("Corn Flakes", Price(2.52), 2), CartItem("Cheerios", Price(8.43), 1))
        )
        for {
          _      <- CartService.addProduct(2, "cornflakes")
          _      <- CartService.addProduct(1, "cheerios")
          // when
          result <- CartService.getCart(sessionId)
          // then
        } yield assertTrue(result == expected)
      }
    ),
    suite("addProduct")(
      test("should create a new item if products not exists in cart") {
        // given
        val sessionId = "addProduct-new"
        val expected  = Cart(
          sessionId = sessionId,
          cartItems = List(CartItem("Corn Flakes", Price(2.52), 2))
        )
        for {
          // when
          _      <- CartService.addProduct(2, "cornflakes")
          // then
          result <- CartService.getCart(sessionId)
        } yield assertTrue(result == expected)
      },
      test("should increment product quantity for existing product") {
        // given
        val sessionId = "addProduct-existing"
        val expected  = Cart(
          sessionId = sessionId,
          cartItems = List(CartItem("Corn Flakes", Price(2.52), 5))
        )
        for {
          _      <- CartService.addProduct(2, "cornflakes")
          // when
          _      <- CartService.addProduct(3, "cornflakes")
          result <- CartService.getCart(sessionId)
          // then
        } yield assertTrue(result == expected)
      },
      test("should fail with ElementDoesNotExists error") {
        // given
        val sessionId = "addProduct-error-404"
        val expected  = Left(CartError.ElementDoesNotExistsError)
        for {
          // when
          result <- CartService.addProduct(2, "cornflakes-404").either
          // then
        } yield assertTrue(result == expected)
      }
    ),
    suite("getSummary")(
      test("should fail with EmptyCartError") {
        // given
        val sessionId = "getSummary-new"
        val expected  = Left(CartError.EmptyCartError)
        for {
          // when
          result <- CartService.getCartSummary(sessionId).either
          // then
        } yield assertTrue(result == expected)
      },
      test("should return Cart Summary") {
        // given
        val sessionId = "getSummary-existing"
        val expected  = CartSummary(
          sessionId,
          List(CartItem("Corn Flakes", Price(2.52), 2), CartItem("Weetabix", Price(9.98), 1)),
          Price(15.02),
          Price(1.88),
          Price(16.90)
        )
        for {
          _      <- CartService.addProduct(2, "cornflakes")
          // when
          _      <- CartService.addProduct(1, "weetabix")
          result <- CartService.getCartSummary(sessionId)
          // then
        } yield assertTrue(result == expected)
      }
    )
  ).provide(CartService.live, ProductAdapter.live, CartRepository.live, http.Client.default)
}
