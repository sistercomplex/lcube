package com.luxin.lcube.server

import com.luxin.lcube.manager.LCubeManager
import org.apache.hadoop.hive.conf.HiveConf
import org.apache.hive.service.CompositeService
import org.apache.spark.SparkEnv
import org.apache.spark.sql.SQLContext
import org.apache.spark.ui.JettyUtils._
import org.apache.spark.ui.ServerInfo
import org.eclipse.jetty.servlet.ServletContextHandler

import scala.collection.mutable.ArrayBuffer


class LCubeHttpServer(val sqlContext: SQLContext) extends CompositeService("LCubeHttpServer") {

  val conf = sqlContext.sparkContext.getConf

  val lCubeManager = new LCubeManager(sqlContext)

  val handlers = ArrayBuffer[ServletContextHandler]()
  var host: String = "0.0.0.0"
  var serverInfo: ServerInfo = _


  override def init(hiveConf: HiveConf) {
    addService(lCubeManager)
    super.init(hiveConf)

  }

  override def start(): Unit = {
    val port: Int = conf.getInt("spark.lcube.http.port", 50055)
    val sSLOptions = SparkEnv.get.securityManager.getSSLOptions("ui")
    serverInfo = startJettyServer(host, port,
      sSLOptions, handlers, conf, "LCubeHttpServer")
    super.start()
  }

  override def stop(): Unit = {
    serverInfo.stop()
    super.stop()
  }
}
