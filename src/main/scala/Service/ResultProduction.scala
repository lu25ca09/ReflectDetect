package Service

import Data.ClassReachesReflect.{Direct, Transitive}
import Data.{ClassInfo, ClassReachesReflect, MethodInfo}


/**
 * this class creates the result of the analysis and returns it as a string
 *
 * @param rootService interface to communicate between domains and classes
 */
class ResultProduction(var rootService: RootService){
  val result : StringBuilder = new StringBuilder
// TODO: Falls Zeit serialisierung in JSON o.ä.
// TODO: real world einfach projekte unabhängig von Vorkommen von Reflection untersuchen
  /**
   * kicks off the computation of the result and returns it after it finished
   * @return
   */
  def getReport: String = {
    addProjectResult()
    result.result()
    }

  /**
   * main handling of the result production
   * it iterates trough all classes and calls addSingleClassInfo
   */
  private def addProjectResult(): Unit ={
    val projectInfo = rootService.projectInfo
    val temp = projectInfo.projectPath.split("/")
    val projectName = temp(temp.size-1)
    result.++=("Project: " + projectName)
    result.++=("\nClasses containing reachable Reflections count: " + projectInfo.getdirectCounter)
    result.++=("\nClasses reaching reflection calls only transitively count: " + projectInfo.getTransitiveCounter)
    result.++=("\nMethods containing reachable Reflections count: " + projectInfo.getMethodCounter)
    projectInfo.classes.values.foreach(classInfo => {
      addSingleClassToResult(classInfo)

    })
  }

  /**
   * adds information about a certain Class to the result string
   * to add the Information for all methods inside addSingleMethodToResults gets called for all Methods of the class
   *
   * @param classInfo dataStructure the results are based on
   */
  private def addSingleClassToResult(classInfo: ClassInfo):Unit = {
    if(classInfo.reflectReach!=ClassReachesReflect.NoReflect ) {

      val className = classInfo.classFile.thisType.simpleName
      result.++=("\n-----------------------------\nClass: " + className)
      result.++=("\nreachable Reflect Calls: " + classInfo.reachableReflectMethods.size)
      if(classInfo.reflectReach==Transitive){
        result.++=("\nClass only reaches reflection calls transitively")
      }else if (classInfo.reflectReach==Direct) {
        result.++=("\nClass reaches Reflection Calls directly")

        result.++=("\nDirect Features: ")
        classInfo.directFeatures.foreach(temp => {
          result.++=("\n" + temp)
        })
        result.++=("\nMethods of " + className + ":")
        classInfo.methods.values.foreach(methodInfo => {
          addSingleMethodToResult(methodInfo)
        })
      }
    }
  }

  /**
   * adds information about a certain method to the result string
   *
   * @param methodInfo dataStructure the results are based on
   */
  private def addSingleMethodToResult(methodInfo: MethodInfo):Unit = {
    if (methodInfo.reachableReflectMethods.nonEmpty) {
      result.++=("\n\nMethod: " + methodInfo.method.name)
      result.++=("\nReachable Reflect Methods Count: " + methodInfo.reachableReflectMethods.size)
      result.++=("\nReflect Features: ")
      methodInfo.directFeatures.foreach(temp => {
        result.++=("\n" + temp)
      })

        result.++=("\nReachable Reflect Methods: ")
        // before running trough all reachable reflect methods they are getting tranformed to a list so it gets sorted by PC
        methodInfo.reachableReflectMethods.values.toList.sortWith(_.pc < _.pc).foreach(reachableReflectMethod => {
          result.++=("\n\nCall of " +
            reachableReflectMethod.method.toString() +
            "\nat PC = " + reachableReflectMethod.pc +
            "\nat line " + reachableReflectMethod.lineNumber +
           // "\nparameter amount "+(reachableReflectMethod.parameterIntra.size+reachableReflectMethod.parameterConstOrInter.size)+
            // "\nparameters resolvable intra-procedural:  " + reachableReflectMethod.parameterIntra.size +
            // "\nparameters constant or resolvable inter-procedural:  " + reachableReflectMethod.parameterConstOrInter.size +
            "\nFeature Category: " + reachableReflectMethod.directFeature)
        })

    }
  }
}
