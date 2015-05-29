package hyperloglog

import hyperloglog.HyperLogLog.{computeRegisterIndex, computeFirstOneRank}
import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}

class HyperLogLog_1_AddingSpec extends FunSpec with Matchers with BeforeAndAfterEach {
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
      0 until 256 foreach { i =>
        computeRegisterIndex(i, 256) shouldBe i
      }
      256 until 512 foreach { i =>
        computeRegisterIndex(i, 256) shouldBe i - 256
      }
      512 until 768 foreach { i =>
        computeRegisterIndex(i, 256) shouldBe i - 512
      }
      // Well, you get the cycle :)
    }

    it("should compute the rank of the first 1 bit") {
      computeFirstOneRank(0) shouldBe 65
      computeFirstOneRank(1) shouldBe 64
      computeFirstOneRank(2) shouldBe 63
      // and so on...
      0 until 64 foreach { n =>
        computeFirstOneRank(1L << n) shouldBe 64 - n
      }
    }

    it("should store the rank to the first bucket") {
      log.addHash(0)

      0 until log.n foreach {
        case n@0 => log.registers(n) should be > 0
        case n => log.registers(n) shouldBe 0
      }
    }

    it("should store the rank to the second bucket") {
      log.addHash(1)

      0 until log.n foreach {
        case n@1 => log.registers(n) should be > 0
        case n => log.registers(n) shouldBe 0
      }
    }

    it("should keep the maximum rank when inserting multiple items in a bucket") {
      log.addHash(256)
      log.registers(0) shouldBe 56

      log.addHash(512)
      log.registers(0) shouldBe 56

      log.addHash(0)
      log.registers(0) shouldBe 65
    }
  }
}
