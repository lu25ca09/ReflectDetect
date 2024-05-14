package Data

/**
    enum DirectDynamicFeature(val name : String){
      case eLoadClass extends DirectDynamicFeature("LoadClass")

      case  eConstructObject extends DirectDynamicFeature("ConstructObject")

      case  eProxy extends DirectDynamicFeature("Proxy")

      case  eAccessObject extends DirectDynamicFeature("AccessObject")

      case  eManipulateObject extends DirectDynamicFeature("ManipulateObject")

      case  eManipulateMetaObject extends DirectDynamicFeature("ManipulateMetaObject")

      case  eInvokeMethod extends DirectDynamicFeature("InvokeMethod")

      case  eArray extends DirectDynamicFeature("Array")

      case  eCasts extends DirectDynamicFeature("Casts")
      
      case eDefault extends DirectDynamicFeature("default")
    }
*/


sealed abstract class ReflectFeature (val name : String, val soundnessType : String){

  override def toString: String = name + " which is " + soundnessType
}
// dynamic features
case object eLoadClass extends ReflectFeature("LoadClass","Dynamic")
case object eConstructObject extends ReflectFeature("ConstructObject","Dynamic")
case object eProxy  extends ReflectFeature("Proxy","Dynamic")
case object eAccessObject extends ReflectFeature("AccessObject","Dynamic")
case object eManipulateObject  extends ReflectFeature("ManipulateObject","Dynamic")
case object eManipulateMetaObject  extends ReflectFeature("ManipulateMetaObject","Dynamic")
case object eInvokeMethod  extends ReflectFeature("InvokeMethod","Dynamic")
case object eArray extends ReflectFeature("Array","Dynamic")
case object eCasts  extends ReflectFeature("Casts","Dynamic")

// Static features
case object eLookUpMetaObject extends ReflectFeature("LookUpMetaObject","Static")
case object eTraverseMetaObject extends ReflectFeature("TraverseMetaObject","Static")
case object eSignature extends ReflectFeature("Signature","Static")
case object eAssertions extends ReflectFeature("Assertions","Static")
case object eAnnotations extends ReflectFeature("Annotations","Static")
case object eResources extends ReflectFeature("Resources","Static")
case object eStringRepresentation extends ReflectFeature("StringRepresentation","Static")
case object eSecurity extends ReflectFeature("Security","Static")
// default, placeholder
case object default extends ReflectFeature("Default", "none")


/**
case class DirectDynamicFeature {
   val eLoadClass = "LoadClass"
}
*/













