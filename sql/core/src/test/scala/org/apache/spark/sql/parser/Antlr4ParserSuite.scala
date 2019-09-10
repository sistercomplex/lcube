package org.apache.spark.sql.parser

import org.apache.spark.SparkFunSuite
import org.apache.spark.sql.execution.SparkSqlParser
import org.apache.spark.sql.internal.SQLConf

class Antlr4ParserSuite extends SparkFunSuite{

  test("test antlr4 parser"){

    val parser = new SparkSqlParser(new SQLConf)
    val plan = parser.parsePlan("select name from tmp where age > 18 and id < 16 order by id desc")
    logDebug(s"${plan.resolved}")
    logDebug("\n"+plan.toJSON)







  }



}
