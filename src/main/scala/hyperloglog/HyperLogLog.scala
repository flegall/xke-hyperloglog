package hyperloglog

import java.util

import com.google.common.hash.Hashing._

import scala.collection.JavaConverters._


class HyperLogLog(registersBit: Int) {
  val hyperLogLogJava = new HyperLogLogJava(registersBit)
  def n = hyperLogLogJava.getN
  def registers:Array[Int] = hyperLogLogJava.getRegisters.asScala.toArray[Integer].map { _.intValue}
  def count = hyperLogLogJava.getCount

  def biasCorrectionForHyperLogLog = hyperLogLogJava.getBiasCorrectionForHyperLogLog
  def biasCorrectionForLogLog = hyperLogLogJava.getBiasCorrectionForLogLog

  def addHash(hashcode: Long): Unit = {
    hyperLogLogJava.addHash(hashcode)
  }

  def addItem(item: Any): Unit = {
    addHash(murmur3_128().hashInt(item.hashCode).asLong)
  }

  def logLogCount: Double =
    hyperLogLogJava.getLogLogCount

  def hyperLogLogCount: Double = {
    hyperLogLogJava.getHyperLogLogCount
  }
}

object HyperLogLog {
  private[hyperloglog] def linearMean(buckets: Array[Int]): Double = {
    val integers = new util.ArrayList[Integer]()
    buckets.foreach(integers.add(_))
    HyperLogLogJava.linearMean(integers)
  }

  private[hyperloglog] def computeFirstOneRank(bucketHash: Long): Int =
    HyperLogLogJava.computeFirstOneRank(bucketHash)

  private[hyperloglog] def computeRegisterIndex(hash: Long, n: Int): Int =
   HyperLogLogJava.computeRegisterIndex(hash, n)
}
