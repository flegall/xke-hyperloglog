package hyperloglog

import java.lang.Long.numberOfLeadingZeros
import java.lang.Math.{log, max, pow}

import com.google.common.hash.Hashing._
import hyperloglog.HyperLogLog._

class HyperLogLog(registersBit: Int) {
  val n = 1 << registersBit
  val registers = new Array[Int](n)
  var count = 0

  for (a <- 0 until n) {
    registers(a) = 0
  }

  val biasCorrectionForHyperLogLog = 1.0 / (2.0 * log(2) * (1.0 + (3.0 * log(2) - 1) / n))
  val biasCorrectionForLogLog = 0.395

  def addHash(hashcode: Long): Unit = {
    val registerIndex = computeRegisterIndex(hashcode, n)
    val firstOneRank = computeFirstOneRank(hashcode)

    registers(registerIndex) = max(firstOneRank, registers(registerIndex))

    count += 1
  }

  def addItem(item: Any): Unit = {
    addHash(murmur3_128().hashInt(item.hashCode).asLong)
  }

  def logLogCount: Double =
    pow(2.0, linearMean(registers)) * n * biasCorrectionForLogLog

  def hyperLogLogCount: Double = {
    n * n * biasCorrectionForHyperLogLog / registers.map { i =>
      1.0 / pow(2.0, i)
    }.sum
  }
}

object HyperLogLog {
  private[hyperloglog] def linearMean(buckets: Array[Int]): Double =
    buckets.sum.toDouble / buckets.length.toDouble

  private[hyperloglog] def computeFirstOneRank(bucketHash: Long): Int =
    numberOfLeadingZeros(bucketHash) + 1

  private[hyperloglog] def computeRegisterIndex(hash: Long, n: Int): Int =
    (hash & (n - 1)).toInt
}
