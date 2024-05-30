package Data

import Data.ClassReachesReflect.ClassReachesReflect
import org.opalj.br.{ClassFile, Method}

import scala.collection.mutable



class ClassInfo(val classFile : ClassFile,
                val methods : mutable.HashMap[Method ,MethodInfo],
                val reachableReflectMethods : mutable.HashMap[Int,ReachableReflectMethod],
                val directFeatures : mutable.Set[DirectDynamicReflectFeature],
                var reflectReach : ClassReachesReflect){}
