package hyperloglog

import java.lang.Integer.numberOfLeadingZeros
import java.lang.Math.{max, log}

class HyperLogLog(numBucketBits: Int) {
  val bucketCount = 1 << numBucketBits
  val buckets = new Array[Int](bucketCount)
  var count = 0

  for (a <- 0 until bucketCount) {
    buckets(a) = 0
  }

  val biasCorrection = 1.0 / (2.0 * log(2) * (1.0 + (3.0 * log(2) - 1) / bucketCount))

  def add(item: Any): Unit =  {
    add(item.hashCode)
  }

  def add(hashcode: Int): Unit = {
    val hash = absoluteValue(hashcode)
    val bucketIndex = computeBucketIndex(hash)
    val bucketHash = computeBucketHash(hash)
    val leadZeros = computeNumberOfLeadingZeros(bucketHash)

    buckets(bucketIndex) = max(leadZeros, buckets(bucketIndex))

    count += 1
  }

  private[hyperloglog] def absoluteValue(hash: Int) : Int = Math.abs(hash)

  private[hyperloglog] def computeBucketIndex(hash: Int): Int = {
    hash >> (Integer.SIZE - numBucketBits)
  }

  private[hyperloglog] def computeBucketHash(hash: Int): Int = {
    hash << numBucketBits | (bucketCount - 1)
  }

  private[hyperloglog] def computeNumberOfLeadingZeros(bucketHash: Int): Int =
    numberOfLeadingZeros(bucketHash)
}
