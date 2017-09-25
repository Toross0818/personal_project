import com.typesafe.config.ConfigException.Parse
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer
import Math.abs

import akka.io.Udp.SO.Broadcast

/**
  * Created by ss on 2017/8/11.
  */
object sim {
  def cal (abRDD : RDD[(String,String)]):ArrayBuffer[Int]={
    val ab = abRDD.collect()
    val id_ab = new ArrayBuffer[Int]()
    for(index1 <- 0 until ab.length){
      val id1 =ab(index1)._1
      val incre1= ab(index1)._2
      for(index2 <- index1+1 until ab.length){
        val id2 =ab(index2)._1
        val incre2= ab(index2)._2
        if(incre1 == incre2) {
          id_ab.append(id1.toInt)
          id_ab.append(id2.toInt)
        }
      }
      println(index1)
    }
    id_ab
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
    val incre_ab = new ArrayBuffer[Int]()

    for(i<- 0 until x_ab.length -1){
      incre_ab.append(x_ab(i+1) - x_ab(i))
    }
    (id,incre_ab.mkString(","))
  }


  def main(args: Array[String]): Unit = {
    val input = args(1)
    val output = args(0)

    val conf = new SparkConf().setAppName("sim_java")
    //val conf = new SparkConf().setAppName("sim_java").setMaster("local[4]")
    val sc = new SparkContext(conf)
    //val ori_data = sc.textFile("E:\\mouse_track_scala\\data\\dsjtzs_txfz_training.txt").map(get_strs)
    val ori_data = sc.textFile(input).map(get_strs)
    val res = cal(ori_data).distinct
//    for (i <- res) println(i)
    val result = sc.parallelize(res)
    result.saveAsTextFile(output)
    println(result.count())
  }
}
