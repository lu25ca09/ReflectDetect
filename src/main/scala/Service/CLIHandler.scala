package Service

import Data.ClassReachesReflect.NoReflect
import Data.{ClassInfo, MethodInfo, ProjectInfo, ReachableReflectMethod, DirectDynamicReflectFeature}
import com.typesafe.config.ConfigValueFactory
import org.opalj.br.{BaseConfig, Method, ObjectType}
import org.opalj.br.analyses.Project
import org.opalj.tac.cg.RTACallGraphKey

import java.io.{File, FileNotFoundException}
import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.collection.mutable




/**
 * entry point for the tool via command line interfaces
 * handles in and outputs in general and manages the different steps of the tool
 */
object CLIHandler {


  private var rootService : RootService = _
  private var file : File = _
  private var projectPath : String = _

  /**
   * entry point
   *
   * @param args program arguments
   */
  def main(args : Array[String]): Unit = {

    checkParams(args)
    initData()
    startAnalysis()
    returnResult()
  }


  /**
   * initalizes the data domain
   */
  private def initData(): Unit = {

    // change config to "org.opalj.br.analyses.cg.ApplicationEntryPointsFinder" for cli project in others words containing a main method
    var config  = BaseConfig
    config = config.withValue("org.opalj.br.analyses.cg.InitialEntryPointsKey.analysis",
      ConfigValueFactory.fromAnyRef("org.opalj.br.analyses.cg.LibraryEntryPointsFinder"))
    //standart rtjar, org.opalj.bytecode.JRELibraryFolder
    val tempProject: Project[URL] = Project.apply(projectFile = file,libraryFile = org.opalj.bytecode.JRELibraryFolder)
    // ugly
    val project : Project[URL] = Project.apply(projectFiles=Array(file),libraryFiles = Array(org.opalj.bytecode.JRELibraryFolder),logContext = tempProject.logContext,config = config)
    val cg = project.get(RTACallGraphKey)

    val projectInfo : ProjectInfo = new ProjectInfo(projectPath,project,cg,mutable.HashMap[ObjectType,ClassInfo]())
    rootService = new RootService(projectInfo,new Analysis(null),new ResultProduction(null))
    rootService.analysis.rootService = rootService
    rootService.resultProduction.rootService = rootService
    initClassInfo()
  }

  /**
   * initializes ClassInfo for all Classes
   */
  private def initClassInfo() : Unit = {

    rootService.projectInfo.project.allClassFiles.foreach(cf => {
      val classInfo = new ClassInfo(cf,new mutable.HashMap[Method,MethodInfo],new mutable.HashMap[Int,ReachableReflectMethod], mutable.Set[DirectDynamicReflectFeature](), NoReflect)
      rootService.projectInfo.classes.addOne(cf.thisType, classInfo)
      cf.methods.foreach(m => {

          classInfo.methods.addOne(m,new MethodInfo(method = m, reachableReflectMethods = new mutable.HashMap[Int,ReachableReflectMethod], directFeatures = mutable.Set[DirectDynamicReflectFeature](), declaringClass = classInfo))
      })
    })
  }

  /**
   * checks the program arguments and loads the jar to be analysed
   * simple at the moment
   * could be more complex and edited in the future
   *
   * @param args program arguments
   */
  private def checkParams( args : Array[String]): Unit = {
      try {
        projectPath = args(0)
        file = new java.io.File(projectPath)
        if (!file.exists()) throw new FileNotFoundException
        println(Console.GREEN + "File to be analysed: "+file)
      } catch {
          case _: ArrayIndexOutOfBoundsException => println(Console.RED + "No Arguments found"
          +"\n Syntax to start the Tool: java -jar [path/to/StaticAnalysis.jar] [path/to/projetToBeAnalysed.jar]")
          scala.sys.exit()
          case _: FileNotFoundException =>
          println(Console.RED + "Jar to be analysed could not be loaded from path: " + projectPath
                              +"\n Syntax to start the Tool: scala [path/to/StaticAnalysis.jar] [path/to/projetToBeAnalysed.jar]")
          scala.sys.exit
          case e : Exception => e.printStackTrace()
            scala.sys.exit
      }
  }

  /**
   * starts the analysis
   */
  private def startAnalysis(): Unit = {
      rootService.analysis.analyze()
  }

  /**
   * starts the computation of the result after the analysis is finished
   * result gets written into a .txt file
   */
  private def returnResult(): Unit = {
      val result : String = rootService.resultProduction.getReport
    var temp = rootService.projectInfo.projectPath.split("/")
    temp = temp(temp.length-1).split('.')
    val projectName = temp(0)+"_result"
      try {
        Files.write(Paths.get("output/"+projectName+".txt"),result.getBytes(StandardCharsets.UTF_8))
      }catch {
        case e : Exception => {
          Files.createDirectory(Paths.get("output"))
          Files.write(Paths.get("output/"+projectName+".txt"),result.getBytes(StandardCharsets.UTF_8))
        }
      }


  }
  
}
