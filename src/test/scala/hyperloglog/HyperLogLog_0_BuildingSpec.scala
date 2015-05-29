package hyperloglog

import org.scalatest.{FunSpec, Matchers}

class HyperLogLog_0_BuildingSpec extends FunSpec with Matchers {

  describe("When building HyperLogLog") {

    it("should build an HyperLogLog of 4 registers") {
      val log = new HyperLogLog(2)

      log.registers should have size 4
      log.registers foreach {_ shouldBe 0}
      log.n shouldBe 4
      log.biasCorrectionForHyperLogLog shouldBe 0.56 +- 0.01
    }

    it("should build an HyperLogLog of 64 registers") {
      val log = new HyperLogLog(6)

      log.registers should have size 64
      log.registers foreach {_ shouldBe 0}
      log.n shouldBe 64
      log.biasCorrectionForHyperLogLog shouldBe 0.70 +- 0.01
    }

    it("should build an HyperLogLog of 2048 registers") {
      val log = new HyperLogLog(11)

      log.registers should have size 2048
      log.registers foreach {_ shouldBe 0}
      log.n shouldBe 2048
      log.biasCorrectionForHyperLogLog shouldBe 0.72 +- 0.01
    }
  }
}
