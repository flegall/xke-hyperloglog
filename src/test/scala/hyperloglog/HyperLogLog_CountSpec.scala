package hyperloglog

import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}

import scala.util.Random

class HyperLogLog_CountSpec extends FunSpec with Matchers with BeforeAndAfterEach {
  var log: HyperLogLog = _

  override def beforeEach(): Unit = {
    log = new HyperLogLog(8)
  }

  describe("When counting with HyperLogLog") {

    it("should get unique items count") {
      val cardinality = 150 * 1000 * 1000
      val universeSize = 1000 * 1000 * 1000

      val random = new Random()
      0 to universeSize foreach { n =>
        val randomInt = random.nextInt(cardinality)
        log.addItem(randomInt)
      }
      print(log.uniqueCount)

      val expected = cardinality.toDouble
      log.uniqueCount shouldBe expected +- (expected * 0.2)
    }
  }
}
