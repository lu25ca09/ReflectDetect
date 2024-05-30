package Service

import Data.ClassReachesReflect.{Direct, NoReflect, Transitive}
import Data._
import org.opalj.br.DeclaredMethod
import org.opalj.tac.DUVar
import org.opalj.value.ValueInformation

import scala.collection.mutable


class Analysis(var rootService: RootService) {

  def analyze(): Unit = {
    rootService.projectInfo.callGraph.reachableMethods().foreach(m => {

      if (isReflect(m.method)) {

        val directDynamicFeature = getFeature(m.method)
        if (directDynamicFeature != default) {
          rootService.projectInfo.callGraph.callersOf(m.method).iterator.foreach(caller => {
            // secures that only the called method of a specific class and the class itself gets info added
            // caller._3 is true for direct calls
            if (caller._3) {
              val callerClassInfo = rootService.projectInfo.classes(caller._1.declaringClassType)
              // TODO: hier eventuell einfügen attribute, parameter und fields der Klasse/Method auf Reflect ObjectType zu prüfen
              val callerMethodInfo = callerClassInfo.methods(caller._1.definedMethod)
              //linenumber
              // caller._1.definedMethod.body.get.lineNumber(caller._2).get

              val body = caller._1.definedMethod.body.get
              val temp = body.lineNumber(caller._2)


              // TODO: manchmal ist temp none. im debugger existiert der pc im body. wieso wird der fehler geworfen???
              var linenumber = -1
              if (!temp.isEmpty) {
                linenumber = temp.get

              }

              val reachedReflectMethod = new ReachableReflectMethod(
                method = m.method.definedMethod,
                declaredMethod = m.method,
                pc = caller._2,
                lineNumber = linenumber,
                directFeature = directDynamicFeature,
                parameterIntra = mutable.Set[DUVar[ValueInformation]](),
                parameterConstOrInter = mutable.Set[DUVar[ValueInformation]](),
                caller = callerMethodInfo)
              addDynamicLanguageFeature(callerClassInfo, callerMethodInfo, reachedReflectMethod)
              addReachableReflectMethod(callerClassInfo, callerMethodInfo, caller._2, reachedReflectMethod)
              addTransitiveInfo(m.method)


            }

          })

        }
      }
    })

  }

  private def getFeature(pMethod: DeclaredMethod): Data.DirectDynamicReflectFeature = {
    val javaRep = pMethod.toJava
    if (javaRep.contains("forName(") ||
      javaRep.contains("loadClass")) {
      return eLoadClass
    } else if (javaRep.contains("getProxyClass") ||
      javaRep.contains("getInvocationHandler") ||
      javaRep.contains("newProxyInstance")) {
      return eProxy
    } else if ((javaRep.contains("set") && javaRep.contains("(java.lang.Object,int,java.lang.Object)")) || //hier vielleicht teilstring für Integer falsch TODO: testen
      (javaRep.contains("newInstance") && javaRep.contains("java.lang.Class") && javaRep.contains("int")) ||
      (javaRep.contains("get") && javaRep.contains("java.lang.Object,int"))) {
      return eArray
    } else if (javaRep.contains("cast(")) {
      return eCasts
    } else if (javaRep.contains("Field") && javaRep.contains("get") && javaRep.contains("java.lang.Object")) {
      return eAccessObject
    } else if (javaRep.contains("newInstance(java.lang.Object") ||
      javaRep.contains("newInstance()")) {
      return eConstructObject
    } else if (javaRep.contains("invoke(")) {
      return eInvokeMethod
    } else if (javaRep.contains("setAccessible(boolean)")) {
      return eManipulateMetaObject
    } else if (javaRep.contains("set") && javaRep.contains("(java.lang.Object,java.lang.Object)")) {
      return eManipulateObject
    }


    // TODO: Parameter von Reflection Calls mit überprüfen... siehe Paper JUDGE
    // TODO: parameter auf user input prüfen, bsp. string auf constant prüfen, nicht konstant nicht trivial also cfg analyse nötig
    default
  }

  private def addTransitiveInfo(pDeclaredMethod: DeclaredMethod): Unit = {
    val list = mutable.ListBuffer[DeclaredMethod]()
    val classMap = rootService.projectInfo.classes
    val callGraph = rootService.projectInfo.callGraph
    //inital iteration trough callers of the parametr
    callGraph.callersOf(pDeclaredMethod).iterator.foreach(context => {
      if (classMap(context._1.declaringClassType).reflectReach == NoReflect) {
        classMap(context._1.declaringClassType).reflectReach = Transitive
        rootService.projectInfo.incrementTransitiveCounter()
        list.addOne(context._1)
      }
    })
    //going trough all previous caller
    // s transitively
    while (list.nonEmpty) {
      val current = list.remove(0)
      callGraph.callersOf(current).iterator.foreach(context => {
        if (classMap(context._1.declaringClassType).reflectReach == NoReflect) {
          classMap(context._1.declaringClassType).reflectReach = Transitive
          rootService.projectInfo.incrementTransitiveCounter()
          list.addOne(context._1)
        }
      })
    }
  }


  private def addReachableReflectMethod(pClassInfo: ClassInfo, pMethodInfo: MethodInfo, pPC: Int, pReflectMethod: ReachableReflectMethod): Unit = {
    if (pClassInfo.reflectReach != Direct) {
      rootService.projectInfo.incrementDirectCounter()
      if (pClassInfo.reflectReach == Transitive) {
        rootService.projectInfo.decrementTransitiveCounter()
      }
    }
    pClassInfo.reflectReach = Direct
    pClassInfo.reachableReflectMethods.addOne(pPC, pReflectMethod)
    pMethodInfo.reachableReflectMethods.addOne(pPC, pReflectMethod)
    //addParameterInfo(pReflectMethod)
  }



  private def addDynamicLanguageFeature(pClassInfo: ClassInfo, pMethodInfo: MethodInfo, pReflectMethod: ReachableReflectMethod): Unit = {
    if (pMethodInfo.directFeatures.size <= 0) {
      rootService.projectInfo.incrementMethodCounter()
    }
    pClassInfo.directFeatures.addOne(pReflectMethod.directFeature)
    pMethodInfo.directFeatures.addOne(pReflectMethod.directFeature)
  }

  private def isReflect(method: DeclaredMethod): Boolean = {
    val temp = method.toJava //TODO: remove debugger temps
    val temp2 = method.declaringClassType.toString()
    val result =
      method.declaringClassType.toString().contains("java/lang/reflect") ||
        // these special methods are also considered reflection features
        // class.cast(<object>) cant get detected cause casts don't appear in reachable methods
        method.declaringClassType.toString().contains("java/lang/Class") && (
          method.toJava.contains("forName(java.lang.String)") ||
            method.toJava.contains("forName(java.lang.String,boolean,java.lang.ClassLoader)") ||
            method.toJava.contains("cast(java.lang.Object") ||
            method.toJava.contains("java.lang.Object newInstance()")
          ) ||
        method.declaringClassType.toString().contains("java/lang/ClassLoader") && method.toJava.contains("loadClass(java.lang.String)")
    result
  }


}
/* Does not work
  //TODO: Optional fertig stellen
  private def addParameterInfo(pReflectMethod: ReachableReflectMethod): Unit = {

    val caller = pReflectMethod.caller
    try {
      // underneath getter for method parameters

      type V = DUVar[ValueInformation]
      implicit val tacai: Method => TACode[TACMethodParameter, V] = rootService.projectInfo.project.get(LazyTACUsingAIKey)
      val tac = tacai.apply(caller.method)
      // implicit val body: Array[Stmt[V]] = tac.stmts
      val index = tac.properStmtIndexForPC(pReflectMethod.pc)
      if (index != -1) {
        val stmt = tac.stmts(index)
        var params: Seq[Expr[V]] = {
          //assignment, ExprStmt, VirtualMethodCall, StaticMethodCall
          if (stmt.isExprStmt) {
            if (stmt.asExprStmt.expr.isVirtualFunctionCall){
              stmt.asExprStmt.expr.asVirtualFunctionCall.params

            } else {
              stmt.asExprStmt.expr.asStaticFunctionCall.params
            }


          } else if (stmt.isAssignment) {
            if (stmt.asAssignment.expr.isVirtualFunctionCall) {
              stmt.asAssignment.expr.asVirtualFunctionCall.params
            } else {
              stmt.asAssignment.expr.asStaticFunctionCall.params
            }

          } else if (stmt.isVirtualMethodCall) {
            stmt.asVirtualMethodCall.params
          } else if (stmt.isStaticMethodCall) {
            stmt.asStaticMethodCall.params
          } else {
            throw new Exception("could not identify params for: " + pReflectMethod.method.name + " at PC: " + pReflectMethod.pc)
          }

        }
        params.foreach(p => {
          //definedBy contains indexes of instructions inside the callers body where the param gets initialized
          //if an index is negative its either a constant or outside of the body initialized

          if (p.asVar.definedBy.filter(_<0).size <1){
            pReflectMethod.parameterIntra.addOne(p.asVar)

          }else{
            pReflectMethod.parameterConstOrInter.addOne(p.asVar)
          }
        })

      }
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
 */