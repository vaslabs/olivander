package org.vaslabs.parser

import java.io.File

import com.github.tototoshi.csv.{CSVReader, CSVWriter}
import org.vaslabs.olivander.domain.dunnhamby.model._

import scala.io.Source

object Main {

  def main(args: Array[String]): Unit ={
    val orders:Stream[List[String]]  = CSVReader.open(Source.fromResource("orders_sample.csv")).toStream

    val orderProducts:List[List[String]]  = CSVReader.open(Source.fromResource("order_products__train.csv")).all()
    val aisles:List[List[String]]  = CSVReader.open(Source.fromResource("aisles.csv")).all()
    val departments:List[List[String]]  = CSVReader.open(Source.fromResource("departments.csv")).all()
    val products:List[List[String]]  = CSVReader.open(Source.fromResource("products.csv")).all()

    var aislesMapping :Map[Int,String] = Map()
    var departmentsMapping :Map[Int,String] = Map()
    var productsMapping :Map[Int,FlattyProduct] = Map()
    var orderProductsMapping :Map[Int, FlattyOrderProduct] = Map()

    var result:List[DunnhumbyProtocol] = List[DunnhumbyProtocol]()

    aisles
      .drop(1)
      .foreach(x=> aislesMapping += ( x(0).toInt -> x(1) ) )

    departments
      .drop(1)
      .foreach(x=> departmentsMapping += ( x(0).toInt -> x(1) ) )

    products
      .drop(1)
      .foreach(x=> {
        productsMapping += (
          x(0).toInt -> FlattyProduct(
            x(1),
            aislesMapping.get(x(2).toInt).orNull,
            departmentsMapping.get(x(3).toInt).orNull
          )
          )
      }
      )

    orderProducts
      .drop(1)
      .foreach(x=> {
        orderProductsMapping += (
          x(0).toInt -> FlattyOrderProduct(
            productsMapping.get(x(1).toInt).orNull.name,
            productsMapping.get(x(1).toInt).orNull.aisleName,
            productsMapping.get(x(1).toInt).orNull.departmentName,
            x(2).toInt,
            x(3).toInt
          )
          )
      }
      )


    orders
      .drop(1)
      .filter(x=>x.contains("train"))
      .foreach(x=> {
        println(x(0))
        result ::= DunnhumbyProtocol(
          x(1).toInt,
          x(0).toInt,
          orderProductsMapping.get(x(0).toInt).orNull.productName,
          orderProductsMapping.get(x(0).toInt).orNull.aisleName,
          orderProductsMapping.get(x(0).toInt).orNull.departmentName,
          orderProductsMapping.get(x(0).toInt).orNull.addToCartOrder,
          orderProductsMapping.get(x(0).toInt).orNull.reordered,
          x(3).toInt,
          x(4).toInt,
          x(5).toInt,
          x(6).toDouble.toInt
        )
      }
      )


    val f = new File("out.csv")
    val writer = CSVWriter.open(f)

    result
      .map(r => List(r.userId,
        r.orderId,
        r.productName,
        r.aisleName,
        r.departmentName,
        r.addToCartOrder,
        r.reordered,
        r.orderNum,
        r.orderDow,
        r.orderHod,
        r.daysSincePrior))
      .foreach(writer.writeRow)


  }

  case class FlattyProduct(val name: String, aisleName:String, departmentName:String)

  case class FlattyOrderProduct(val productName:String, aisleName:String, departmentName:String, addToCartOrder:Int, reordered:Int)

}
