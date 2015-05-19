package hyperloglog

import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}

class HyperLogLog_AddingSpec extends FunSpec with Matchers with BeforeAndAfterEach {
  var log: HyperLogLog= _

  override def beforeEach(): Unit = {
    log = new HyperLogLog(8)
  }

  describe("When adding an element to an HyperLogLog") {

    it("should add something") {
      log.add("something")

      log.count shouldBe 1
    }

    it("should compute an absolute value for the hash") {
      log.absoluteValue(-4) shouldBe 4
      log.absoluteValue(4) shouldBe 4
    }

    it("should compute the bucket index") {
      0 to 23 foreach { n =>
        log.computeBucketIndex(n) shouldBe 0
      }

      log.computeBucketIndex(1 << 24) shouldBe 1
      log.computeBucketIndex(1 << 25) shouldBe 2
      log.computeBucketIndex(1 << 26) shouldBe 4
      log.computeBucketIndex(1 << 27) shouldBe 8

      log.computeBucketIndex((1 << 24) + (1 << 25)) shouldBe 3
      log.computeBucketIndex((1 << 24) + (1 << 25) + (1 << 26)) shouldBe 7
    }

    it("should compute the hash within a bucket") {
      // It also should work above :)
      0 to 1337 foreach { n =>
        // 255 == (1 << 8) -1
        log.computeBucketHash(n) & 255 shouldBe 255
        log.computeBucketHash(n) >> 8 shouldBe n
      }
    }

    it("should compute the number of leading zeros") {
      log.computeNumberOfLeadingZeros(0) shouldBe 32
      log.computeNumberOfLeadingZeros(1) shouldBe 31
      log.computeNumberOfLeadingZeros(2) shouldBe 30
      // and so on...
      0 to 31 foreach { n =>
        log.computeNumberOfLeadingZeros(1 << n) shouldBe 31 - n
      }
    }
  }
}
