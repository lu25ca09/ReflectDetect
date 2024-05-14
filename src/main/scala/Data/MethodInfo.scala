package Data

import org.opalj.br.Method

import scala.collection.mutable

class MethodInfo(val method : Method,
                 val reachableReflectMethods : mutable.HashMap[Int,ReachableReflectMethod],
                 val directFeatures : mutable.Set[ReflectFeature],
                 val declaringClass : ClassInfo){}
