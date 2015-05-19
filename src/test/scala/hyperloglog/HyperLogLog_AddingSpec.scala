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
      log.computeNumberOfLeadingZeros(0) shouldBe 32
      log.computeNumberOfLeadingZeros(1) shouldBe 31
      log.computeNumberOfLeadingZeros(2) shouldBe 30
      // and so on...
      0 until 32 foreach { n =>
        log.computeNumberOfLeadingZeros(1 << n) shouldBe 31 - n
      }
    }

    it("should store the leading zeros count to the first bucket") {
      log.add(0)

      0 until log.bucketCount foreach {
        case n@0 => log.buckets(n) should be > 0
        case n => log.buckets(n) shouldBe 0
      }
    }

    it("should store the leading zeros count to the second bucket") {
      log.add(1)

      0 until log.bucketCount foreach {
        case n@1 => log.buckets(n) should be > 0
        case n => log.buckets(n) shouldBe 0
      }
    }

    it("should keep the maximum leading zeros when inserting multiple items in a bucket") {
      log.add(256)
      log.buckets(0) shouldBe 31

      log.add(512)
      log.buckets(0) shouldBe 31

      log.add(0)
      log.buckets(0) shouldBe 32
    }
  }
}
