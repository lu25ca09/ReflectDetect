package Data

import org.opalj.br.{DeclaredMethod, Method}
import org.opalj.tac.DUVar
import org.opalj.value.ValueInformation

import scala.collection.mutable

class ReachableReflectMethod(val method : Method,
                             val declaredMethod: DeclaredMethod,
                             val pc : Integer,
                             val lineNumber : Integer,
                             val directFeature: DirectDynamicReflectFeature,
                             val caller : MethodInfo) {}
