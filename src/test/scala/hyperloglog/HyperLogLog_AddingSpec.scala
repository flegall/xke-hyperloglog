package hyperloglog

import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}

class HyperLogLog_AddingSpec extends FunSpec with Matchers with BeforeAndAfterEach {
  var log: HyperLogLog= _

  override def beforeEach(): Unit = {
    log = new HyperLogLog(8)
  }

  describe("When adding an element to an HyperLogLog") {

    it("should add something") {
      log.addItem("something")

      log.count shouldBe 1
    }

    it("should compute the bucket index") {
      0 until 256 foreach { n =>
        log.computeBucketIndex(n) shouldBe n
      }
      256 until 512 foreach { n =>
        log.computeBucketIndex(n) shouldBe n - 256
      }
      512 until 768 foreach { n =>
        log.computeBucketIndex(n) shouldBe n - 512
      }
      // Well, you get the cycle :)
    }

    it("should compute the hash within a bucket") {
      0 until 256 foreach { n =>
        log.computeBucketHash(n) shouldBe 0
      }
      256 until 512 foreach { n =>
        log.computeBucketHash(n) shouldBe 1
      }
      512 until 768 foreach { n =>
        log.computeBucketHash(n) shouldBe 2
      }
      768 until 1024 foreach { n =>
        log.computeBucketHash(n) shouldBe 3
      }
    }

    it("should compute the number of leading zeros") {
      log.computeNumberOfLeadingZeros(0) shouldBe 64
      log.computeNumberOfLeadingZeros(1) shouldBe 63
      log.computeNumberOfLeadingZeros(2) shouldBe 62
      // and so on...
      0 until 64 foreach { n =>
        log.computeNumberOfLeadingZeros(1L << n) shouldBe 63 - n
      }
    }

    it("should store the leading zeros count to the first bucket") {
      log.addHash(0)

      0 until log.bucketCount foreach {
        case n@0 => log.buckets(n) should be > 0
        case n => log.buckets(n) shouldBe 0
      }
    }

    it("should store the leading zeros count to the second bucket") {
      log.addHash(1)

      0 until log.bucketCount foreach {
        case n@1 => log.buckets(n) should be > 0
        case n => log.buckets(n) shouldBe 0
      }
    }

    it("should keep the maximum leading zeros when inserting multiple items in a bucket") {
      log.addHash(256)
      log.buckets(0) shouldBe 63

      log.addHash(512)
      log.buckets(0) shouldBe 63

      log.addHash(0)
      log.buckets(0) shouldBe 64
    }
  }
}
