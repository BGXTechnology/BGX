package net.bgx.bgxnetwork.bgxop.tools;

import edu.uci.ics.jung.utils.UserDataContainer;

import java.awt.*;

/**
 * Class GraphDataUtil
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public abstract class GraphDataUtil {
  public static final UserDataContainer.CopyAction shared = new UserDataContainer.CopyAction.Shared();
  public static final UserDataContainer.CopyAction clone = new UserDataContainer.CopyAction.Clone();

  public static final String key_id = "id";
  public static final String key_name = "name";
  public static final String key_type = "type";
  public static final String key_cost = "cost";
  public static final String key_color = "color";
  public static final String key_highlight = "highlight";
  public static final String key_mark = "mark";
  public static final String key_visible = "visible";
  public static final String key_height = "height";
  public static int getHeight(UserDataContainer obj){
		return getInt(obj, key_height);
	}
	public static void setHeight(UserDataContainer obj, int height){
		obj.setUserDatum(key_height, height, clone);
	}
  public static int getId(UserDataContainer obj) {
    return getInt(obj, key_id);
  }

  public static void setId(UserDataContainer obj, int id) {
    obj.setUserDatum(key_id, id, clone);
  }

  public static String getName(UserDataContainer obj) {
    return getString(obj, key_name);
  }

  public static void setName(UserDataContainer obj, String name) {
    obj.setUserDatum(key_name, name, clone);
  }

  public static String getType(UserDataContainer obj) {
    return getString(obj, key_type);
  }

  public static void setType(UserDataContainer obj, String name) {
    obj.setUserDatum(key_type, name, clone);
  }

  public static double getCost(UserDataContainer obj) {
    return getDouble(obj, key_cost);
  }

  public static void setCost(UserDataContainer obj, double cost) {
    obj.setUserDatum(key_cost, cost, clone);
  }

  public static Color getColor(UserDataContainer obj) {
    Object o = obj.getUserDatum(key_color);
    if (o!=null && (o instanceof Color)) return (Color) o;
    return null;
  }

  public static void setColor(UserDataContainer obj, Color c) {
    if (c==null) obj.removeUserDatum(key_color);
    else obj.setUserDatum(key_color, c, clone);
  }

  public static boolean getHighlighted(UserDataContainer obj) {
    return getBoolean(obj, key_highlight, false);
  }

  public static void setHighlighted(UserDataContainer obj, boolean val) {
    setBoolean(obj, key_highlight, val);
  }

  public static boolean getMarked(UserDataContainer obj) {
    return getBoolean(obj, key_mark, false);
  }

  public static void setMarked(UserDataContainer obj, boolean val) {
    setBoolean(obj, key_mark, val);
  }

  public static Boolean getVisible(UserDataContainer obj) {
    return getBoolean(obj, key_visible, true);
  }

  public static void setVisible(UserDataContainer obj, boolean val) {
    setBoolean(obj, key_visible, val);
  }

  //**********************************

  public static int getInt(UserDataContainer obj, String key) {
    Object o = obj.getUserDatum(key);
    if (o!=null && (o instanceof Integer)) return (Integer)o;
    return 0;
  }

  public static long getLong(UserDataContainer obj, String key) {
    Object o = obj.getUserDatum(key);
    if (o!=null && (o instanceof Long)) return (Long)o;
    return 0;
  }

  public static String getString(UserDataContainer obj, String key) {
    Object o = obj.getUserDatum(key);
    if (o!=null && (o instanceof String)) return (String)o;
    return null;
  }

  public static double getDouble(UserDataContainer obj, String key) {
    Object o = obj.getUserDatum(key);
    if (o!=null && (o instanceof Double)) return (Double)o;
    return 0;
  }

  public static Boolean getBoolean(UserDataContainer obj, String key, Boolean def) {
    Object o = obj.getUserDatum(key);
    if (o!=null && (o instanceof VisibleFlag)) return ((VisibleFlag)o).isVisible();
    return def;
  }

  public static void setBoolean(UserDataContainer obj, String key, Boolean val) {
    obj.setUserDatum(key, new VisibleFlag(val), clone);
  }
    public static class VisibleFlag implements Cloneable {
        private boolean visible;


        public VisibleFlag(boolean visible) {
            this.visible = visible;
        }

        public boolean isVisible() {
            return visible;
        }

        public Object clone() throws CloneNotSupportedException {
          VisibleFlag visibeFlag = (VisibleFlag)super.clone();
          visibeFlag.visible=this.visible;
          return visibeFlag;
        }
    }

}
