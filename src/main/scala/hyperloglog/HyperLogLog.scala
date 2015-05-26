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

  val biasCorrection = 1.0 / (2.0 * log(2) * (1.0 + (3.0 * log(2) - 1) / bucketCount))

  def addHash(hashcode: Long): Unit = {
    val bucketIndex = computeBucketIndex(hashcode)
    val leadZeros = computeNumberOfLeadingZeros(hashcode)

    buckets(bucketIndex) = max(leadZeros, buckets(bucketIndex))

    count += 1
  }

  def addItem(item: Any): Unit = {
    addHash(murmur3_128().hashInt(item.hashCode).asLong)
  }

  private[hyperloglog] def computeBucketIndex(hash: Long): Int =
    (hash & (bucketCount - 1)).toInt

  def logLogCount: Double =
    pow(2.0, linearMean(buckets)) * bucketCount * biasCorrection

  def hyperLogLogCount: Double = {
    val sumOfPowers = buckets.map { n =>
      pow(2.0, -n.toDouble)
    }.sum

    bucketCount * bucketCount * biasCorrection * sumOfPowers
  }
}

object HyperLogLog {
  private[hyperloglog] def linearMean(buckets: Array[Int]): Double =
    buckets.sum.toDouble / buckets.length.toDouble

  private[hyperloglog] def computeNumberOfLeadingZeros(bucketHash: Long): Int =
    numberOfLeadingZeros(bucketHash)
}
