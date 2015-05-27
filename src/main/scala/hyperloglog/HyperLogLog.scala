package hyperloglog

import java.lang.Long.numberOfLeadingZeros
import java.lang.Math.{log, max, pow}

import com.google.common.hash.Hashing._
import hyperloglog.HyperLogLog._

class HyperLogLog(numBucketBits: Int) {
  val bucketCount = 1 << numBucketBits
  val buckets = new Array[Int](bucketCount)
  var count = 0

  for (a <- 0 until bucketCount) {
    buckets(a) = 0
  }

  val biasCorrectionForHyperLogLog = 1.0 / (2.0 * log(2) * (1.0 + (3.0 * log(2) - 1) / bucketCount))

  val biasCorrectionForLogLog = 0.395

  def addHash(hashcode: Long): Unit = {
    val bucketIndex = computeBucketIndex(hashcode)
    val firstOneRank = computeFirstOneRank(hashcode)

    buckets(bucketIndex) = max(firstOneRank, buckets(bucketIndex))

    count += 1
  }

  def addItem(item: Any): Unit = {
    addHash(murmur3_128().hashInt(item.hashCode).asLong)
  }

  private[hyperloglog] def computeBucketIndex(hash: Long): Int =
    (hash & (bucketCount - 1)).toInt

  def logLogCount: Double =
    pow(2.0, linearMean(buckets)) * bucketCount * biasCorrectionForLogLog

  def hyperLogLogCount: Double = {
    val sumOfInverses = buckets.map { n =>
      1.0 / pow(2.0, n)
    }.sum

    bucketCount * bucketCount * biasCorrectionForHyperLogLog / sumOfInverses
  }
}

object HyperLogLog {
  private[hyperloglog] def linearMean(buckets: Array[Int]): Double =
    buckets.sum.toDouble / buckets.length.toDouble

  private[hyperloglog] def computeFirstOneRank(bucketHash: Long): Int =
    numberOfLeadingZeros(bucketHash) + 1
}
