package com.patrickmcgeever.clusterexampleworker

object HelloLauncher {

  def primesUnder(n: Long): List[Long] = {
    require(n >= 2)
    def rec(i: Long, primes: List[Long]): List[Long] = {
      if (i >= n) primes
      else if (prime(i, primes)) rec(i + 1, i :: primes)
      else rec(i + 1, primes)
    }
    rec(2, List()).reverse
  }

  def prime(num: Long, factors: List[Long]): Boolean = factors.forall(num % _ != 0)

  def main(args: Array[String]): Unit = {
    println("I am alive!!!!")

    println(primesUnder(300000).sum)
  }

}
