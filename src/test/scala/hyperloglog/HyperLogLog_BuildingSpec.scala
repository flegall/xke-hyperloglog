package hyperloglog

import org.scalatest.{FunSpec, Matchers}

class HyperLogLog_BuildingSpec extends FunSpec with Matchers {

  describe("When building HyperLogLog") {

    it("should build an HyperLogLog of 4 buckets") {
      val log = new HyperLogLog(2)

      log.buckets should have size 4
      log.buckets foreach {_ shouldBe 0}
      log.bucketCount shouldBe 4
      log.biasCorrection shouldBe 0.56 +- 0.01
    }

    it("should build an HyperLogLog of 64 buckets") {
      val log = new HyperLogLog(6)

      log.buckets should have size 64
      log.buckets foreach {_ shouldBe 0}
      log.bucketCount shouldBe 64
      log.biasCorrection shouldBe 0.70 +- 0.01
    }

    it("should build an HyperLogLog of 2048 buckets") {
      val log = new HyperLogLog(11)

      log.buckets should have size 2048
      log.buckets foreach {_ shouldBe 0}
      log.bucketCount shouldBe 2048
      log.biasCorrection shouldBe 0.72 +- 0.01
    }
  }
}
