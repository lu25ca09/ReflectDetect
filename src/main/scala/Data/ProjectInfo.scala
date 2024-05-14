package Data

import org.opalj.br.ObjectType
import org.opalj.br.analyses.Project
import org.opalj.tac.cg.CallGraph

import java.net.URL
import scala.collection.mutable




class ProjectInfo(val projectPath : String,
                  val project : Project[URL],
                  val callGraph: CallGraph,
                  val classes : mutable.HashMap[ObjectType,ClassInfo]){
  private var reachableReflectMethodCounter : Int = 0
  private var directReflectClassesCounter: Int = 0
  private var transitiveReflectClassesCounter: Int = 0

  def incrementMethodCounter(): Unit ={reachableReflectMethodCounter=reachableReflectMethodCounter+1}
  def incrementDirectCounter(): Unit ={directReflectClassesCounter=directReflectClassesCounter+1}
  def incrementTransitiveCounter(): Unit ={transitiveReflectClassesCounter=transitiveReflectClassesCounter+1}
  def decrementTransitiveCounter(): Unit ={transitiveReflectClassesCounter=transitiveReflectClassesCounter-1}
  def getMethodCounter: Int = reachableReflectMethodCounter
  def getdirectCounter: Int = directReflectClassesCounter
  def getTransitiveCounter: Int = transitiveReflectClassesCounter
}
