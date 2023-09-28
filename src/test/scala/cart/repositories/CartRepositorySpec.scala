package com.siriusxm
package cart.repositories

import cart.domain._
import zio._
import zio.test._

object CartRepositorySpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("CartRepository")(
    suite("createOrReturnCart")(
      test("should return empty cart for non-existing session") {
        // given
        val sessionId = "createOrReturnCart-new"
        val expected  = Cart(sessionId, List.empty)
        for {
          // when
          result <- CartRepository.getOrCreateCart(sessionId = sessionId)
          // then
        } yield assertTrue(result == expected)
      },
      test("should return existing data") {
        // given
        val sessionId = "createOrReturnCart-existing"
        val expected  = Cart(
          sessionId = sessionId,
          cartItems = List(CartItem("Corn Flakes", Price(2.52), 2), CartItem("Cheerios", Price(8.43), 1))
        )
        for {

          _      <- CartRepository.addOrIncrementProductQuantity(sessionId, "Corn Flakes", Price(2.52), 2)
          _      <- CartRepository.addOrIncrementProductQuantity(sessionId, "Cheerios", Price(8.43), 1)
          // when
          result <- CartRepository.getOrCreateCart(sessionId = sessionId)
          // then
        } yield assertTrue(result == expected)
      }
    ),
    suite("addOrIncrementProductQuantity")(
      test("should create and modify data") {
        // given
        val sessionId     = "addOrIncrementProductQuantity-existing"
        val firstExpected = Cart(
          sessionId = sessionId,
          cartItems = List(CartItem("Corn Flakes", Price(2.52), 2))
        )
        val lastExpected  = Cart(
          sessionId = sessionId,
          cartItems = List(CartItem("Corn Flakes", Price(2.52), 5))
        )
        for {
          // when
          _         <- CartRepository.addOrIncrementProductQuantity(sessionId, "Corn Flakes", Price(2.52), 2)
          firstSave <- CartRepository.getOrCreateCart(sessionId)
          _         <- CartRepository.addOrIncrementProductQuantity(sessionId, "Corn Flakes", Price(2.52), 3)

          lastSave <- CartRepository.getOrCreateCart(sessionId)
          // then
        } yield assertTrue(firstSave == firstExpected && lastSave == lastExpected)
      }
    )
  ).provide(CartRepository.live)
}
