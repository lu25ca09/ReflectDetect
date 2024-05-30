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


sealed abstract class DirectDynamicReflectFeature(val name : String, val soundnessType : String){

  override def toString: String = name + " which is " + soundnessType
}
// dynamic features
case object eLoadClass extends DirectDynamicReflectFeature("LoadClass","Dynamic")
case object eConstructObject extends DirectDynamicReflectFeature("ConstructObject","Dynamic")
case object eProxy  extends DirectDynamicReflectFeature("Proxy","Dynamic")
case object eAccessObject extends DirectDynamicReflectFeature("AccessObject","Dynamic")
case object eManipulateObject  extends DirectDynamicReflectFeature("ManipulateObject","Dynamic")
case object eManipulateMetaObject  extends DirectDynamicReflectFeature("ManipulateMetaObject","Dynamic")
case object eInvokeMethod  extends DirectDynamicReflectFeature("InvokeMethod","Dynamic")
case object eArray extends DirectDynamicReflectFeature("Array","Dynamic")
case object eCasts  extends DirectDynamicReflectFeature("Casts","Dynamic")

// Static features
case object eLookUpMetaObject extends DirectDynamicReflectFeature("LookUpMetaObject","Static")
case object eTraverseMetaObject extends DirectDynamicReflectFeature("TraverseMetaObject","Static")
case object eSignature extends DirectDynamicReflectFeature("Signature","Static")
case object eAssertions extends DirectDynamicReflectFeature("Assertions","Static")
case object eAnnotations extends DirectDynamicReflectFeature("Annotations","Static")
case object eResources extends DirectDynamicReflectFeature("Resources","Static")
case object eStringRepresentation extends DirectDynamicReflectFeature("StringRepresentation","Static")
case object eSecurity extends DirectDynamicReflectFeature("Security","Static")
// default, placeholder
case object default extends DirectDynamicReflectFeature("Default", "none")


/**
case class DirectDynamicFeature {
   val eLoadClass = "LoadClass"
}
*/













