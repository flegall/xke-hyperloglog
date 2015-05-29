package hyperloglog

import java.lang.Math.log

import com.google.common.hash.Hashing._

class HyperLogLog(registersBit: Int) {
  val n: Int = ???
  val registers:Array[Int] = ???
  var count: Int = ???

  val biasCorrectionForHyperLogLog = 1.0 / (2.0 * log(2) * (1.0 + (3.0 * log(2) - 1) / n))
  val biasCorrectionForLogLog = 0.395

  def addHash(hashcode: Long): Unit = ???

  def addItem(item: Any): Unit = {
    addHash(murmur3_128().hashInt(item.hashCode).asLong)
  }

  def logLogCount: Double = ???

  def hyperLogLogCount: Double = ???
}

object HyperLogLog {
  private[hyperloglog] def linearMean(buckets: Array[Int]): Double = ???

  private[hyperloglog] def computeFirstOneRank(bucketHash: Long): Int = ???

  private[hyperloglog] def computeRegisterIndex(hash: Long, n: Int): Int = ???
}
