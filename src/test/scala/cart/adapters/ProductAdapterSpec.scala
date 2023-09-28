package com.siriusxm
package cart.adapters

import cart.domain.CartProduct
import cart.errors.CartError
import zio.*
import zio.test.*

object ProductAdapterSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("ProductAdapter")(
    suite("getProductById")(
      test("should return error when product is not found") {
        // given
        val wrongProductId = "cornflakes-error"
        val expected       = Left(CartError.ElementDoesNotExistsError)
        for {
          // when
          result <- ProductAdapter.getProductById(wrongProductId).either
          // then
        } yield assertTrue(result == expected)
      },
      test("should return CartProduct for valid productId") {
        // given
        val wrongProductId = "cornflakes"
        val expected       = CartProduct("Corn Flakes", 2.52)
        for {
          // when
          result <- ProductAdapter.getProductById(wrongProductId)
          // then
        } yield assertTrue(result == expected)
      }
    )
  ).provide(ProductAdapter.live, zio.http.Client.default)
}
