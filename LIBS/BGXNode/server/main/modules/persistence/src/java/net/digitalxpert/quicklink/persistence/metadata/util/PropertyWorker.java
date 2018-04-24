package net.bgx.bgxnetwork.persistence.metadata.util;
import net.bgx.bgxnetwork.persistence.metadata.*;

/**
 * User: A.Borisenko Date: 20.02.2007 Time: 18:16:49
 */
public class PropertyWorker{
    public static PropertyType getPropertyTypeBy(String name){
        return null;
    }
    public static String getValuePropertyAsString(ControlObject controlObject, String propertyName){
        if(controlObject != null){
            for(PropertyVal pVal : controlObject.getPropertyVals()){
                if(pVal.getPropertyType().getNamePropertyType() != null
                        && pVal.getPropertyType().getNamePropertyType().equals(propertyName)){
                    return (String) pVal.getValueObject();
                }
            }
        }
        return null;
    }

    public static String getValuePropertyAsString(LinkObject linkObject, String propertyName){
        if(linkObject != null){
            for(LinkValue linkVal : linkObject.getPropertyVals()){
                if(linkVal.getPropertyType().getNamePropertyType() != null
                        && linkVal.getPropertyType().getNamePropertyType().equals(propertyName)){
                    return (String) linkVal.getValueObject();
                }
            }
        }
        return null;
    }

    public static String getValuePropertyAsString(ControlObject controlObject, Long propertyCode){
        if(controlObject != null){
            for(PropertyVal pVal : controlObject.getPropertyVals()){
                if(pVal.getPropertyType().getCodePropertyType() != null){
                    Long lcpt = Long.parseLong(pVal.getPropertyType().getCodePropertyType());
                    if (lcpt.equals(propertyCode))
                        return (String) pVal.getValueObject();
                }
            }
        }
        return null;
    }

    public static String getValuePropertyAsString(ControlObject controlObject, Integer propertyId){
        if(controlObject != null){
            for(PropertyVal pVal : controlObject.getPropertyVals()){
                if(pVal.getPropertyType().getPropertyTypeId() != null
                        && pVal.getPropertyType().getPropertyTypeId().equals(propertyId)){
                    return (String) pVal.getValueObject();
                }
            }
        }
        return null;
    }

    public static String getValuePropertyAsString(LinkObject linkObject, Integer propertyId){
        if(linkObject != null){
            for(LinkValue linkValue : linkObject.getPropertyVals()){
                if(linkValue.getPropertyType().getPropertyTypeId() != null
                        && linkValue.getPropertyType().getPropertyTypeId().equals(propertyId)){
                    return (String) linkValue.getValueObject();
                }
            }
        }
        return null;
    }

    public static String getValuePropertyAsString(LinkObject linkObject, Long propertyCode){
        if(linkObject != null){
            for(LinkValue pVal : linkObject.getPropertyVals()){
                if(pVal.getPropertyType().getCodePropertyType() != null){
                    Long lcpt = Long.parseLong(pVal.getPropertyType().getCodePropertyType());
                    if (lcpt.equals(propertyCode))
                        return (String) pVal.getValueObject();
                }
            }
        }
        return null;
    }

    public static void setValuePropertyAsString(ControlObject controlObject, String propertyName, String val){
        if(controlObject != null){
            boolean isexist = false;
            for(PropertyVal pVal : controlObject.getPropertyVals()){
                if(pVal.getPropertyType().getNamePropertyType() != null
                        && pVal.getPropertyType().getNamePropertyType().equals(propertyName)){
                    isexist = true;
                    pVal.setValueObject(val);
                    if(isexist)
                        break;
                }
            }
            //todo
//            if (!isexist){
//
//            }
        }
    }

    public static void setValuePropertyAsString(LinkObject linkObject, String propertyName, String val){
        if(linkObject != null){
            for(LinkValue linkVal : linkObject.getPropertyVals()){
                if(linkVal.getPropertyType().getNamePropertyType() != null
                        && linkVal.getPropertyType().getNamePropertyType().equals(propertyName)){
                    linkVal.setValueObject(val);
                }
            }
        }
    }
}
