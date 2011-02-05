package jsesh.mdc.model;

import jsesh.mdc.interfaces.ModifierListInterface;

/**
 * This file is free Software
 * (c) Serge Rosmorduc
 * @author rosmord
 * 
 * Modifiers are quite tricky, because their syntax is not very regular.
 * Basically, there are :
 * 	boolean modifiers, which work as a trigger (e.g. reverse)
 *  integer modifiers, which set an integer value. (e.g. size, rotation)
 * 	
 * available operations are :
 * 
 * including a modifier.
 * removing a modifier
 * getting an iterator to the collection of modifiers for easy inspection ? 
 *  
 * For ease of use :
 * 	get/set operation for boolean modifiers
 *  get/set/unset/existence operations for integer modifiers.
 * 
 * The invariants concerning modifiers are
 * 		for a given type (boolean or integer) of modifier, there is only one value
 * 		with a given name.
 * 
 * Special modifiers :
 * This class deals with some modifiers in a specific way.
 * "\" (signe reverse) can be added, but will be used through "isReversed".
 * all modifiers about angles are supported, but will be likewise dealt with through the "angle" attribute (in degrees). 
 *
 * TODO CLEAN UP The inner structure of this class. 
 */
public class ModifiersList
	extends EmbeddedModelElement
	implements ModifierListInterface {

	private static int getArity(Modifier m) {
		if (m.getValue() == null)
			return 0;
		else
			return 1;
	}

	private int angle;
	private boolean reversed;
	private int scale;
	// scale of the sign, in percentage. 100 means full scale.

	/**
	 * Constructor for ModifiersList.
	 */
	public ModifiersList() {
		angle = 0;
		scale = 100;
		reversed = false;
	}

	/*
	 * @see jsesh.mdc.model.ModelElement#Accept(jsesh.mdc.model.ModelElementVisitor)
	 */
	public void accept(ModelElementVisitor v) {
		v.visitModifierList(this);
	}

	// Method called for "normal" modifiers
	private void addAux(Modifier m) {
		boolean notFound = true;
		Modifier m1 = getModifierFrom(m.getName(), getArity(m));
		if (m1 == null)
			addChild(m);
		else if (getArity(m) == 1)
			m1.setValue(m.getValue());
	}
	//TODO : choose what to do with "parent" and "modifiers".
	
	/* (non-Javadoc)
	 * @see jsesh.mdc.model.ModelElement#buildTopItem()
	 */
	public TopItem buildTopItem() {
		return null;
	}

	/* (non-Javadoc)
	 * @see jsesh.mdc.model.ModelElement#compareToAux(jsesh.mdc.model.ModelElement)
	 */
	public int compareToAux(ModelElement e) {
		ModifiersList l=(ModifiersList) e;
		int result= angle- l.angle;
		if (result== 0) {
			result= (reversed?1:0) - (l.reversed?1:0);
			if (result==0)
			{
				result= scale - l.scale;
				if (result == 0) {
					result= compareContents(l);
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see jsesh.mdc.model.ModelElement#deepCopy()
	 */
	public ModelElement deepCopy() {
		ModifiersList result= new ModifiersList();
		result.angle= angle;
		result.reversed= reversed;
		result.scale= scale;
		copyContentTo(result);
		return result;
	}

	/**
	 * Method getAngle.
	 * @return int
	 */
	public int getAngle() {
		return angle;
	}

	/**
	* gets the value of the boolean modifier name.
	* if it's defined, it's true, else false.
	* @param name
	* @return boolean
	*/
	public boolean getBoolean(String name) {
		if (name.equals(""))
			return (reversed);
		else {
			Modifier m = getModifierFrom(name, 0);
			return (m != null);
		}
	}

	public int getInteger(String name) throws NoSuchModifierException {
		Modifier m = getModifierFrom(name, 1);
		if (m != null)
			return m.getValue().intValue();
		else
			throw new NoSuchModifierException(name);
	}
	/**
	 * returns the value of integer modifier "name". If no such modifier exists,
	 * return the default value.
	 * @param name
	 * @param defaultValue
	 * @return int
	 */

	public int getIntegerWithDefault(String name, int defaultValue) {
		Modifier m = getModifierFrom(name, 1);
		if (m != null)
			return m.getValue().intValue();
		else
			return defaultValue;
	}

	/**
	 * @param i
	 * @return the ith modifier
	 */
	public Modifier getModifierAt(int i) {
		return (Modifier) getChildAt(i); 
	}

	private Modifier getModifierFrom(String name, int arity) {
		Modifier result = null;
		for (int i = 0; result == null && i < getNumberOfChildren(); i++) {
			Modifier m1 = (Modifier) getChildAt(i);
			if (name.equals(m1.getName())
				&& (arity == -1 || getArity(m1) == arity))
				result = m1;
		}
		return result;
	}

	/**
	 * @return the scale.
	 */
	public int getScale() {
		return scale;
	}
	/**
	 * Returns true if there is a corresponding modifier.
	 * @param name
	 * @return boolean
	 */
	public boolean hasInteger(String name) {
		Modifier m = getModifierFrom(name, 1);
		return m != null;
	}

	/**
	 * adds modifier m to the modifier list. If there is already a modifier 
	 * with the same name and type as m, it will be replaced.
	 * @param m
	 */
	public void includeModifier(Modifier m) {
		if ("".equals(m.getName())) {
			if (m.getValue() == null) {
				// Reversed
				reversed = true;
			} else {
				// Modifier is "\NNN" => scale.
				setScale(m.getValue().intValue());
			}
		} else if (m.getValue() != null) {
			if (m.getName().equals("r")) {
				angle = (360 - m.getValue().intValue() * 90) % 360;
			} else if (m.getName().equals("t")) {
				angle = (360 - m.getValue().intValue() * 90) % 360;
				reversed = true;
			} else if (m.getName().equals("R")) {
				angle = m.getValue().intValue() % 360;
				if (angle < 0)
					angle += 360;
			} else if (m.getName().equals("s")) {
				double scale= 100;
				for (int i= 0; i< m.getValue().intValue(); i++)
					scale= scale/1.4142136; // sqrt(2)
				setScale((int) scale);
			} else
				addAux(m);
		} else {
			addAux(m);
		}
	}

	/**
	 * Method isReversed.
	 * @return boolean
	 */
	public boolean isReversed() {
		return reversed;
	}

	/**
	 * remove modifier m from the modifiers list.
	 * @param m
	 */

	public void removeModifier(Modifier m) {
		if ("".equals(m.getName())) {
			if (m.getValue() == null) {
				// Reversed
				reversed = false;
			} else {
				setScale(100);
			}
		} else if (m.getValue() != null) {
			if (m.getName().equals("r")) {
				angle = 0;
			} else if (m.getName().equals("t")) {
				angle = 0;
				reversed = false;
			} else if (m.getName().equals("R")) {
				angle = 0;
			} else
				removeChild(m);
		} else
			removeChild(m);
	}

	/**
	 * 
	 * @param a
	 */
	public void setAngle(int a) {
		angle = a % 360;
		if (a < 0)
			a += 360;
		notifyModification();
	}

	/**
	 * sets the value of name to value.
	 * @param name
	 * @param value
	 */

	public void setBoolean(String name, boolean value) {
		if (value)
			includeModifier(new Modifier(name, null));
		else
			removeModifier(new Modifier(name, null));
	}

	/**
	* 
	* @param name
	* @param val a <b>positive</b> integer.
	*/
	public void setInteger(String name, int val) {
		includeModifier(new Modifier(name, new Integer(val)));
	}

	/**
	 * Method setReversed.
	 * @param _reversed
	 */
	public void setReversed(boolean _reversed) {
		reversed = _reversed;
		notifyModification();
	}

	/**
	 * @param i
	 */
	public void setScale(int i) {
		scale = i;
		notifyModification();
	}

	/**
	 * Unsets a modifier.
	 * Note that angle and reverse can't be unset. They can be set to 0.
	 * @param name
	 */
	public void unSetInteger(String name) {
		Modifier m = getModifierFrom(name, 1);
		removeChild(m);
	}
}