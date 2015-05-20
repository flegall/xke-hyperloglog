package hyperloglog

import java.lang.Long.numberOfLeadingZeros
import java.lang.Math.{pow, max, log}

class HyperLogLog(numBucketBits: Int) {
  val bucketCount = 1 << numBucketBits
  val buckets = new Array[Int](bucketCount)
  var count = 0

  for (a <- 0 until bucketCount) {
    buckets(a) = 0
  }

  val biasCorrection = 1.0 / (2.0 * log(2) * (1.0 + (3.0 * log(2) - 1) / bucketCount))

  def add(hashcode: Long): Unit = {
    val bucketIndex = computeBucketIndex(hashcode)
    val bucketHash = computeBucketHash(hashcode)
    val leadZeros = computeNumberOfLeadingZeros(bucketHash)

    buckets(bucketIndex) = max(leadZeros, buckets(bucketIndex))

    count += 1
  }

  def uniqueCount: Double = {
    val averageLeadingZeros = (buckets.sum / bucketCount) * biasCorrection
    pow(2.0, averageLeadingZeros) * bucketCount
  }

  private[hyperloglog] def computeBucketIndex(hash: Long): Int = {
    (hash & (bucketCount - 1)).toInt
  }

  private[hyperloglog] def computeBucketHash(hash: Long): Long = {
    hash >> numBucketBits
  }

  private[hyperloglog] def computeNumberOfLeadingZeros(bucketHash: Long): Int =
    numberOfLeadingZeros(bucketHash)
}
