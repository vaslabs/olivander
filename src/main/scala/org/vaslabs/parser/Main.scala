package org.vaslabs.parser

import java.io.{File, PrintWriter}

import com.github.tototoshi.csv.{CSVReader, CSVWriter}
import org.vaslabs.olivander.domain.dunnhamby.model.{Order, Product}

import scala.io.Source
import io.circe.syntax._
import io.circe.generic.auto._
object Main {

  def main(args: Array[String]): Unit = {
    val orders: Stream[List[String]] = CSVReader.open(Source.fromResource("orders.csv")).toStream

    val orderProducts: List[List[String]] = CSVReader.open(Source.fromResource("order_products__prior.csv")).all()
    val aisles: List[List[String]] = CSVReader.open(Source.fromResource("aisles.csv")).all()
    val departments: List[List[String]] = CSVReader.open(Source.fromResource("departments.csv")).all()
    val products: List[List[String]] = CSVReader.open(Source.fromResource("products.csv")).all()

    var aislesMapping: Map[Int, String] = Map()
    var departmentsMapping: Map[Int, String] = Map()
    var productsMapping: Map[Int, FlattyProduct] = Map()

    var result: List[Order] = List[Order]()

    aisles
      .drop(1)
      .foreach(x => aislesMapping += (x(0).toInt -> x(1)))

    departments
      .drop(1)
      .foreach(x => departmentsMapping += (x(0).toInt -> x(1)))

    products
      .drop(1)
      .foreach(x => {
        productsMapping += (
          x(0).toInt -> FlattyProduct(
            x(1),
            aislesMapping.get(x(2).toInt).orNull,
            departmentsMapping.get(x(3).toInt).orNull
          )
          )
      }
      )

    def getFlattyOrderProduct(x: List[String]) = {
      Product(
        productsMapping.get(x(1).toInt).orNull.name,
        productsMapping.get(x(1).toInt).orNull.aisleName,
        productsMapping.get(x(1).toInt).orNull.departmentName,
        x(2).toInt,
        x(3).toInt
      )
    }

    val orderProductsMapping: Map[Int, List[Product]] = orderProducts.drop(1)
        .map(l => l(0).toInt -> getFlattyOrderProduct(l))
          .groupBy(tuple => tuple._1)
            .mapValues(_.map(_._2))



    orders
      .drop(1)
      .filter(x => x.contains("prior"))
      .foreach(x => {
        println(x(0))
        result ::= Order(
          x(1).toInt,
          x(0).toInt,
          orderProductsMapping.get(x(0).toInt).get,
          x(3).toInt,
          x(4).toInt,
          x(5).toInt,
          x(6).toDouble.toInt
        )
      }
      )


    val f = new File("out.jsonrowsprior")
    val printWriter = new PrintWriter(f)
    result.map(_.asJson.noSpaces)
        .foreach(printWriter.println)

    printWriter.close()


  }

  case class FlattyProduct(val name: String, aisleName: String, departmentName: String)

}
