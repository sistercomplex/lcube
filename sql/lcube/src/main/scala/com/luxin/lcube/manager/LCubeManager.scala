package com.luxin.lcube.manager

import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.hive.conf.HiveConf
import org.apache.hive.service.CompositeService
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.catalyst.catalog.CatalogDatabase
import org.apache.spark.sql.hive.HiveUtils
import org.apache.spark.sql.hive.thriftserver.SparkSQLEnv

class LCubeManager(val sqlContext: SQLContext) extends CompositeService("LCubeManager") {


  override def init(hiveConf: HiveConf): Unit = {

    val hiveClient = HiveUtils.newClientForExecution(
      SparkSQLEnv.sqlContext.sparkContext.getConf,
      SparkSQLEnv.sqlContext.sessionState.newHadoopConf())
    val catalog = SparkSQLEnv.sqlContext.sessionState.catalog
    if (!hiveClient.databaseExists("lcube")) {
      hiveClient.createDatabase(
        CatalogDatabase(
          "lcube",
          "LCUBE tmp work database", catalog.getDefaultDBPath("lcube"), Map.empty
        ), true)

    }


    val databases = hiveClient.listDatabases("*")


    super.init(hiveConf)
  }

  override def start(): Unit = {
    super.start()
  }
}
