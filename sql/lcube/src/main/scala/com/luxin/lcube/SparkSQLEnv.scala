package org.apache.spark.sql.hive.thriftserver

import java.io.PrintStream

import org.apache.spark.internal.Logging
import org.apache.spark.sql.hive.{HiveExternalCatalog, HiveUtils}
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.util.Utils
import org.apache.spark.{SparkConf, SparkContext}

/** A singleton object for the master program. The slaves should not access this. */
object SparkSQLEnv extends Logging {
  logDebug("Initializing SparkSQLEnv")

  var sqlContext: SQLContext = _
  var sparkContext: SparkContext = _

  def init() {
    if (sqlContext == null) {
      val sparkConf = new SparkConf(loadDefaults = true)
      // If user doesn't specify the appName, we want to get [SparkSQL::localHostName] instead of
      // the default appName [SparkSQLCLIDriver] in cli or beeline.
      val maybeAppName = sparkConf
        .getOption("spark.app.name")
        //.filterNot(_ == classOf[SparkSQLCLIDriver].getName)
        //.filterNot(_ == classOf[HiveThriftServer2].getName)

      sparkConf
        .setAppName(maybeAppName.getOrElse(s"SparkSQL::${Utils.localHostName()}"))

      val sparkSession = SparkSession.builder.config(sparkConf).enableHiveSupport().getOrCreate()
      sparkContext = sparkSession.sparkContext
      sqlContext = sparkSession.sqlContext

      val metadataHive = sparkSession
        .sharedState.externalCatalog.unwrapped.asInstanceOf[HiveExternalCatalog].client
      metadataHive.setOut(new PrintStream(System.out, true, "UTF-8"))
      metadataHive.setInfo(new PrintStream(System.err, true, "UTF-8"))
      metadataHive.setError(new PrintStream(System.err, true, "UTF-8"))
      sparkSession.conf.set(HiveUtils.FAKE_HIVE_VERSION.key, HiveUtils.builtinHiveVersion)
    }
  }

  /** Cleans up and shuts down the Spark SQL environments. */
  def stop() {
    logDebug("Shutting down Spark SQL Environment")
    // Stop the SparkContext
    if (SparkSQLEnv.sparkContext != null) {
      sparkContext.stop()
      sparkContext = null
      sqlContext = null
    }
  }
}
