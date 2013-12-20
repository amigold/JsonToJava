import scala.util.parsing.json.JSON
import scala.collection.mutable.MutableList
import java.io.{File, PrintWriter}
import scala.xml.Null
import java.math.BigDecimal

object JsonToJava {

  type JsonObject = Map[String, Any]
  type JsonArray = List[Any]

  JSON.globalNumberParser = (input: String) => {
    if (input.contains("."))
      new BigDecimal(input)
    else
      Integer.parseInt(input)
  }

  def evalObject(className: String, obj: JsonObject): Boolean = {

    val writer = new PrintWriter(new File(String.format("../%s.java", upperFirstChar(className))))

    for ((k, v) <- obj) {
      v match {
        case value: JsonObject => {
          writeToFile(writer, classNameify(k));
          evalObject(k, value)
        }
        case value: JsonArray => evalArray(writer, k, value)
        case _ => evalPrimitive(writer, k, v)
      }
    }

    writer.close()
    true
  }

  def classNameify(str: String): String = {
    String.format("public %s %s;\n",
      upperFirstChar(str), str)
  }

  def upperFirstChar(str: String): String = {
    str.substring(0, 1).toUpperCase() + str.substring(1, str.length())
  }

  def evalPrimitive(writer: PrintWriter, fieldName: String, primitive: Any): Boolean = {

    if (primitive == null)
      writeToFile(writer, String.format("public %s %s;\n", "String",
        fieldName));
    else
      writeToFile(writer, String.format("public %s %s;\n", primitive.getClass.getSimpleName(),
        fieldName));
    true
  }

  def writeToFile(writer: PrintWriter, str: String) = {
    //    println(str);
    writer.write(str);
  }

  def evalArray(writer: PrintWriter, className: String, arr: List[Any]): Boolean = {

    if (arr.isEmpty) {
      writeToFile(writer, String.format("public %s %s;\n", "String[]",
        className));
      true
    } else {

      writeToFile(writer, String.format("public ArrayList<%s> %s;\n", upperFirstChar(className),
        className));

      arr.head match {
        case item: JsonObject => evalObject(className, item)
        case item: JsonArray => evalArray(writer, className, arr)
        //        case _ => evalPrimitiveArray(writer, fieldName, primitive)
      }
    }
  }

  def main(args: Array[String]) {

    if (args.isEmpty) {
      println("USAGE: java -jar jsontojava.jar <JSON_FILE_PATH>")
      return
    }

    val json = io.Source.fromFile(args.head).mkString
    val result = JSON.parseFull(json)

    result match {
      case Some(e) => e match {
        case x: JsonObject => evalObject("RootObject", x)
        //        case x: JsonArray => evalArray(x)
        case _ => println("Error!")
      }
      case None => println("Failed.")
    }
  }
}