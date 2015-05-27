package hyperloglog

import java.lang.Math.abs

import hyperloglog.HyperLogLog._
import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}

import scala.util.Random

class HyperLogLog_2_CountSpec extends FunSpec with Matchers with BeforeAndAfterEach {

  describe("linearMean()") {
    it("should compute the linear mean") {
      linearMean(Array(1, 2, 3, 4, 5)) shouldBe 3.0 +- 0.01
    }
  }

  describe("When counting with LogLog") {

    it("of 50K/1M with 256 buckets") {
      val uniqueItemsCount = 50 * 1000

      val log = buildHyperLogLog(8, 1 * 1000 * 1000, uniqueItemsCount)

      expectWithError(log.logLogCount, uniqueItemsCount, 15)
    }

    it("of 100K/2M with 256 buckets") {
      val uniqueItemsCount = 100 * 1000

      val log = buildHyperLogLog(8, 2 * 1000 * 1000, uniqueItemsCount)

      expectWithError(log.logLogCount, uniqueItemsCount, 16)
    }

    it("of 1M/10M with 256 buckets") {
      val uniqueItemsCount = 1000 * 1000
      val log = buildHyperLogLog(8, 10 * 1000 * 1000, uniqueItemsCount)

      expectWithError(log.logLogCount, uniqueItemsCount, 7)
    }

    it("of 1M/10M with 1024 buckets") {
      val uniqueItemsCount = 1000 * 1000
      val log = buildHyperLogLog(10, 10 * 1000 * 1000, uniqueItemsCount)

      expectWithError(log.logLogCount, uniqueItemsCount, 3)
    }

    it("of 1337M/15M with 1024 buckets") {
      val uniqueItemsCount = 1337 * 1000

      val log = buildHyperLogLog(10, 15 * 1000 * 1000, uniqueItemsCount)

      expectWithError(log.logLogCount, uniqueItemsCount, 10)
    }

    it("of 22M/200M with 1024 buckets") {
      val uniqueItemsCount = 22 * 1000 * 1000

      val log = buildHyperLogLog(10, 200 * 1000 * 1000, uniqueItemsCount)

      expectWithError(log.logLogCount, uniqueItemsCount, 21)
    }

    it("of 1337M/15M with 16384 buckets") {
      val uniqueItemsCount = 1337 * 1000

      val log = buildHyperLogLog(14, 15 * 1000 * 1000, uniqueItemsCount)

      expectWithError(log.logLogCount, uniqueItemsCount, 10)
    }
  }

  describe("When counting with HyperLogLog") {
    it("of 1M/10M with 1024 buckets") {
      val uniqueItemsCount = 1000 * 1000

      val log = buildHyperLogLog(10, 10 * 1000 * 1000, uniqueItemsCount)

      expectWithError(log.hyperLogLogCount, uniqueItemsCount, 10)
    }

    it("of 100K/10M with 1024 buckets") {
      val uniqueItemsCount = 100 * 1000

      val log = buildHyperLogLog(10, 10 * 1000 * 1000, uniqueItemsCount)

      expectWithError(log.hyperLogLogCount, uniqueItemsCount, 10)
    }

    it("of 250K/10M with 1024 buckets") {
      val uniqueItemsCount = 250 * 1000

      val log = buildHyperLogLog(10, 10 * 1000 * 1000, uniqueItemsCount)

      expectWithError(log.hyperLogLogCount, uniqueItemsCount, 10)
    }

    it("of 250K/10M with 256 buckets") {
      val uniqueItemsCount = 250 * 1000

      val log = buildHyperLogLog(8, 10 * 1000 * 1000, uniqueItemsCount)

      expectWithError(log.hyperLogLogCount, uniqueItemsCount, 10)
    }

    it("of 1337M/15M with 16384 buckets") {
      val uniqueItemsCount = 1337 * 1000

      val log = buildHyperLogLog(14, 15 * 1000 * 1000, uniqueItemsCount)

      expectWithError(log.hyperLogLogCount, uniqueItemsCount, 1)
    }
  }

  def buildHyperLogLog(numBucketBits: Int, universeSize: Int, cardinality: Int): HyperLogLog = {
    val log = new HyperLogLog(numBucketBits)

    val random = new Random()
    0 until universeSize foreach { n =>
      val randomInt = random.nextInt(cardinality)
      log.addItem(randomInt)
    }
    
    log
  }

  def expectLogLogCount(log: HyperLogLog, expectedItemsCount: Int, expectedError: Double) = {
    val expected = expectedItemsCount.toDouble
    val error = errorRate(log.logLogCount, expected)

    println(s"Expected $expected, got ${log.logLogCount}, error : $error")

    error should be <= expectedError
  }

  def expectWithError(actualCount: Double, expectedItemsCount: Int, expectedError: Double) = {
    val expected = expectedItemsCount.toDouble
    val error = errorRate(actualCount, expected)

    println(s"Expected $expected, got $actualCount, error : $error")

    error should be <= expectedError
  }

  def errorRate(actualCount: Double, expected: Double): Double = {
    (abs(expected - actualCount) / actualCount) * 100.0
  }
}
