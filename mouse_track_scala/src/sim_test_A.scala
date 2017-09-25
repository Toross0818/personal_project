import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ArrayBuffer

/**
  * Created by ss on 2017/8/12.
  */
object sim_test_A {
  def cal (abRDD : RDD[(String,String,String)]):ArrayBuffer[String]={
    val ab = abRDD.collect()
    val line_ab = new ArrayBuffer[String]()
    for(index1 <- 0 until ab.length){
      val id1 =ab(index1)._1
      val incre1= ab(index1)._2
      val line1 = ab(index1)._3
      for(index2 <- index1+1 until ab.length){
        val id2 =ab(index2)._1
        val incre2= ab(index2)._2
        val line2= ab(index2)._3

        if(incre1 == incre2) {
          line_ab.append(line1)
          line_ab.append(line2)
        }
      }
      println(index1)
    }
    line_ab
  }

  def get_strs(line:String)={
    val arr = line.split(" ")
    val id = arr(0)
    val track_str = arr(1).split(";")
    val len_track = track_str.length
    val x_ab = new ArrayBuffer[Int]()
    for(i <- 0 until len_track){
      x_ab.append(track_str(i).split(",")(0).toInt)
    }
    var flag = 0
    if(x_ab.max - x_ab(x_ab.length-1) >0){
      flag = 1
    }
    val incre_ab = new ArrayBuffer[Int]()

    for(i<- 0 until x_ab.length -1){
      incre_ab.append(x_ab(i+1) - x_ab(i))
    }
    (id,incre_ab.mkString(","),line,flag)
  }


  def main(args: Array[String]): Unit = {
    val input = args(1)
    val output = args(0)

    val conf = new SparkConf().setAppName("sim_test_A")
    val sc = new SparkContext(conf)
    val ori_data = sc.textFile(input).map(get_strs).filter(_._4 == 0).map{line => (line._1,line._2,line._3)}


    val res = cal(ori_data).distinct

    val result = sc.parallelize(res)
    result.saveAsTextFile(output)
  }

}
