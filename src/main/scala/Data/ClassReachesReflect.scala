package Data

/**

enum ClassReachesReflect(val name : String){
  case Direct extends ClassReachesReflect("Direct")

  case Transitive extends ClassReachesReflect("Transitive")

  case NoReflect extends ClassReachesReflect("NoReflect")
}
*/

/**
 *
 */

object ClassReachesReflect extends Enumeration {
  type ClassReachesReflect = Value

  val Direct : Data.ClassReachesReflect.Value = Value("Direct")
  val Transitive: Data.ClassReachesReflect.Value = Value("Transitive")
  val NoReflect : Data.ClassReachesReflect.Value = Value("Reflect Call are not transitively reachable")



}